package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3

import java.util.HashMap


class PlayerController : InputProcessor {
    private val TAG = PlayerController::class.java.simpleName

    internal enum class Keys { LEFT, RIGHT, UP, DOWN, QUIT }
    internal enum class Mouse { SELECT, DOACTION }

    private val lastMouseCoordinates = Vector3()

    init {
        keys.put(Keys.LEFT, false)
        keys.put(Keys.RIGHT, false)
        keys.put(Keys.UP, false)
        keys.put(Keys.DOWN, false)
        keys.put(Keys.QUIT, false)

        mouseButtons.put(Mouse.SELECT, false)
        mouseButtons.put(Mouse.DOACTION, false)
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
        //Gdx.app.debug(TAG, "GameScreen: MOUSE DOWN........: (" + screenX + "," + screenY + ")" );

        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            this.setClickedMouseCoordinates(screenX, screenY)
        }

        //left is selection, right is context menu
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

    fun dispose() {}

    fun leftPressed() = keys.put(Keys.LEFT, true)
    fun rightPressed() = keys.put(Keys.RIGHT, true)
    fun upPressed() = keys.put(Keys.UP, true)
    fun downPressed() = keys.put(Keys.DOWN, true)
    fun quitPressed() = keys.put(Keys.QUIT, true)


    fun setClickedMouseCoordinates(x: Int, y: Int): Vector3=
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


    fun update(delta: Float) {
        if (delta == 0f || BludBourne.player.state === Entity.State.PAUSE) return

        processInput(delta)

        BludBourne.player.setNextPositionToCurrent()
    }

    private fun processInput(delta: Float) {
        //Keyboard input
        if (keys[Keys.LEFT]!!) {
            Gdx.app.debug(TAG, "LEFT key")
            BludBourne.player.calculateNextPosition(Entity.Direction.LEFT, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.LEFT, delta)
        } else if (keys[Keys.RIGHT]!!) {
            Gdx.app.debug(TAG, "RIGHT key")
            BludBourne.player.calculateNextPosition(Entity.Direction.RIGHT, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.RIGHT, delta)
        } else if (keys[Keys.UP]!!) {
            Gdx.app.debug(TAG, "UP key")
            BludBourne.player.calculateNextPosition(Entity.Direction.UP, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.UP, delta)
        } else if (keys[Keys.DOWN]!!) {
            Gdx.app.debug(TAG, "DOWN key")
            BludBourne.player.calculateNextPosition(Entity.Direction.DOWN, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.DOWN, delta)
        } else if (keys[Keys.QUIT]!!) {
            Gdx.app.exit()
        } else {
            BludBourne.player.state = Entity.State.IDLE
        }

        //Mouse input
        if (mouseButtons[Mouse.SELECT]!!) {
            //Gdx.app.debug(TAG, "Mouse LEFT click at : (" + lastMouseCoordinates.x + "," + lastMouseCoordinates.y + ")" );
            mouseButtons.put(Mouse.SELECT, false)
        }

    }

    companion object {
        internal var keys: MutableMap<Keys, Boolean> = HashMap()
        internal var mouseButtons: MutableMap<Mouse, Boolean> = HashMap()

        fun hide() {
            keys.put(Keys.LEFT, false)
            keys.put(Keys.RIGHT, false)
            keys.put(Keys.UP, false)
            keys.put(Keys.DOWN, false)
            keys.put(Keys.QUIT, false)
        }
    }

}
