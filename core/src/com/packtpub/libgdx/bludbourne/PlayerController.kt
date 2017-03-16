package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3

import java.util.HashMap


class PlayerController {
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

    fun dispose() {}

    fun leftPressed() = keys.put(Keys.LEFT, true)
    fun rightPressed() = keys.put(Keys.RIGHT, true)
    fun upPressed() = keys.put(Keys.UP, true)
    fun downPressed() = keys.put(Keys.DOWN, true)
    fun quitPressed() = keys.put(Keys.QUIT, true)


    fun setClickedMouseCoordinates(x: Int, y: Int) =
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
    }

    private fun processInput(delta: Float) {
        //Keyboard input
        if (keys[Keys.LEFT]!!) {
            BludBourne.player.calculateNextPosition(Entity.Direction.LEFT, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.LEFT)
        } else if (keys[Keys.RIGHT]!!) {
            BludBourne.player.calculateNextPosition(Entity.Direction.RIGHT, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.RIGHT)
        } else if (keys[Keys.UP]!!) {
            BludBourne.player.calculateNextPosition(Entity.Direction.UP, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.UP)
        } else if (keys[Keys.DOWN]!!) {
            BludBourne.player.calculateNextPosition(Entity.Direction.DOWN, delta)
            BludBourne.player.state = Entity.State.WALKING
            BludBourne.player.setDirection(Entity.Direction.DOWN)
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
