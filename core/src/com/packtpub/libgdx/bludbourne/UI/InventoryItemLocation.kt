package com.packtpub.libgdx.bludbourne.UI

class InventoryItemLocation {
    var locationIndex: Int = 0
    var itemTypeAtLocation: String = ""
    var numberItemsAtLocation: Int = 0

    constructor() {}

    constructor(locationIndex: Int, itemTypeAtLocation: String, numberItemsAtLocation: Int) {
        this.locationIndex = locationIndex
        this.itemTypeAtLocation = itemTypeAtLocation
        this.numberItemsAtLocation = numberItemsAtLocation
    }
}