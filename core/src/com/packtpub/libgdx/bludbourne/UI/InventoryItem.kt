package com.packtpub.libgdx.bludbourne.UI


import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image

class InventoryItem(textureRegion: TextureRegion,
                    var itemAttributes: Int,
                    var itemID: String) :
        Image(textureRegion) {

    companion object {
        val CONSUMABLE = 0x01
        val WEARABLE = 0x02
        val STACKABLE = 0x04
    }

    fun isStackable(): Boolean {
        return itemAttributes and InventoryItem.STACKABLE == InventoryItem.STACKABLE
    }

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemID.equals(candidateInventoryItem.itemID, ignoreCase = true)
    }

}
