package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target

class InventorySlotSource(internal var sourceSlot: InventorySlot,
                          internal var dragAndDrop: DragAndDrop) :
        Source(sourceSlot.getTopInventoryItem()) {

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): Payload {
        val payload = Payload()

        sourceSlot = actor.parent as InventorySlot
        sourceSlot.decrementItemCount()

        payload.dragActor = actor
        dragAndDrop.setDragActorPosition(-x, -y + actor.height)

        return payload
    }

    override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int, payload: Payload, target: Target?) {
        if (target == null) {
            sourceSlot.add(payload.dragActor)
        }
    }
}
