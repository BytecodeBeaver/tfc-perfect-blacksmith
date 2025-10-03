package com.bytecodebeaver.tfcperfectblacksmith.mixin;

import com.bytecodebeaver.tfcperfectblacksmith.interfaces.IForgeRuleAccessor;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgeRule.class)
public abstract class ForgeRuleAccessorMixin implements IForgeRuleAccessor {
    @Accessor(value = "type", remap = false)
    public abstract ForgeStep getType();

    @Override
    public ForgeRule getForgeRule() {
        return (ForgeRule)(Object) this;
    }
}