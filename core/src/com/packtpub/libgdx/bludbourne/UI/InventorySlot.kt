package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.SnapshotArray
import com.packtpub.libgdx.bludbourne.InventoryItem
import com.packtpub.libgdx.bludbourne.Utility

class InventorySlot constructor() : Stack(), InventorySlotSubject {

    //All slots have this default image
    private val _defaultBackground = Stack()
    private var _customBackgroundDecal = Image()
    val image = Image(NinePatch(Utility.STATUSUI_TEXTUREATLAS.createPatch("dialog")))
    private var numItemsVal = 0
    private val numItemsLabel = Label(numItemsVal.toString(), Utility.STATUSUI_SKIN, "inventory-item-count")
    private var _filterItemType: Int = 0
    private val _observers = Array<InventorySlotObserver>()

    init {
        numItemsLabel.setAlignment(Align.bottomRight)
        numItemsLabel.isVisible = false
        numItemsLabel.name = "numitems"

        _defaultBackground.add(image)
        _defaultBackground.name = "background"

        this.add(_defaultBackground)
        this.add(numItemsLabel)
    }

    constructor(filterItemType: Int, customBackgroundDecal: Image) : this() {
        _filterItemType = filterItemType
        _customBackgroundDecal = customBackgroundDecal
        _defaultBackground.add(_customBackgroundDecal)
    }

    fun decrementItemCount(sendRemoveNotification: Boolean) {
        numItemsVal--
        numItemsLabel.setText(numItemsVal.toString())
        if (_defaultBackground.children.size == 1) {
            _defaultBackground.add(_customBackgroundDecal)
        }
        checkVisibilityOfItemCount()
        if (sendRemoveNotification) {
            notify(this, InventorySlotObserver.SlotEvent.REMOVED_ITEM)
        }
    }

    fun incrementItemCount(sendAddNotification: Boolean) {
        numItemsVal++
        numItemsLabel.setText(numItemsVal.toString())
        if (_defaultBackground.children.size > 1) {
            _defaultBackground.children.pop()
        }
        checkVisibilityOfItemCount()
        if (sendAddNotification) {
            notify(this, InventorySlotObserver.SlotEvent.ADDED_ITEM)
        }
    }

    override fun add(actor: Actor) {
        super.add(actor)

        if (actor != _defaultBackground && actor != numItemsLabel) {
            incrementItemCount(true)
        }
    }

    private fun checkVisibilityOfItemCount() {
        numItemsLabel.isVisible = numItemsVal >= 2
    }

    fun hasItem(): Boolean {
        if (hasChildren()) {
            val items = this.children
            if (items.size > 2) {
                return true
            }
        }
        return false
    }

    fun getNumItems(): Int {
        if (hasChildren()) {
            val items: SnapshotArray<Actor> = this.children
            return items.size - 2
        }
        return 0
    }

    fun getNumItems(name: String): Int {
        if (hasChildren()) {
            val items: SnapshotArray<Actor> = this.getChildren()
            var totalFilteredSize = 0
            for (actor in items) {
                if (actor.getName().equals(name, true)) {
                    totalFilteredSize++
                }
            }
            return totalFilteredSize
        }
        return 0
    }

    fun clearAllInventoryItems(sendRemoveNotification: Boolean) {
        if (hasItem()) {
            val arrayChildren = this.children
            val numInventoryItems = getNumItems()
            for (i in 0..numInventoryItems - 1) {
                decrementItemCount(sendRemoveNotification)
                arrayChildren.pop()
            }
        }
    }

    fun doesAcceptItemUseType(itemUseType: Int): Boolean {
        if (_filterItemType == 0) {
            return true
        } else return _filterItemType and itemUseType == itemUseType
    }

    fun getTopInventoryItem(): InventoryItem? {
        var actor: InventoryItem? = null
        if (hasChildren()) {
            val items = this.children
            if (items.size > 2) {
                actor = items.peek() as InventoryItem
            }
        }
        return actor
    }

    fun add(array: Array<Actor>) {
        for (actor in array) {
            super.add(actor)

            if (actor != _defaultBackground && actor != numItemsLabel) {
                incrementItemCount(true)
            }
        }
    }

    fun getAllInventoryItems(): Array<Actor> {
        val items = Array<Actor>()
        if (hasItem()) {
            val arrayChildren = this.children
            val numInventoryItems = arrayChildren.size - 2
            for (i in 0..numInventoryItems - 1) {
                decrementItemCount(true)
                items.add(arrayChildren.pop())
            }
        }
        return items
    }

    fun updateAllInventoryItemNames(name: String) {
        if (hasItem()) {
            val arrayChildren: SnapshotArray<Actor> = this.children
            //skip first two elements
            for (i in arrayChildren.size - 1 downTo 2) {
                arrayChildren.get(i).name = name
            }
        }
    }

    fun removeAllInventoryItemsWithName(name: String) {
        if (hasItem()) {
            val arrayChildren: SnapshotArray<Actor> = this.children
            //skip first two elements
            for (i in arrayChildren.size - 1 downTo 2) {
                val itemName = arrayChildren.get(i).name
                if (itemName.equals(name, true)) {
                    decrementItemCount(true)
                    arrayChildren.removeIndex(i)
                }
            }
        }
    }

    override fun addObserver(inventorySlotObserver: InventorySlotObserver) {
        _observers.add(inventorySlotObserver)
    }

    override fun removeObserver(inventorySlotObserver: InventorySlotObserver) {
        _observers.removeValue(inventorySlotObserver, true)
    }

    override fun removeAllObservers() {
        _observers.forEach { observer -> _observers.removeValue(observer, true) }
    }

    override fun notify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        _observers.forEach { observer -> observer.onNotify(slot, event) }
    }

    companion object {
        fun swapSlots(inventorySlotSource: InventorySlot, inventorySlotTarget: InventorySlot, dragActor: InventoryItem) {
            //check if items can accept each other, otherwise, no swap
            if (!inventorySlotTarget.doesAcceptItemUseType(dragActor.itemUseType) ||
                    !inventorySlotSource.doesAcceptItemUseType(inventorySlotTarget.getTopInventoryItem()!!.itemUseType)) {
                inventorySlotSource.add(dragActor)
                return
            }
            //swap
            val tempArray = inventorySlotSource.getAllInventoryItems()
            tempArray.add(dragActor)
            inventorySlotSource.add(inventorySlotTarget.getAllInventoryItems())
            inventorySlotTarget.add(tempArray)
        }
    }


}
