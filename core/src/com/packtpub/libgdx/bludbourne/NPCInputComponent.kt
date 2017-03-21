package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class NPCInputComponent : InputComponent(), InputProcessor {

    private var frameTime = 0.0f

    init {
        currentDirection = Entity.Direction.getRandomNext()
        currentState = Entity.State.WALKING
    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 1) {
            if (string[0].equals(Component.MESSAGE.COLLISION_WITH_MAP.toString(), ignoreCase = true)) {
                currentDirection = Entity.Direction.getRandomNext()
            }
        }

        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.INIT_STATE.toString(), ignoreCase = true)) {
                currentState = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.INIT_DIRECTION.toString(), true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun dispose() {

    }

    override fun update(entity: Entity, delta: Float) {
        if (keys[InputComponent.Keys.QUIT]!!) {
            Gdx.app.exit()
        }

        // if IMMOBILE, don't update anything
        if (currentState == Entity.State.IMMOBILE) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IMMOBILE))
            return
        }

        frameTime += delta

        //Change direction after so many seconds
        if (frameTime > 3) {
            currentState = Entity.State.getRandomNext()
            currentDirection = Entity.Direction.getRandomNext()
            frameTime = 0.0f
        }

        if (currentState === Entity.State.IDLE) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE))
            return
        }

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

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.Q) {
            keys.put(InputComponent.Keys.QUIT, true)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    companion object {
        private val TAG = NPCInputComponent::class.java.simpleName
    }
}
