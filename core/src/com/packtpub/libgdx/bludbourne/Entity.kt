package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class Entity {
    private val TAG = Entity::class.java.simpleName

    var state = State.IDLE
    var direction = Direction.DOWN
    var boundingBox: Rectangle = Rectangle()
    var nextPlayerPosition: Vector2 = Vector2()
    var currentPlayerPosition: Vector2 = Vector2()

    private val inputComponent = InputComponent()
    private val graphicsComponent = GraphicsComponent()
    private val physicsComponent = PhysicsComponent()

    enum class State {
        IDLE, WALKING, ANIMATED, ANIMATE_ONCE, ANIMATE_ONCE_REVERSE, PAUSE
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }

    fun update(mapMgr: MapManager, batch: Batch, delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update(mapMgr, this, delta)
        graphicsComponent.update(batch, this, delta)

        currentPlayerPosition = physicsComponent.currentPlayerPosition
        nextPlayerPosition = physicsComponent.nextPlayerPosition
        boundingBox = physicsComponent.boundingBox
    }

    fun dispose() {
        inputComponent.dispose()
        physicsComponent.dispose()
        graphicsComponent.dispose()
    }

    companion object {
        var FRAME_WIDTH = 16
        var FRAME_HEIGHT = 16
    }


}
