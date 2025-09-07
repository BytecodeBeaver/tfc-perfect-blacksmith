package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(TfcPerfectBlacksmith.MODID)
public class TfcPerfectBlacksmith {
    public static final String MODID = "tfcperfectblacksmith";
    private static final Logger LOGGER = LogUtils.getLogger();
    /*
    @SubscribeEvent
    private void onLoad() {
        LOGGER.info("TFC Perfect Blacksmith loaded!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {

    }
    */

    public TfcPerfectBlacksmith() {

    }
}
