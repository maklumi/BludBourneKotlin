package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class Entity {
    private val TAG = Entity::class.java.simpleName

    var velocity: Vector2 = Vector2(2f, 2f)
    var state = State.IDLE
    var currentPlayerPosition: Vector2 = Vector2()
    var nextPlayerPosition: Vector2 = Vector2()
    var direction = Direction.DOWN

    var boundingBox: Rectangle = Rectangle()

    companion object {
        var FRAME_WIDTH = 16
        var FRAME_HEIGHT = 16
    }

    private val inputComponent = InputComponent()
    private val graphicsComponent = GraphicsComponent()

    enum class State {
        IDLE, WALKING, ANIMATED, ANIMATE_ONCE, ANIMATE_ONCE_REVERSE, PAUSE
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }


    fun update(batch: Batch, delta: Float) {

        inputComponent.update(this, delta)
        graphicsComponent.update(batch, this, delta)

        setBoundingBoxSize(0f, 0.5f)
    }


    fun init(startX: Float, startY: Float) {
        currentPlayerPosition.set(startX, startY)
        nextPlayerPosition.set(startX, startY)
    }

    fun dispose() {
        inputComponent.dispose()
    }


    fun setBoundingBoxSize(percentageWidthReduced: Float, percentageHeightReduced: Float) {
        val width: Float
        val height: Float

        val widthReductionAmount = 1.0f - percentageWidthReduced //.8f for 20% (1 - .20)
        val heightReductionAmount = 1.0f - percentageHeightReduced //.8f for 20% (1 - .20)

        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = FRAME_WIDTH * widthReductionAmount
        } else {
            width = FRAME_WIDTH.toFloat()
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = FRAME_HEIGHT * heightReductionAmount
        } else {
            height = FRAME_HEIGHT.toFloat()
        }


        if (width == 0f || height == 0f) {
            Gdx.app.debug(TAG, "Width and Height are 0!! $width:$height")
        }

        // convert from unit coordinates to pixel coordinates
        val minX = nextPlayerPosition.x / MapManager.UNIT_SCALE
        val minY = nextPlayerPosition.y / MapManager.UNIT_SCALE

        boundingBox.set(minX, minY, width, height)
        //Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }


    fun setNextPositionToCurrent() {
        currentPlayerPosition.set(nextPlayerPosition.x, nextPlayerPosition.y)
    }


    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {

        var testX = currentPlayerPosition.x
        var testY = currentPlayerPosition.y

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> testX -= velocity.x
            Entity.Direction.RIGHT -> testX += velocity.x
            Entity.Direction.UP -> testY += velocity.y
            Entity.Direction.DOWN -> testY -= velocity.y
        }

        nextPlayerPosition.x = testX
        nextPlayerPosition.y = testY

        //velocity
        velocity.scl(1 / deltaTime)
    }


}
