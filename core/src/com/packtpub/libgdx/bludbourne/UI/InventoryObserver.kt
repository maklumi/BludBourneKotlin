package com.packtpub.libgdx.bludbourne.UI

interface InventoryObserver {
    enum class InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
        ITEM_CONSUMED,
        ADD_WAND_AP,
        REMOVE_WAND_AP,
        NONE
    }

    fun onNotify(value: String, event: InventoryEvent)
}
