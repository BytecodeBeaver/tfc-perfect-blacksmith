package com.bytecodebeaver.tfcperfectblacksmith.events;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TFCAnvilOpenedEvent extends Event {

    private final AnvilBlockEntity anvilBlockEntity;
    private final IItemHandlerModifiable inventory;

    public AnvilBlockEntity getAnvilBlockEntity() {
        return this.anvilBlockEntity;
    }

    public TFCAnvilOpenedEvent(AnvilBlockEntity anvilBlockEntity, IItemHandlerModifiable inventory) {
        this.anvilBlockEntity = anvilBlockEntity;
        this.inventory = inventory;
    }

    public IItemHandlerModifiable getInventory() {
        return inventory;
    }
}
