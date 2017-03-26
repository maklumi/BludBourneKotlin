package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.UI.StoreInventoryObserver.StoreInventoryEvent
import com.packtpub.libgdx.bludbourne.Utility

class StoreInventoryUI : Window("Store Inventory", Utility.STATUSUI_SKIN, "solidbackground"),
        InventorySlotObserver, StoreInventorySubject {

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
    private val _playerTotalGP: Label

    private var _tradeInVal = 0
    private var _fullValue = 0
    private var _playerTotal = 0

    private val _sellButton: Button
    private val _buyButton: Button
    val closeButton: Button

    private val _buttons: Table
    private val _totalLabels: Table

    private val observers = Array<StoreInventoryObserver>()
    private val _json = Json()

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
        disableButton(_sellButton, true)

        _sellTotalLabel = Label(SELL + " : " + _tradeInVal + GP, Utility.STATUSUI_SKIN)
        _sellTotalLabel.setAlignment(Align.center)
        _buyTotalLabel = Label(BUY + " : " + _fullValue + GP, Utility.STATUSUI_SKIN)
        _buyTotalLabel.setAlignment(Align.center)

        _playerTotalGP = Label("$PLAYER_TOTAL : $_playerTotal $GP", Utility.STATUSUI_SKIN)

        _buyButton = TextButton(BUY, Utility.STATUSUI_SKIN, "inventory")
        disableButton(_buyButton, true)

        closeButton = TextButton("X", Utility.STATUSUI_SKIN)

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

        add()
        add(closeButton)
        row()

        //this.debugAll();
        this.defaults().expand().fill()
        this.add(inventorySlotTable).pad(10f, 10f, 10f, 10f)
        this.row()
        this.add(_buttons)
        this.row()
        this.add(_totalLabels)
        this.row()
        this.add(_playerInventorySlotTable).pad(10f, 10f, 10f, 10f)
        row()
        add(_playerTotalGP)
        this.pack()

        // Listeners
        _buyButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (_fullValue in 1.._playerTotal) {
                    _playerTotal -= _fullValue
                    this@StoreInventoryUI.notify(_playerTotal.toString(), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED)
                    _fullValue = 0
                    _buyTotalLabel.setText("$BUY : $_fullValue$GP")
                    disableButton(_buyButton, true)

                    if (_tradeInVal > 0) {
                        disableButton(_sellButton, false)
                    } else {
                        disableButton(_sellButton, true)
                    }

                    //Make sure we update the owner of the items
                    InventoryUI.setInventoryItemNames(_playerInventorySlotTable, PLAYER_INVENTORY)
                    savePlayerInventory()
                }
            }
        })

        _sellButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (_tradeInVal > 0) {
                    _playerTotal += _tradeInVal
                    this@StoreInventoryUI.notify(_playerTotal.toString(), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED)
                    _tradeInVal = 0
                    _sellTotalLabel.setText("$SELL : $_tradeInVal$GP")
                    disableButton(_sellButton, true)

                    if (_fullValue > 0 && _playerTotal >= _fullValue) {
                        disableButton(_buyButton, false)
                    } else {
                        disableButton(_buyButton, true)
                    }

                    // remove sold items
                    val cells = inventorySlotTable.cells
                    for (i in 0..cells.size - 1) {
                        val inventorySlot = cells[i].actor as InventorySlot
                        if (inventorySlot == null) continue
                        if (inventorySlot.hasItem() &&
                                inventorySlot.getTopInventoryItem()!!.name.equals(PLAYER_INVENTORY, true)) {
                            inventorySlot.clearAllInventoryItems(false)
                        }
                    }
                    savePlayerInventory()
                }
            }
        })

    }

    fun loadPlayerInventory(playerInventoryItems: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(_playerInventorySlotTable, playerInventoryItems, _dragAndDrop)
    }

    fun loadStoreInventory(storeInventoryItems: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(inventorySlotTable, storeInventoryItems, _dragAndDrop)
    }

    fun savePlayerInventory() {
//        InventoryUI.removeInventoryItems(STORE_INVENTORY, _playerInventorySlotTable)
//        val items = InventoryUI.getInventory(_playerInventorySlotTable)
//        this@StoreInventoryUI.notify(_json.toJson(items), StoreInventoryEvent.PLAYER_INVENTORY_UPDATED)
        val playerItemsInPlayerInventory = InventoryUI.getInventory(_playerInventorySlotTable, PLAYER_INVENTORY)
        val playerItemsInStoreInventory = InventoryUI.getInventory(_playerInventorySlotTable, inventorySlotTable, PLAYER_INVENTORY)
        playerItemsInPlayerInventory.addAll(playerItemsInStoreInventory)
        this@StoreInventoryUI.notify(_json.toJson(playerItemsInPlayerInventory), StoreInventoryEvent.PLAYER_INVENTORY_UPDATED);

    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            InventorySlotObserver.SlotEvent.ADDED_ITEM -> {
                // moving from player inventory to store inventory to sell
                if (slot.getTopInventoryItem()!!.name.equals(PLAYER_INVENTORY, ignoreCase = true) && slot.name.equals(STORE_INVENTORY, ignoreCase = true)) {
                    _tradeInVal += slot.getTopInventoryItem()!!.getTradeValue()
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP)
                }

                // moving from store inventory to player inventory to buy
                if (slot.getTopInventoryItem()!!.name.equals(STORE_INVENTORY, ignoreCase = true) && slot.name.equals(PLAYER_INVENTORY, ignoreCase = true)) {
                    _fullValue += slot.getTopInventoryItem()!!.itemValue
                    _buyTotalLabel.setText(BUY + " : " + _fullValue + GP)
                }

                if (_tradeInVal > 0) {
                    disableButton(_sellButton, false)
                } else {
                    disableButton(_sellButton, true)
                }

                if (_fullValue in 1.._playerTotal) {
                    disableButton(_buyButton, false)
                } else {
                    disableButton(_buyButton, true)
                }

            }
            InventorySlotObserver.SlotEvent.REMOVED_ITEM -> {
                if (slot.getTopInventoryItem()!!.name.equals(PLAYER_INVENTORY, ignoreCase = true) && slot.name.equals(STORE_INVENTORY, ignoreCase = true)) {
                    _tradeInVal -= slot.getTopInventoryItem()!!.getTradeValue()
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP)
                }

                if (slot.getTopInventoryItem()!!.name.equals(STORE_INVENTORY, ignoreCase = true) && slot.name.equals(PLAYER_INVENTORY, ignoreCase = true)) {
                    _fullValue -= slot.getTopInventoryItem()!!.itemValue
                    _buyTotalLabel.setText(BUY + " : " + _fullValue + GP)
                }

                if (_tradeInVal <= 0) {
                    disableButton(_sellButton, true)
                } else {
                    disableButton(_sellButton, false)
                }

                if (_fullValue <= 0 || _playerTotal < _fullValue) {
                    disableButton(_buyButton, true)
                } else {
                    disableButton(_buyButton, false)
                }
            }
        }
    }

    fun setPlayerGP(value: Int) {
        _playerTotal = value
        _playerTotalGP.setText("$PLAYER_TOTAL : $_playerTotal$GP")
    }

    private fun disableButton(button: Button, disable: Boolean) {
        if (disable) {
            button.isDisabled = true
            button.touchable = Touchable.disabled
        } else {
            button.isDisabled = false
            button.touchable = Touchable.enabled
        }
    }

    override fun addObserver(storeObserver: StoreInventoryObserver) {
        observers.add(storeObserver)
    }

    override fun removeObserver(storeObserver: StoreInventoryObserver) {
        observers.removeValue(storeObserver, true)
    }

    override fun removeAllObservers() {
        observers.forEach { observer -> observers.removeValue(observer, true) }
    }

    override fun notify(value: String, event: StoreInventoryObserver.StoreInventoryEvent) {
        observers.forEach { observer -> observer.onNotify(value, event) }
    }

    companion object {

        private val STORE_INVENTORY = "Store_Inventory"
        private val PLAYER_INVENTORY = "Player_Inventory"

        private val SELL = "SELL"
        private val BUY = "BUY"
        private val GP = " GP"
        private val PLAYER_TOTAL = "Player Total"
    }
}
