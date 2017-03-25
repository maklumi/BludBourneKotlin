package com.packtpub.libgdx.bludbourne.UI

interface InventorySlotSubject {

    fun addObserver(inventorySlotObserver: InventorySlotObserver)
    fun removeObserver(inventorySlotObserver: InventorySlotObserver)
    fun removeAllObservers()
    fun notify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent)
}
