package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Scaling

class InventoryUI(skin: Skin, textureAtlas: TextureAtlas) : Window("Inventory", skin, "solidbackground") {

    private val _numSlots = 50
    private val _lengthSlotRow = 10
    private val _dragAndDrop = DragAndDrop()
    private val _slotWidth = 52
    private val _slotHeight = 52

    init {
        //create
        val inventorySlotTable = Table()
        val playerSlotsTable = Table()
        val equipSlots = Table()
        equipSlots.defaults().space(10f)

        val headSlot = InventorySlot(
                InventoryItem.ItemType.ARMOR_HELMET.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_helmet")))

        val leftArmSlot = InventorySlot(
                InventoryItem.ItemType.WEAPON_ONEHAND.value or
                        InventoryItem.ItemType.WEAPON_TWOHAND.value or
                        InventoryItem.ItemType.ARMOR_SHIELD.value or
                        InventoryItem.ItemType.WAND_ONEHAND.value or
                        InventoryItem.ItemType.WAND_TWOHAND.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_weapon"))
        )

        val rightArmSlot = InventorySlot(
                InventoryItem.ItemType.WEAPON_ONEHAND.value or
                        InventoryItem.ItemType.WEAPON_TWOHAND.value or
                        InventoryItem.ItemType.ARMOR_SHIELD.value or
                        InventoryItem.ItemType.WAND_ONEHAND.value or
                        InventoryItem.ItemType.WAND_TWOHAND.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_shield"))

        )

        val chestSlot = InventorySlot(
                InventoryItem.ItemType.ARMOR_CHEST.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_chest")))
        
        val legsSlot = InventorySlot(
                InventoryItem.ItemType.ARMOR_FEET.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_boot")))

        _dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(legsSlot))

        playerSlotsTable.background = Image(NinePatch(textureAtlas.createPatch("dialog"))).drawable


        //layout
        for (i in 1.._numSlots) {
            val inventorySlot = InventorySlot()
            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            if (i == 5 || i == 10 || i == 15 || i == 20) {
                //TEMP TODO
                val inventorySlotItem = InventoryItem(
                        PlayerHUD.itemsTextureAtlas.findRegion("armor01"),
                        InventoryItem.ItemAttribute.WEARABLE.value,
                        "armor01",
                        InventoryItem.ItemType.ARMOR_CHEST.value)

                inventorySlotItem.setScaling(Scaling.none)
                inventorySlot.add(inventorySlotItem)

                _dragAndDrop.addSource(InventorySlotSource(inventorySlot, _dragAndDrop))
            } else if (i == 1 || i == 13 || i == 25 || i == 30) {
                //TEMP TODO
                val inventorySlotItem = InventoryItem(
                        PlayerHUD.itemsTextureAtlas.findRegion("potions02"),
                        InventoryItem.ItemAttribute.CONSUMABLE.value or InventoryItem.ItemAttribute.STACKABLE.value,
                        "potions02",
                        InventoryItem.ItemType.RESTORE_MP.value)

                inventorySlotItem.setScaling(Scaling.none)
                inventorySlot.add(inventorySlotItem)

                _dragAndDrop.addSource(InventorySlotSource(inventorySlot, _dragAndDrop))
            }

            inventorySlotTable.add(inventorySlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

//            inventorySlotTable.add(image)
            if (i % _lengthSlotRow == 0) inventorySlotTable.row()
        }

        equipSlots.add()
        equipSlots.add(headSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        equipSlots.row()

        equipSlots.add(leftArmSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        equipSlots.add(chestSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        equipSlots.add(rightArmSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        equipSlots.row()

        equipSlots.add()
        equipSlots.right().add(legsSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

        playerSlotsTable.add(equipSlots)

        this.add(playerSlotsTable).padBottom(20f).row()
        this.add(inventorySlotTable).row()
        this.pack()
    }
}
