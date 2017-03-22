package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN


class PlayerInputComponent : InputComponent(), InputProcessor {

    private val TAG = InputComponent::class.java.simpleName

    private val lastMouseCoordinates = Vector3()

    init {
        Gdx.input.inputProcessor = this
    }

    override fun update(entity: Entity, delta: Float) {
        //Keyboard input
        if (keys[Keys.LEFT]!!) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
            entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT))
        } else if (keys[Keys.RIGHT]!!) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
            entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT))
        } else if (keys[Keys.UP]!!) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
            entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP))
        } else if (keys[Keys.DOWN]!!) {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
            entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN))
        } else if (keys[Keys.QUIT]!!) {
            Gdx.app.exit()
        } else {
            entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE))

        }

        //Mouse input
        if (mouseButtons[Mouse.SELECT]!!) {
            Gdx.app.debug(TAG, "Mouse LEFT click at : (" + lastMouseCoordinates.x + "," + lastMouseCoordinates.y + ")");
            entity.sendMessage(Component.MESSAGE.INIT_SELECT_ENTITY, json.toJson(lastMouseCoordinates))
            mouseButtons.put(Mouse.SELECT, false)
        }

    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION.toString(), ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            this.leftPressed()
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            this.rightPressed()
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            this.upPressed()
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            this.downPressed()
        }
        if (keycode == Input.Keys.Q) {
            this.quitPressed()
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            this.leftReleased()
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            this.rightReleased()
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            this.upReleased()
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            this.downReleased()
        }
        if (keycode == Input.Keys.Q) {
            this.quitReleased()
        }
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            this.setClickedMouseCoordinates(screenX, screenY)
        }

        if (button == Input.Buttons.LEFT) {
            this.selectMouseButtonPressed(screenX, screenY)
        }
        if (button == Input.Buttons.RIGHT) {
            this.doActionMouseButtonPressed(screenX, screenY)
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //left is selection, right is context menu
        if (button == Input.Buttons.LEFT) {
            this.selectMouseButtonReleased(screenX, screenY)
        }
        if (button == Input.Buttons.RIGHT) {
            this.doActionMouseButtonReleased(screenX, screenY)
        }
        return true
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

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    fun leftPressed() = keys.put(Keys.LEFT, true)
    fun rightPressed() = keys.put(Keys.RIGHT, true)
    fun upPressed() = keys.put(Keys.UP, true)
    fun downPressed() = keys.put(Keys.DOWN, true)
    fun quitPressed() = keys.put(Keys.QUIT, true)


    fun setClickedMouseCoordinates(x: Int, y: Int): Vector3 =
            lastMouseCoordinates.set(x.toFloat(), y.toFloat(), 0f)

    fun selectMouseButtonPressed(x: Int, y: Int) =
            mouseButtons.put(Mouse.SELECT, true)

    fun doActionMouseButtonPressed(x: Int, y: Int) =
            mouseButtons.put(Mouse.DOACTION, true)


    fun leftReleased() = keys.put(Keys.LEFT, false)
    fun rightReleased() = keys.put(Keys.RIGHT, false)
    fun upReleased() = keys.put(Keys.UP, false)
    fun downReleased() = keys.put(Keys.DOWN, false)
    fun quitReleased() = keys.put(Keys.QUIT, false)
    fun selectMouseButtonReleased(x: Int, y: Int) =
            mouseButtons.put(Mouse.SELECT, false)

    fun doActionMouseButtonReleased(x: Int, y: Int) =
            mouseButtons.put(Mouse.DOACTION, false)


    fun hide() {
        keys.put(Keys.LEFT, false)
        keys.put(Keys.RIGHT, false)
        keys.put(Keys.UP, false)
        keys.put(Keys.DOWN, false)
        keys.put(Keys.QUIT, false)
    }


}
