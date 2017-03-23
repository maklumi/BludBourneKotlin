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
            _description.setText(inventorySlot.getTopInventoryItem()!!.itemShortDescription)
            this.pack()
        } else {
            _description.setText("")
            this.pack()
        }
    }
}

