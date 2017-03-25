package com.packtpub.libgdx.bludbourne.UI


interface StoreInventorySubject {
    fun addObserver(storeObserver: StoreInventoryObserver)
    fun removeObserver(storeObserver: StoreInventoryObserver)
    fun removeAllObservers()
    fun notify(value: String, event: StoreInventoryObserver.StoreInventoryEvent)
}
