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
import com.packtpub.libgdx.bludbourne.Utility

class InventoryUI() : Window("Inventory", Utility.STATUSUI_SKIN, "solidbackground") {

    private val _numSlots = 50
    private val _lengthSlotRow = 10
    val inventorySlotTable = Table()
    val playerSlotsTable = Table()
    val equipSlots = Table()
    private val _dragAndDrop = DragAndDrop()
    val inventoryActors = Array<Actor>()
    private val _inventorySlotTooltip = InventorySlotTooltip(Utility.STATUSUI_SKIN)

    private val _slotWidth = 52
    private val _slotHeight = 52

    init {
        //create
        inventorySlotTable.name = "Inventory_Slot_Table"
        equipSlots.name = "Equipment_Slot_Table"
        equipSlots.defaults().space(10f)

        val headSlot = InventorySlot(
                ARMOR_HELMET.value,
                Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_helmet")))

        val leftArmSlot = InventorySlot(
                WEAPON_ONEHAND.value or
                        WEAPON_TWOHAND.value or
                        ARMOR_SHIELD.value or
                        WAND_ONEHAND.value or
                        WAND_TWOHAND.value,
                Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_weapon"))
        )

        val rightArmSlot = InventorySlot(
                WEAPON_ONEHAND.value or
                        WEAPON_TWOHAND.value or
                        ARMOR_SHIELD.value or
                        WAND_ONEHAND.value or
                        WAND_TWOHAND.value,
                Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_shield"))

        )

        val chestSlot = InventorySlot(
                ARMOR_CHEST.value,
                Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_chest")))

        val legsSlot = InventorySlot(
                ARMOR_FEET.value,
                Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_boot")))

        headSlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
        leftArmSlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
        rightArmSlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
        chestSlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
        legsSlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))

        _dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        _dragAndDrop.addTarget(InventorySlotTarget(legsSlot))

        playerSlotsTable.background = Image(NinePatch(Utility.STATUSUI_TEXTUREATLAS.createPatch("dialog"))).drawable


        //layout
        for (i in 1.._numSlots) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))

            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            inventorySlotTable.add(inventorySlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

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
        inventoryActors.add(_inventorySlotTooltip)

        this.add(playerSlotsTable).padBottom(20f).row()
        this.add(inventorySlotTable).row()
        this.pack()
    }

    fun populateInventory(targetTable: Table, inventoryItems: Array<InventoryItemLocation>) {
        val cells: Array<Cell<Actor>> = targetTable.cells
        for (i in 0..inventoryItems.size - 1) {
            val itemLocation = inventoryItems[i]
            val itemTypeId = InventoryItem.ItemTypeID.valueOf(itemLocation.itemTypeAtLocation)

            val inventorySlot = cells[itemLocation.locationIndex].actor as InventorySlot
            inventorySlot.clearAllInventoryItems()

            for (index in 0..itemLocation.numberItemsAtLocation - 1) {
                inventorySlot.add(InventoryItemFactory.instance.getInventoryItem(itemTypeId))
                _dragAndDrop.addSource(InventorySlotSource(inventorySlot, _dragAndDrop))
            }
        }
    }

    fun getInventory(targetTable: Table): Array<InventoryItemLocation> {
        val cells: Array<Cell<Actor>> = targetTable.cells
        val items = Array<InventoryItemLocation>()
        for (i in 0..cells.size - 1) {
            if (cells[i].actor == null) continue
            val inventorySlot = cells[i].actor as InventorySlot
            val numItems = inventorySlot.getNumItems()
            if (numItems > 0) {
                items.add(InventoryItemLocation(i,
                        inventorySlot.getTopInventoryItem()!!.itemTypeID.toString(),
                        numItems))
            }
        }
        return items
    }
}
