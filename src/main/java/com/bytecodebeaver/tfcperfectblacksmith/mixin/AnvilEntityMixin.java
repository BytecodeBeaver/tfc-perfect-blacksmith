package com.bytecodebeaver.tfcperfectblacksmith.mixin;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlockEntity.class)
public class AnvilEntityMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "work", at = @At("HEAD"), remap = false)
    private void workHook(ServerPlayer player, ForgeStep step, CallbackInfoReturnable<InteractionResult> cir) {
        LOGGER.info("Work detected!");

        player.displayClientMessage(
                Component.literal(
                        Integer.toString(step.step())
                ), false
        );
    }
}
