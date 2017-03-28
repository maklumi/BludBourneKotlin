package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.InventoryItem
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemUseType.*
import com.packtpub.libgdx.bludbourne.InventoryItemFactory
import com.packtpub.libgdx.bludbourne.Utility


class InventoryUI : Window("Inventory", Utility.STATUSUI_SKIN, "solidbackground") {

    private val _lengthSlotRow = 10
    val inventorySlotTable = Table()
    val playerSlotsTable = Table()
    val equipSlots = Table()
    val dragAndDrop = DragAndDrop()
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

        dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(legsSlot))

        playerSlotsTable.background = Image(NinePatch(Utility.STATUSUI_TEXTUREATLAS.createPatch("dialog"))).drawable


        //layout
        for (i in 1..numSlots) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))

            dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

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

    fun addEntityToInventory(entity: Entity, itemName: String) {
        val sourceCells = inventorySlotTable.cells

        for (index in 0..sourceCells.size - 1) {
            val inventorySlot = sourceCells.get(index).actor ?: continue
            inventorySlot as InventorySlot
            val numItems = inventorySlot.getNumItems()
            if (numItems == 0) {
                val inventoryItem = InventoryItemFactory.instance.getInventoryItem(InventoryItem.ItemTypeID.valueOf(entity.entityConfig.itemTypeID))
                inventoryItem.name = itemName
                inventorySlot.add(inventoryItem)
                dragAndDrop.addSource(InventorySlotSource(inventorySlot, dragAndDrop))
                break
            }
        }
    }

    fun removeQuestItemFromInventory(questID: String) {
        val sourceCells = inventorySlotTable.cells
        for (index in 0..sourceCells.size - 1) {
            val inventorySlot = sourceCells.get(index).actor as InventorySlot
            val item = inventorySlot.getTopInventoryItem() ?: continue
            val inventoryItemName = item.name
            if (inventoryItemName != null && inventoryItemName == questID) {
                inventorySlot.clearAllInventoryItems(false)
            }
        }
    }

    companion object {
        val numSlots = 50

        fun clearInventoryItems(targetTable: Table) {
            val cells: Array<Cell<Actor>> = targetTable.cells

            for (i in 0..cells.size - 1) {
                val inventorySlot = cells[i].actor ?: continue
                inventorySlot as InventorySlot
                inventorySlot.clearAllInventoryItems(false)
            }
        }

        fun removeInventoryItems(name: String, inventoryTable: Table): Array<InventoryItemLocation> {
            val cells: Array<Cell<Actor>> = inventoryTable.cells
            val items = Array<InventoryItemLocation>()
            for (i in 0..cells.size - 1) {
                val inventorySlot = cells.get(i).actor ?: continue
                inventorySlot as InventorySlot
                inventorySlot.removeAllInventoryItemsWithName(name)
            }
            return items
        }

        fun populateInventory(targetTable: Table, inventoryItems: Array<InventoryItemLocation>, dragAndDrop: DragAndDrop) {
            clearInventoryItems(targetTable)

            val cells: Array<Cell<Actor>> = targetTable.cells

            (0..cells.size - 1).forEach { i ->
                val inventorySlot = cells[i].actor as InventorySlot
                inventorySlot.clearAllInventoryItems(true)
            }

            for (i in 0..inventoryItems.size - 1) {
                val itemLocation = inventoryItems[i]
                val itemTypeId = InventoryItem.ItemTypeID.valueOf(itemLocation.itemTypeAtLocation)

                val inventorySlot = cells[itemLocation.locationIndex].actor as InventorySlot

                for (index in 0..itemLocation.numberItemsAtLocation - 1) {
                    val item = InventoryItemFactory.instance.getInventoryItem(itemTypeId)
                    if (item.name == null) {
                        item.name = targetTable.name
                    }
                    inventorySlot.add(item)
                    dragAndDrop.addSource(InventorySlotSource(inventorySlot, dragAndDrop))
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

        fun getInventory(targetTable: Table, name: String): Array<InventoryItemLocation> {
            val cells: Array<Cell<Actor>> = targetTable.cells
            val items: Array<InventoryItemLocation> = Array()
            for (i in 0..cells.size - 1) {
                val inventorySlot = cells[i].actor ?: continue
                inventorySlot as InventorySlot
                val numItems = inventorySlot.getNumItems(name)
                if (numItems > 0) {
                    items.add(InventoryItemLocation(i,
                            inventorySlot.getTopInventoryItem()!!.itemTypeID.toString(),
                            numItems))
                }
            }
            return items
        }

        fun getInventory(sourceTable: Table, targetTable: Table, name: String): Array<InventoryItemLocation> {
            val items: Array<InventoryItemLocation> = getInventory(targetTable, name)
            val sourceCells: Array<Cell<Actor>> = sourceTable.cells
            var counter = 0
            for (item in items) {
                for (index in 0..sourceCells.size - 1) {
                    counter = index
                    val inventorySlot = sourceCells[index].actor ?: continue
                    inventorySlot as InventorySlot
                    val numItems = inventorySlot.getNumItems(name)
                    if (numItems == 0) {
                        item.locationIndex = index
//                        println("[index]: $index itemtype: ${item.itemTypeAtLocation} numItems: $numItems")
                        counter++
                        break
                    }
                }
                // If we run out of room when buying items and still left items to be sold,
                // then we will stack remaining items in the last slot.
                if (counter == sourceCells.size) {
                    item.locationIndex = counter - 1
                }
            }
            return items
        }

        fun setInventoryItemNames(targetTable: Table, name: String) {
            val cells: Array<Cell<Actor>> = targetTable.cells
            for (i in 0..cells.size - 1) {
                val inventorySlot = cells[i].actor ?: continue
                inventorySlot as InventorySlot
                inventorySlot.updateAllInventoryItemNames(name)
            }
        }
    }

}
