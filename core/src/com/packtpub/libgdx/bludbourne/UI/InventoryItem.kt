package com.packtpub.libgdx.bludbourne.UI


import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image

class InventoryItem(textureRegion: TextureRegion, private val _itemAttributes: Int, val itemID: String, val itemType: Int) : Image(textureRegion) {

    enum class ItemAttribute constructor(val value: Int) {
        CONSUMABLE(1),
        WEARABLE(2),
        STACKABLE(4)

    }

    enum class ItemType constructor(val value: Int) {
        RESTORE_HEALTH(1),
        RESTORE_MP(2),
        DAMAGE(4),
        WEAPON_ONEHAND(8),
        WEAPON_TWOHAND(16),
        WAND_ONEHAND(32),
        WAND_TWOHAND(64),
        ARMOR_SHIELD(128),
        ARMOR_HELMET(256),
        ARMOR_CHEST(512),
        ARMOR_FEET(1024)
    }

    val isStackable: Boolean
        get() = _itemAttributes and ItemAttribute.STACKABLE.value == ItemAttribute.STACKABLE.value

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemID.equals(candidateInventoryItem.itemID, ignoreCase = true)
    }
}
