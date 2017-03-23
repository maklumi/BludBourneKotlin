package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target

class InventorySlotTarget(internal var targetSlot: InventorySlot) : Target(targetSlot) {

    override fun drag(source: Source, payload: Payload, x: Float, y: Float, pointer: Int): Boolean {
        return true
    }

    override fun reset(source: Source?, payload: Payload?) {}

    override fun drop(source: Source, payload: Payload, x: Float, y: Float, pointer: Int) {
        val sourceActor = payload.dragActor as InventoryItem
        val targetActor = targetSlot.getTopInventoryItem()

        if (!targetSlot.hasItem()) {
            targetSlot.add(sourceActor)
        } else {
            //If the same item and stackable, add
            if (sourceActor.isSameItemType(targetActor!!) && sourceActor.isStackable()) {
                targetSlot.add(sourceActor)
            } else {
                //If they aren't the same items or the items aren't stackable, then swap
                InventorySlot.swapSlots((source as InventorySlotSource).sourceSlot, targetSlot, sourceActor)
            }
        }
    }
}
