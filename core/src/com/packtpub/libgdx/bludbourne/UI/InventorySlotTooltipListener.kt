package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

class InventorySlotTooltipListener(private val _toolTip: InventorySlotTooltip) :
        InputListener() {
    private var _isInside = false
    private val _currentCoords = Vector2(0f, 0f)
    private val _offset = Vector2(20f, 10f)

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        val inventorySlot = event!!.listenerActor as InventorySlot
        if (_isInside) {
            _currentCoords.set(x, y)
            inventorySlot.localToStageCoordinates(_currentCoords)

            _toolTip.setPosition(_currentCoords.x + _offset.x, _currentCoords.y + _offset.y)
        }
        return false
    }


    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        val inventorySlot = event!!.listenerActor as InventorySlot
        _toolTip.setVisible(inventorySlot, false)
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        val inventorySlot = event.listenerActor as InventorySlot

        _isInside = true

        _currentCoords.set(x, y)
        inventorySlot.localToStageCoordinates(_currentCoords)

        _toolTip.updateDescription(inventorySlot)
        _toolTip.setPosition(_currentCoords.x + _offset.x, _currentCoords.y + _offset.y)
        _toolTip.toFront()
        _toolTip.setVisible(inventorySlot, true)
    }

    override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        val inventorySlot = event.listenerActor as InventorySlot
        _toolTip.setVisible(inventorySlot, false)
        _isInside = false

        _currentCoords.set(x, y)
        inventorySlot.localToStageCoordinates(_currentCoords)
    }

}

