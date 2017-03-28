package com.packtpub.libgdx.bludbourne.UI

class InventoryItemLocation(var locationIndex: Int,
                            var itemTypeAtLocation: String,
                            var numberItemsAtLocation: Int,
                            var itemNameProperty: String) {
    constructor(): this (0, "", 0, "")
}