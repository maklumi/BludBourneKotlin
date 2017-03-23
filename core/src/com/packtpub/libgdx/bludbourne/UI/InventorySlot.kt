package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.InventoryItem

class InventorySlot constructor() : Stack() {

    //All slots have this default image
    private val _defaultBackground = Stack()
    private var _customBackgroundDecal = Image()
    val image = Image(NinePatch(PlayerHUD.statusUITextureAtlas.createPatch("dialog")))
    private var numItemsVal = 0
    private val numItemsLabel = Label(numItemsVal.toString(), PlayerHUD.statusUISkin, "inventory-item-count")
    private var _filterItemType: Int = 0

    init {
        numItemsLabel.setAlignment(Align.bottomRight)
        numItemsLabel.isVisible = false
        _defaultBackground.add(image)
        this.add(_defaultBackground)
        this.add(numItemsLabel)
    }

    constructor(filterItemType: Int, customBackgroundDecal: Image) : this() {
        _filterItemType = filterItemType
        _customBackgroundDecal = customBackgroundDecal
        _defaultBackground.add(_customBackgroundDecal)
    }

    fun decrementItemCount() {
        numItemsVal--
        numItemsLabel.setText(numItemsVal.toString())
        if (_defaultBackground.children.size == 1) {
            _defaultBackground.add(_customBackgroundDecal)
        }
        checkVisibilityOfItemCount()
    }

    fun incrementItemCount() {
        numItemsVal++
        numItemsLabel.setText(numItemsVal.toString())
        if (_defaultBackground.children.size > 1) {
            _defaultBackground.children.pop()
        }
        checkVisibilityOfItemCount()
    }

    override fun add(actor: Actor) {
        super.add(actor)

        if (actor != _defaultBackground && actor != numItemsLabel) {
            incrementItemCount()
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
                incrementItemCount()
            }
        }
    }

    fun getAllInventoryItems(): Array<Actor> {
        val items = Array<Actor>()
        if (hasItem()) {
            val arrayChildren = this.children
            val numInventoryItems = arrayChildren.size - 2
            for (i in 0..numInventoryItems - 1) {
                items.add(arrayChildren.pop())
                decrementItemCount()
            }
        }
        return items
    }

    companion object {
        fun swapSlots(inventorySlotSource: InventorySlot, inventorySlotTarget: InventorySlot, dragActor: Actor) {
            //swap
            val tempArray = inventorySlotSource.getAllInventoryItems()
            tempArray.add(dragActor)
            inventorySlotSource.add(inventorySlotTarget.getAllInventoryItems())
            inventorySlotTarget.add(tempArray)
        }
    }


}
