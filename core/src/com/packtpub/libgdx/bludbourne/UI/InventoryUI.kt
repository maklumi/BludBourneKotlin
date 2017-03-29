package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.InventoryItem
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemUseType.*
import com.packtpub.libgdx.bludbourne.InventoryItemFactory
import com.packtpub.libgdx.bludbourne.Utility


class InventoryUI : Window("Inventory", Utility.STATUSUI_SKIN, "solidbackground"), InventorySlotObserver {

    private val _lengthSlotRow = 10
    val inventorySlotTable = Table()
    val playerSlotsTable = Table()
    val equipSlots = Table()
    val dragAndDrop = DragAndDrop()
    val inventoryActors = Array<Actor>()
    private val _inventorySlotTooltip = InventorySlotTooltip(Utility.STATUSUI_SKIN)

    private val _DPValLabel: Label
    private var _DPVal = 0
    private val _APValLabel: Label
    private var _APVal = 0

    private val _slotWidth = 52
    private val _slotHeight = 52

    init {
        //create
        inventorySlotTable.name = "Inventory_Slot_Table"
        equipSlots.name = "Equipment_Slot_Table"
        equipSlots.defaults().space(10f)

        val DPLabel = Label("Defense: ", Utility.STATUSUI_SKIN)
        _DPValLabel = Label(_DPVal.toString(), Utility.STATUSUI_SKIN)

        val APLabel = Label("Attack : ", Utility.STATUSUI_SKIN)
        _APValLabel = Label(_APVal.toString(), Utility.STATUSUI_SKIN)

        val labelTable = Table()
        labelTable.add<Actor>(DPLabel).align(Align.left)
        labelTable.add<Actor>(_DPValLabel).align(Align.center)
        labelTable.row()
        labelTable.row()
        labelTable.add<Actor>(APLabel).align(Align.left)
        labelTable.add<Actor>(_APValLabel).align(Align.center)

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

        headSlot.addObserver(this)
        leftArmSlot.addObserver(this)
        rightArmSlot.addObserver(this)
        chestSlot.addObserver(this)
        legsSlot.addObserver(this)

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

        this.add(playerSlotsTable).padBottom(20f)
        this.add(labelTable)
        this.row()
        this.add(inventorySlotTable).colspan(2)
        this.row()
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

    fun doesInventoryHaveSpace(): Boolean {
        val sourceCells = inventorySlotTable.cells
        var index = 0

        while (index < sourceCells.size) {
            val inventorySlot = sourceCells.get(index).actor
            if (inventorySlot == null) {
                index++
                continue
            }
            val numItems = (inventorySlot as InventorySlot).getNumItems()
            if (numItems == 0) {
                return true
            } else {
                index++
            }
            index++
        }
        return false
    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            InventorySlotObserver.SlotEvent.ADDED_ITEM -> {
                val addItem = slot.getTopInventoryItem() ?: return
                if (addItem.isInventoryItemOffensive()) {
                    _APVal += addItem.itemUseTypeValue
                    _APValLabel.setText(_APVal.toString())
                } else if (addItem.isInventoryItemDefensive()) {
                    _DPVal += addItem.itemUseTypeValue
                    _DPValLabel.setText(_DPVal.toString())
                }
            }
            InventorySlotObserver.SlotEvent.REMOVED_ITEM -> {
                val removeItem = slot.getTopInventoryItem() ?: return
                if (removeItem.isInventoryItemOffensive()) {
                    _APVal -= removeItem.itemUseTypeValue
                    _APValLabel.setText(_APVal.toString())
                } else if (removeItem.isInventoryItemDefensive()) {
                    _DPVal -= removeItem.itemUseTypeValue
                    _DPValLabel.setText(_DPVal.toString())
                }
            }
            else -> {
            }
        }
    }


    companion object {
        val numSlots = 50
        val PLAYER_INVENTORY = "Player_Inventory"
        val STORE_INVENTORY = "Store_Inventory"

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

        fun populateInventory(targetTable: Table, inventoryItems: Array<InventoryItemLocation>, dragAndDrop: DragAndDrop, defaultName: String, disableNonDefaultItems: Boolean) {
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
                    val itemName = itemLocation.itemNameProperty
                    if (itemName == null || itemName.isEmpty()) {
                        item.name = defaultName
                    } else {
                        item.name = itemName
                    }

                    inventorySlot.add(item)
                    if (item.name.equals(defaultName, true)) {
                        dragAndDrop.addSource(InventorySlotSource(inventorySlot, dragAndDrop))
                    } else if (!disableNonDefaultItems) {
                        dragAndDrop.addSource(InventorySlotSource(inventorySlot, dragAndDrop))
                    }
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
                            numItems,
                            inventorySlot.getTopInventoryItem()!!.name))
                }
            }
            return items
        }

        fun getInventoryFiltered(targetTable: Table, filterOutName: String): Array<InventoryItemLocation> {
            val cells = targetTable.cells
            val items = Array<InventoryItemLocation>()
            for (i in 0..cells.size - 1) {
                val inventorySlot = cells.get(i).actor as InventorySlot
                val numItems = inventorySlot.getNumItems()
                if (numItems > 0) {
                    val topItemName = inventorySlot.getTopInventoryItem()!!.name
                    if (topItemName.equals(filterOutName, ignoreCase = true)) continue
                    //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                    items.add(InventoryItemLocation(
                            i,
                            inventorySlot.getTopInventoryItem()!!.itemTypeID!!.toString(),
                            numItems,
                            inventorySlot.getTopInventoryItem()!!.name))
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
                            numItems,
                            name))
                }
            }
            return items
        }

        fun getInventoryFiltered(sourceTable: Table, targetTable: Table, filterOutName: String): Array<InventoryItemLocation> {
            val items: Array<InventoryItemLocation> = getInventoryFiltered(targetTable, filterOutName)
            val sourceCells: Array<Cell<Actor>> = sourceTable.cells
            var counter = 0
            for (item in items) {
                for (index in 0..sourceCells.size - 1) {
                    counter = index
                    val inventorySlot = sourceCells[index].actor ?: continue
                    inventorySlot as InventorySlot
                    val numItems = inventorySlot.getNumItems()
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
