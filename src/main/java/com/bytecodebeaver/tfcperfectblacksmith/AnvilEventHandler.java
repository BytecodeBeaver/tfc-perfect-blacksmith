package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.logging.LogUtils;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilOpenedEvent;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilWorkEvent;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.slf4j.Logger;

import java.util.Queue;

@Mod.EventBusSubscriber(modid = TfcPerfectBlacksmith.MODID)
public class AnvilEventHandler {
    private Queue<ForgeStep> optimalSteps;

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
    public void onAnvilWork(TFCAnvilWorkEvent anvilWorkEvent) {
        // If action was matching, It's fine. But If user issued wrong action, then we need to find new optimal steps
        /*
        ForgeStep nextOptimalStep = optimalSteps.peek();
        if(anvilWorkEvent.getStep() != nextOptimalStep) {
            // User made a mistake - We need to create new set of optimal actions

        }*/



        Level level = anvilWorkEvent.getAnvilBlockEntity().getLevel();
        var inventory = anvilWorkEvent.getInventory();

        assert level != null;

        final ItemStack stack = inventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN);
        if (!stack.isEmpty()) {
            final Forging forge = ForgingCapability.get(stack);
            LOGGER.debug("Work after anvil: {}", forge.getWork() + anvilWorkEvent.getStep().step());
        }
    }

    @SubscribeEvent
    public void onAnvilOpen(TFCAnvilOpenedEvent event) {
        AnvilRecipe currentAnvilRecipe = getCurrentAnvilRecipe(
                event.getAnvilBlockEntity(),
                event.getInventory()
        );
        if(currentAnvilRecipe == null) return;

        // TODO: Probably Forge.getWorkTarget() is the intended way
        int target = currentAnvilRecipe.computeTarget((AnvilBlockEntity.AnvilInventory)event.getInventory());
        LOGGER.debug("Opened anvil and computed target value of: {}", target);

        // Load optimal steps
        AnvilWorkStepSolver anvilWorkStepSolver = new AnvilWorkStepSolver();
        optimalSteps = anvilWorkStepSolver.solve(
                target, currentAnvilRecipe.getRules()
        );
    }
}
