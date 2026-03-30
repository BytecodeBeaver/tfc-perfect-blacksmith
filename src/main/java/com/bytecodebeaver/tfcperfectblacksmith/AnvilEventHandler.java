package com.bytecodebeaver.tfcperfectblacksmith;

import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilOpenedEvent;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilWorkEvent;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

import java.util.LinkedList;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = TfcPerfectBlacksmith.MODID)
public class AnvilEventHandler {
    private Queue<ForgeStep> optimalSteps = null;
    private final AnvilWorkStepSolver anvilWorkStepSolver = new AnvilWorkStepSolver();

    private AnvilRecipe getCurrentAnvilRecipe(AnvilBlockEntity anvilBlockEntity, IItemHandler inventory) {
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
        if(optimalSteps == null || optimalSteps.isEmpty()) return;

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
        displayNextOptimalStep(anvilWorkEvent.getPlayer());
    }

    private Queue<ForgeStep> getOptimalAnvilSteps(AnvilBlockEntity anvil, IItemHandler anvilInventory) {
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

    private void sendAnvilStateMessage(ServerPlayer player, AnvilBlockEntity anvil, IItemHandler anvilInventory) {
        final ItemStack stack = anvilInventory.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN);
        if (stack.isEmpty()) {
            player.sendSystemMessage(Component.literal("Anvil is empty."));
            return;
        }

        final Forging forge = ForgingCapability.get(stack);
        if (forge == null) {
            player.sendSystemMessage(Component.literal("Workpiece in anvil: " + stack.getHoverName().getString()));
            return;
        }

        final AnvilRecipe recipe = getCurrentAnvilRecipe(anvil, anvilInventory);
        final String recipeId = recipe != null ? recipe.getId().toString() : "none";
        player.sendSystemMessage(Component.literal(
                "Workpiece: " + stack.getHoverName().getString()
                        + " | Work: " + forge.getWork() + "/" + forge.getWorkTarget()
                        + " | Recipe: " + recipeId
        ));
    }

    @SubscribeEvent
    public void onAnvilRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        final BlockEntity blockEntity = serverLevel.getBlockEntity(event.getPos());
        if (!(blockEntity instanceof AnvilBlockEntity anvilBlockEntity)) return;

        // Read current anvil inventory through capabilities to avoid direct internals.
        anvilBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(anvilInventory -> {
            optimalSteps = getOptimalAnvilSteps(anvilBlockEntity, anvilInventory);
            sendAnvilStateMessage(player, anvilBlockEntity, anvilInventory);
            displayNextOptimalStep(player);
        });
    }

    private void displayNextOptimalStep(Player player) {
        if(player == null || optimalSteps == null || optimalSteps.isEmpty()) return;

        ForgeStep nextOptimalStep = optimalSteps.peek();
        if(nextOptimalStep == null) return;

        player.sendSystemMessage(
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
    }
}
