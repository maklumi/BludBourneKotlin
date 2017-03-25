package com.packtpub.libgdx.bludbourne.UI

interface InventorySlotObserver {
    enum class SlotEvent {
        ADDED_ITEM,
        REMOVED_ITEM
    }

    fun onNotify(slot: InventorySlot, event: SlotEvent)
}
