package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window

class InventorySlotTooltip(_skin: Skin) : Window("", _skin) {
    private val _description = Label("", _skin, "inventory-item-count")

    init {
        this.add(_description)
        this.padLeft(5f).padRight(5f)
        this.pack()
        this.isVisible = false
    }

    fun setVisible(inventorySlot: InventorySlot, visible: Boolean) {
        super.setVisible(visible)

        if (!inventorySlot.hasItem()) {
            super.setVisible(false)
        }
    }

    fun updateDescription(inventorySlot: InventorySlot) {
        if (inventorySlot.hasItem()) {
            val string = StringBuilder()
            string.append(inventorySlot.getTopInventoryItem()!!.itemShortDescription)
            string.append(System.getProperty("line.separator"))
            string.append(String.format("Original Value: %s GP", inventorySlot.getTopInventoryItem()!!.itemValue))
            string.append(System.getProperty("line.separator"))
            string.append(String.format("Trade Value: %s GP", inventorySlot.getTopInventoryItem()!!.getTradeValue()))
            _description.setText(string)
            this.pack()
        } else {
            _description.setText("")
            this.pack()
        }
    }
}

