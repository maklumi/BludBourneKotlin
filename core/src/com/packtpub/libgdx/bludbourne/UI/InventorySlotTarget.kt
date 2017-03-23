package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target

class InventorySlotTarget(internal var _targetSlot: InventorySlot) : Target(_targetSlot) {

    override fun drag(source: Source, payload: Payload, x: Float, y: Float, pointer: Int): Boolean {
        return true
    }

    override fun reset(source: Source?, payload: Payload?) {}

    override fun drop(source: Source, payload: Payload, x: Float, y: Float, pointer: Int) {
        val actor = payload.dragActor ?: return

        _targetSlot.add(actor)
    }
}
