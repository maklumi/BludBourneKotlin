package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image

class InventoryItem : Image {

    enum class ItemAttribute constructor(val value: Int) {
        CONSUMABLE(1),
        EQUIPPABLE(2),
        STACKABLE(4)

    }

    enum class ItemUseType constructor(val value: Int) {
        ITEM_RESTORE_HEALTH(1),
        ITEM_RESTORE_MP(2),
        ITEM_DAMAGE(4),
        WEAPON_ONEHAND(8),
        WEAPON_TWOHAND(16),
        WAND_ONEHAND(32),
        WAND_TWOHAND(64),
        ARMOR_SHIELD(128),
        ARMOR_HELMET(256),
        ARMOR_CHEST(512),
        ARMOR_FEET(1024)
    }

    enum class ItemTypeID {
        ARMOR01, ARMOR02, ARMOR03, ARMOR04, ARMOR05,
        BOOTS01, BOOTS02, BOOTS03, BOOTS04, BOOTS05,
        HELMET01, HELMET02, HELMET03, HELMET04, HELMET05,
        SHIELD01, SHIELD02, SHIELD03, SHIELD04, SHIELD05,
        WANDS01, WANDS02, WANDS03, WANDS04, WANDS05,
        WEAPON01, WEAPON02, WEAPON03, WEAPON04, WEAPON05,
        POTIONS01, POTIONS02, POTIONS03,
        SCROLL01, SCROLL02, SCROLL03
    }

    var itemAttributes: Int = 0
    var itemUseType: Int = 0
    var itemTypeID: ItemTypeID? = null
    var itemShortDescription: String? = null


    constructor(textureRegion: TextureRegion, itemAttributes: Int,
                itemTypeID: ItemTypeID, itemUseType: Int) : super(textureRegion) {

        this.itemTypeID = itemTypeID
        this.itemAttributes = itemAttributes
        this.itemUseType = itemUseType
    }

    constructor() : super()

    constructor(inventoryItem: InventoryItem) : super() {
        this.itemTypeID = inventoryItem.itemTypeID
        this.itemAttributes = inventoryItem.itemAttributes
        this.itemUseType = inventoryItem.itemUseType
        this.itemShortDescription = inventoryItem.itemShortDescription
    }

    val isStackable: Boolean
        get() = itemAttributes and ItemAttribute.STACKABLE.value ==
                ItemAttribute.STACKABLE.value

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemTypeID == candidateInventoryItem.itemTypeID
    }
}
