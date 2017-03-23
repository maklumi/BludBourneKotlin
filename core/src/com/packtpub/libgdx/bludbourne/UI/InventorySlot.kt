package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align

class InventorySlot : Stack() {

    //All slots have this default image
    private val _imageBackground = Image(NinePatch(PlayerHUD.statusUITextureAtlas.createPatch("dialog")))
    private var _numItemsVal = 0
    private val _numItemsLabel = Label(_numItemsVal.toString(), PlayerHUD.statusUISkin, "inventory-item-count")

    init {
        this.add(_imageBackground)

        _numItemsLabel.setAlignment(Align.bottomRight)
        _numItemsLabel.isVisible = false
        this.add(_numItemsLabel)
    }

    fun decrementItemCount() {
        _numItemsVal--
        _numItemsLabel.setText(_numItemsVal.toString())
        checkVisibilityOfItemCount()
    }

    fun incrementItemCount() {
        _numItemsVal++
        _numItemsLabel.setText(_numItemsVal.toString())
        checkVisibilityOfItemCount()
    }

    override fun add(actor: Actor) {
        super.add(actor)

        if (actor != _imageBackground && actor != _numItemsLabel) {
            incrementItemCount()
        }
    }

    private fun checkVisibilityOfItemCount() {
        _numItemsLabel.isVisible = _numItemsVal >= 2
    }

    val topInventoryItem: Actor?
        get() {
            var actor: Actor? = null
            if (hasChildren()) {
                val items = this.children
                if (items.size > 1) {
                    actor = items.peek()
                }
            }
            return actor
        }
}
