package com.bytecodebeaver.tfcperfectblacksmith.events;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TFCAnvilWorkEvent extends Event {
    private final AnvilBlockEntity anvilBlockEntity;
    private final IItemHandlerModifiable inventory;
    private final ServerPlayer player;

    private final ForgeStep step;

    public AnvilBlockEntity getAnvilBlockEntity() {
        return this.anvilBlockEntity;
    }

    public TFCAnvilWorkEvent(AnvilBlockEntity anvilBlockEntity, IItemHandlerModifiable inventory, ServerPlayer player, ForgeStep step) {
        this.anvilBlockEntity = anvilBlockEntity;
        this.inventory = inventory;
        this.player = player;
        this.step = step;
    }

    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public ForgeStep getStep() {
        return step;
    }
}
