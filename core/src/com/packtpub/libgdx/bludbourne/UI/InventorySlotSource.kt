package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target

class InventorySlotSource(internal var _sourceSlot: InventorySlot,
                          internal var _dragAndDrop: DragAndDrop) :
        Source(_sourceSlot.topInventoryItem) {

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): Payload {
        val payload = Payload()

        _sourceSlot = actor.parent as InventorySlot
        _sourceSlot.decrementItemCount()

        payload.dragActor = actor
        _dragAndDrop.setDragActorPosition(-x, -y + actor.height)

        return payload
    }

    override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: Payload?, target: Target?) {
        if (target == null) {
            _sourceSlot.add(payload!!.dragActor)
        }
    }
}
