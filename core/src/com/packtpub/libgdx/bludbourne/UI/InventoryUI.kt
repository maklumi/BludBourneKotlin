package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.InventoryItem
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemUseType.*
import com.packtpub.libgdx.bludbourne.InventoryItemFactory

class InventoryUI(skin: Skin, textureAtlas: TextureAtlas) : Window("Inventory", skin, "solidbackground") {

    private val _numSlots = 50
    private val _lengthSlotRow = 10
    private val _inventorySlotTable: Table
    private val _playerSlotsTable: Table
    private val _equipSlots: Table
    private val _dragAndDrop = DragAndDrop()
    private val _slotWidth = 52
    private val _slotHeight = 52

    init {
        //create
        _inventorySlotTable = Table()
        _playerSlotsTable = Table()
        _equipSlots = Table()
        _equipSlots.defaults().space(10f)

        val headSlot = InventorySlot(
                ARMOR_HELMET.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_helmet")))

        val leftArmSlot = InventorySlot(
                WEAPON_ONEHAND.value or
                        WEAPON_TWOHAND.value or
                        ARMOR_SHIELD.value or
                        WAND_ONEHAND.value or
                        WAND_TWOHAND.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_weapon"))
        )

        val rightArmSlot = InventorySlot(
                WEAPON_ONEHAND.value or
                        WEAPON_TWOHAND.value or
                        ARMOR_SHIELD.value or
                        WAND_ONEHAND.value or
                        WAND_TWOHAND.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_shield"))

        )

        val chestSlot = InventorySlot(
                ARMOR_CHEST.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_chest")))

        val legsSlot = InventorySlot(
                ARMOR_FEET.value,
                Image(PlayerHUD.itemsTextureAtlas.findRegion("inv_boot")))

        _dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(legsSlot))

        _playerSlotsTable.background = Image(NinePatch(textureAtlas.createPatch("dialog"))).drawable


        //layout
        for (i in 1.._numSlots) {
            val inventorySlot = InventorySlot()
            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            _inventorySlotTable.add(inventorySlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

            if (i % _lengthSlotRow == 0) _inventorySlotTable.row()
        }

        _equipSlots.add()
        _equipSlots.add(headSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        _equipSlots.row()

        _equipSlots.add(leftArmSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        _equipSlots.add(chestSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        _equipSlots.add(rightArmSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())
        _equipSlots.row()

        _equipSlots.add()
        _equipSlots.right().add(legsSlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

        _playerSlotsTable.add(_equipSlots)

        this.add(_playerSlotsTable).padBottom(20f).row()
        this.add(_inventorySlotTable).row()
        this.pack()
    }

    fun populateInventory(itemTypeIDs: Array<InventoryItem.ItemTypeID>) {
        val cells: Array<Cell<Actor>> = _inventorySlotTable.cells
        for (i in 0..itemTypeIDs.size - 1) {
            val inventorySlot = cells.get(i).actor as InventorySlot
            inventorySlot.add(InventoryItemFactory.instance.getInventoryItem(itemTypeIDs.get(i)))
            _dragAndDrop.addSource(InventorySlotSource(inventorySlot, _dragAndDrop))
        }
    }
}
