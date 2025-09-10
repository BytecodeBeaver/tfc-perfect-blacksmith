package com.bytecodebeaver.tfcperfectblacksmith.mixin;

import com.mojang.logging.LogUtils;
import com.bytecodebeaver.tfcperfectblacksmith.AnvilWorkStepSolver;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilOpenedEvent;
import com.bytecodebeaver.tfcperfectblacksmith.events.TFCAnvilWorkEvent;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Queue;

@Mixin(value = {
        AnvilBlockEntity.class,
        //InventoryBlockEntity.class
})
public abstract class AnvilEntityMixin extends InventoryBlockEntity<AnvilBlockEntity.AnvilInventory> {
    @Shadow(remap = false)
    @Final
    public static int SLOT_INPUT_MAIN;
    @Shadow(remap = false)
    @Final
    public static int SLOT_INPUT_SECOND;
    private static final Logger LOGGER = LogUtils.getLogger();

    public AnvilEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<AnvilBlockEntity.AnvilInventory> inventoryFactory, Component defaultName) {
        super(type, pos, state, inventoryFactory, defaultName);
    }

    @Inject(method = "anvilProvider", at = @At("HEAD"), remap = false)
    private void onAnvilOpen(CallbackInfoReturnable<MenuProvider> cir) {
        // Check if there is forging in progress
        MinecraftForge.EVENT_BUS.post(new TFCAnvilOpenedEvent((AnvilBlockEntity)(Object)this, inventory));
        System.out.println("Anvil opened internally!");
    }


    public void onAnvilClose(ScreenEvent.Closing event) {
        System.out.println("Anvil screen closed!");
    }

    @Inject(method = "work", at = @At("RETURN"), remap = false)
    private void postAnvilWorkHook(ServerPlayer player, ForgeStep step, CallbackInfoReturnable<InteractionResult> cir) {
        MinecraftForge.EVENT_BUS.post(new TFCAnvilWorkEvent((AnvilBlockEntity)(Object)this, inventory, player, step));
    }
}
