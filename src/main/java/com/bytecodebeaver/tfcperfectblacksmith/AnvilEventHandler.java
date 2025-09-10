package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.logging.LogUtils;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilClosedEvent;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilOpenedEvent;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilWorkEvent;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = TfcPerfectBlacksmith.MODID)
public class AnvilEventHandler {
    private Queue<ForgeStep> optimalSteps = null;
    private Iterator<ForgeStep> optimalStepsIterator;
    private AnvilWorkStepSolver anvilWorkStepSolver = new AnvilWorkStepSolver();

    private static final Logger LOGGER = LogUtils.getLogger();

    private AnvilRecipe getCurrentAnvilRecipe(AnvilBlockEntity anvilBlockEntity, IItemHandlerModifiable inventory) {
        Level level = anvilBlockEntity.getLevel();

        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN);
        if (!stack.isEmpty()) {
            final Forging forge = ForgingCapability.get(stack);
            if (forge != null) {
                return forge.getRecipe(level);
            }
        }
        return null;
    }

    @SubscribeEvent
    public void postAnvilWork(TFCAnvilWorkEvent.Post anvilWorkEvent) {
        // Do nothing if for some reason interaction failed
        if(anvilWorkEvent.getInteractionResult() == InteractionResult.FAIL) return;

        // If action was matching, It's fine. But If user issued wrong action, then we need to find new optimal steps
        if(optimalSteps.isEmpty()) return;

        ForgeStep nextOptimalStep = optimalSteps.peek();
        if(anvilWorkEvent.getStep().equals(nextOptimalStep)) {
            optimalSteps.remove();
        } else {
            // User made a mistake - We need to create new set of optimal actions as old one is invalid
            optimalSteps = getOptimalAnvilSteps(
                    anvilWorkEvent.getAnvilBlockEntity(),
                    anvilWorkEvent.getInventory()
            );
        }
        displayNextOptimalStep();
    }

    private Queue<ForgeStep> getOptimalAnvilSteps(AnvilBlockEntity anvil, IItemHandlerModifiable anvilInventory) {
        AnvilRecipe currentAnvilRecipe = getCurrentAnvilRecipe(
                anvil,
                anvilInventory
        );
        if(currentAnvilRecipe == null) return new LinkedList<>();

        final ItemStack stack = anvilInventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN);
        if (stack.isEmpty()) return new LinkedList<>();

        final Forging forge = ForgingCapability.get(stack);
        if(forge == null) return new LinkedList<>();

        int target = forge.getWorkTarget();

        return new LinkedList<>(anvilWorkStepSolver.solve(
                target, forge.getWork(), currentAnvilRecipe.getRules()
        ));
    }

    private void displayNextOptimalStep() {
        if(optimalSteps.isEmpty()) return;

        ForgeStep nextOptimalStep = optimalSteps.peek();
        if(nextOptimalStep == null) return;

        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal(nextOptimalStep.name())
        );
    }

    @SubscribeEvent
    public void onAnvilOpen(TFCAnvilOpenedEvent event) {
        // Load optimal steps for currently opened anvil - Might be different than the last time
        optimalSteps = getOptimalAnvilSteps(
                event.getAnvilBlockEntity(),
                event.getInventory()
        );
        /*
        StringBuilder builder = new StringBuilder();
        for(ForgeStep step : optimalSteps) {
            builder.append(step.name());
        }

        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal(builder.toString())
        );
        */
        displayNextOptimalStep();
    }
}
