package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array

class InventorySlot : Stack() {

    //All slots have this default image
    private val imageBackground = Image(NinePatch(PlayerHUD.statusUITextureAtlas.createPatch("dialog")))
    private var numItemsVal = 0
    private val numItemsLabel = Label(numItemsVal.toString(), PlayerHUD.statusUISkin, "inventory-item-count")

    init {
        this.add(imageBackground)

        numItemsLabel.setAlignment(Align.bottomRight)
        numItemsLabel.isVisible = false
        this.add(numItemsLabel)
    }

    fun decrementItemCount() {
        numItemsVal--
        numItemsLabel.setText(numItemsVal.toString())
        checkVisibilityOfItemCount()
    }

    fun incrementItemCount() {
        numItemsVal++
        numItemsLabel.setText(numItemsVal.toString())
        checkVisibilityOfItemCount()
    }

    override fun add(actor: Actor) {
        super.add(actor)

        if (actor != imageBackground && actor != numItemsLabel) {
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

            if (actor != imageBackground && actor != numItemsLabel) {
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
