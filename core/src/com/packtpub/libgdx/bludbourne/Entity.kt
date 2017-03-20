package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array

class Entity(val inputComponent: InputComponent,
             val physicsComponent: PhysicsComponent,
             val graphicsComponent: GraphicsComponent) {

    private val TAG = Entity::class.java.simpleName

    enum class State {
        IDLE, WALKING,
        IMMOBILE;

        companion object {
            fun getRandomNext(): State {
                return State.values()[MathUtils.random(State.values().size - 2)]
            }
        }
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        companion object {

            fun getRandomNext(): Direction {
                return Direction.values()[MathUtils.random(Direction.values().size - 1)]
            }
        }
    }

    private val components = Array<Component>(MAX_COMPONENTS)

    init {
        components.add(inputComponent)
        components.add(graphicsComponent)
        components.add(physicsComponent)
    }

    fun update(mapMgr: MapManager, batch: Batch, delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update(this, mapMgr, delta)
        graphicsComponent.update(this, batch, delta)
    }

    fun sendMessage(message: String, vararg args: String) {
        var fullMessage = message

        args.forEach { fullMessage += Component.MESSAGE.MESSAGE_TOKEN + it }

        components.forEach { it.receiveMessage(fullMessage) }
    }

    fun dispose() = components.forEach { it.dispose() }

    companion object {
        var FRAME_WIDTH = 16
        var FRAME_HEIGHT = 16
        val MAX_COMPONENTS = 5
    }


}
