package com.bytecodebeaver.tfcperfectblacksmith.mixin;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgeRule.class)
public interface ForgeRuleAccessor {
    @Accessor(value = "type", remap = false)
    ForgeStep getType();
}
