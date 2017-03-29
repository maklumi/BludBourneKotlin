package com.packtpub.libgdx.bludbourne.UI

interface InventoryObserver {
    enum class InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
        NONE
    }

    fun onNotify(value: String, event: InventoryEvent)
}
