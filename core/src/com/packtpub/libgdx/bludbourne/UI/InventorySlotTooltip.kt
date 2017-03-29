package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.packtpub.libgdx.bludbourne.InventoryItem

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
            val item: InventoryItem = inventorySlot.getTopInventoryItem() ?: InventoryItem()
            string.append(item.itemShortDescription)
            if (item.isInventoryItemOffensive()) {
                string.append(System.getProperty("line.separator"))
                string.append(String.format("Attack Points: %s", item.itemUseTypeValue))
            } else if (item.isInventoryItemDefensive()) {
                string.append(System.getProperty("line.separator"));
                string.append(String.format("Defense Points: %s", item.itemUseTypeValue))
            }
            string.append(System.getProperty("line.separator"))
            string.append(String.format("Original Value: %s GP", item.itemValue))
            string.append(System.getProperty("line.separator"))
            string.append(String.format("Trade Value: %s GP", item.getTradeValue()))
            _description.setText(string)
            this.pack()
        } else {
            _description.setText("")
            this.pack()
        }
    }
}

