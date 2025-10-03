package com.bytecodebeaver.tfcperfectblacksmith.interfaces;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

public interface IForgeRuleAccessor {
    ForgeStep getType();
    ForgeRule getForgeRule();
}
