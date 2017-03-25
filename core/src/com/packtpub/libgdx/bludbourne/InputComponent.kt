package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.ComponentSubject
import java.util.*


abstract class InputComponent : ComponentSubject(), Component, InputProcessor {

    var currentDirection: Entity.Direction = Entity.Direction.DOWN
    var currentState: Entity.State = Entity.State.IDLE
    var json: Json = Json()

    enum class Keys {
        LEFT, RIGHT, UP, DOWN, QUIT, PAUSE
    }

    enum class Mouse {
        SELECT, DOACTION
    }

    protected var keys: MutableMap<Keys, Boolean> = HashMap()
    protected var mouseButtons: MutableMap<Mouse, Boolean> = HashMap()

    //initialize the hashmap for inputs
    init {
        keys.put(Keys.LEFT, false)
        keys.put(Keys.RIGHT, false)
        keys.put(Keys.UP, false)
        keys.put(Keys.DOWN, false)
        keys.put(Keys.QUIT, false)
        keys.put(Keys.PAUSE, false)

        mouseButtons.put(Mouse.SELECT, false)
        mouseButtons.put(Mouse.DOACTION, false)
    }

    abstract fun update(entity: Entity, delta: Float)
}
