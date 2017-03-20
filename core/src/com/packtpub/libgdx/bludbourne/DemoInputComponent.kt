package com.packtpub.libgdx.bludbourne

import com.packtpub.libgdx.bludbourne.Component.MESSAGE.MESSAGE_TOKEN

class DemoInputComponent : InputComponent() {

    init {
       currentDirection = Entity.Direction.getRandomNext()
    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 1) {
            if (string[0].equals(Component.MESSAGE.COLLISION_WITH_MAP, ignoreCase = true)) {
               currentDirection = Entity.Direction.getRandomNext()
            }
        }
    }

    override fun dispose() {

    }

    override fun update(entity: Entity, delta: Float) {
        when (currentDirection) {
            Entity.Direction.LEFT -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT))
            }
            Entity.Direction.RIGHT -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT))
            }
            Entity.Direction.UP -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP))
            }
            Entity.Direction.DOWN -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN))
            }
        }
    }

    companion object {
        private val TAG = DemoInputComponent::class.java.simpleName
    }
}
