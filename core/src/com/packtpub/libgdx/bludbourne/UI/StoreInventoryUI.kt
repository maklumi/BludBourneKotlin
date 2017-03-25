package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Utility

class StoreInventoryUI : Window("Store Inventory", Utility.STATUSUI_SKIN, "solidbackground"),
        InventorySlotObserver {

    private val _numStoreInventorySlots = 30
    private val _lengthSlotRow = 10
    val inventorySlotTable: Table
    private val _playerInventorySlotTable: Table
    private val _dragAndDrop: DragAndDrop
    val inventoryActors: Array<Actor>

    private val _slotWidth = 52
    private val _slotHeight = 52

    private val _inventorySlotTooltip: InventorySlotTooltip

    private val _sellTotalLabel: Label
    private val _buyTotalLabel: Label

    private var _tradeInVal = 0
    private var _fullValue = 0

    private val _sellButton: Button
    private val _buyButton: Button

    private val _buttons: Table
    private val _totalLabels: Table

    init {

        this.setFillParent(true)

        //create
        _dragAndDrop = DragAndDrop()
        inventoryActors = Array<Actor>()
        inventorySlotTable = Table()
        inventorySlotTable.name = STORE_INVENTORY

        _playerInventorySlotTable = Table()
        _playerInventorySlotTable.name = PLAYER_INVENTORY
        _inventorySlotTooltip = InventorySlotTooltip(Utility.STATUSUI_SKIN)

        _sellButton = TextButton(SELL, Utility.STATUSUI_SKIN, "inventory")
        _sellButton.isDisabled = true
        _sellButton.touchable = Touchable.disabled

        _sellTotalLabel = Label(SELL + " : " + _tradeInVal + GP, Utility.STATUSUI_SKIN)
        _sellTotalLabel.setAlignment(Align.center)
        _buyTotalLabel = Label(BUY + " : " + _fullValue + GP, Utility.STATUSUI_SKIN)
        _buyTotalLabel.setAlignment(Align.center)

        _buyButton = TextButton(BUY, Utility.STATUSUI_SKIN, "inventory")
        _buyButton.isDisabled = true
        _buyButton.touchable = Touchable.disabled

        _buttons = Table()
        _buttons.defaults().expand().fill()
        _buttons.add(_sellButton).padLeft(10f).padRight(10f)
        _buttons.add(_buyButton).padLeft(10f).padRight(10f)

        _totalLabels = Table()
        _totalLabels.defaults().expand().fill()
        _totalLabels.add(_sellTotalLabel).padLeft(40f)
        _totalLabels.add()
        _totalLabels.add(_buyTotalLabel).padRight(40f)

        //layout
        for (i in 1.._numStoreInventorySlots) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
            inventorySlot.addObserver(this)
            inventorySlot.name = STORE_INVENTORY

            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            inventorySlotTable.add(inventorySlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

            if (i % _lengthSlotRow == 0) {
                inventorySlotTable.row()
            }
        }

        for (i in 1..InventoryUI.numSlots) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(_inventorySlotTooltip))
            inventorySlot.addObserver(this)
            inventorySlot.name = PLAYER_INVENTORY

            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            _playerInventorySlotTable.add(inventorySlot).size(_slotWidth.toFloat(), _slotHeight.toFloat())

            if (i % _lengthSlotRow == 0) {
                _playerInventorySlotTable.row()
            }
        }

        inventoryActors.add(_inventorySlotTooltip)

        //this.debugAll();
        this.defaults().expand().fill()
        this.add(inventorySlotTable).pad(10f, 10f, 10f, 10f)
        this.row()
        this.add(_buttons)
        this.row()
        this.add(_totalLabels)
        this.row()
        this.add(_playerInventorySlotTable).pad(10f, 10f, 10f, 10f)
        this.pack()
    }

    fun loadPlayerInventory(playerInventoryItems: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(_playerInventorySlotTable, playerInventoryItems, _dragAndDrop)
    }

    fun loadStoreInventory(storeInventoryItems: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(inventorySlotTable, storeInventoryItems, _dragAndDrop)
    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            InventorySlotObserver.SlotEvent.ADDED_ITEM -> {
                if (slot.getTopInventoryItem()!!.name.equals(PLAYER_INVENTORY, ignoreCase = true) && slot.name.equals(STORE_INVENTORY, ignoreCase = true)) {
                    _tradeInVal += slot.getTopInventoryItem()!!.getTradeValue()
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP)
                    if (_tradeInVal > 0) {
                        _sellButton.isDisabled = false
                        _sellButton.touchable = Touchable.enabled
                    }
                }

                if (slot.getTopInventoryItem()!!.name.equals(STORE_INVENTORY, ignoreCase = true) && slot.name.equals(PLAYER_INVENTORY, ignoreCase = true)) {
                    _fullValue += slot.getTopInventoryItem()!!.itemValue
                    _buyTotalLabel.setText(BUY + " : " + _fullValue + GP)
                    if (_fullValue > 0) {
                        _buyButton.isDisabled = false
                        _buyButton.touchable = Touchable.enabled
                    }
                }
            }
            InventorySlotObserver.SlotEvent.REMOVED_ITEM -> {
                if (slot.getTopInventoryItem()!!.name.equals(PLAYER_INVENTORY, ignoreCase = true) && slot.name.equals(STORE_INVENTORY, ignoreCase = true)) {
                    _tradeInVal -= slot.getTopInventoryItem()!!.getTradeValue()
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP)
                    if (_tradeInVal <= 0) {
                        _sellButton.isDisabled = true
                        _sellButton.touchable = Touchable.disabled
                    }
                }
                if (slot.getTopInventoryItem()!!.name.equals(STORE_INVENTORY, ignoreCase = true) && slot.name.equals(PLAYER_INVENTORY, ignoreCase = true)) {
                    _fullValue -= slot.getTopInventoryItem()!!.itemValue
                    _buyTotalLabel.setText(BUY + " : " + _fullValue + GP)
                    if (_fullValue <= 0) {
                        _buyButton.isDisabled = true
                        _buyButton.touchable = Touchable.disabled
                    }
                }
            }
        }
    }

    companion object {

        private val STORE_INVENTORY = "Store_Inventory"
        private val PLAYER_INVENTORY = "Player_Inventory"

        private val SELL = "SELL"
        private val BUY = "BUY"
        private val GP = " GP"
    }
}
