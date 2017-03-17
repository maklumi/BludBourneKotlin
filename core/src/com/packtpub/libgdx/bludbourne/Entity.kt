package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.*

class Entity {

    var velocity: Vector2 = Vector2(100f, 100f)
    var rotationDegrees = 0f
    var state = State.IDLE
    var currentPlayerPosition: Vector2 = Vector2()
    var nextPlayerPosition: Vector2 = Vector2()
    var currentDirection = Direction.LEFT
        private set
    var previousDirection = Direction.UP
        private set

    var frameTime = 0f

    var frameSprite: Sprite = Sprite()
        private set


    enum class State {
        IDLE, WALKING, ANIMATED, ANIMATE_ONCE, ANIMATE_ONCE_REVERSE, PAUSE
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        //Gdx.app.debug(TAG, "Current Direction: " + Direction.values()[(ordinal()) % Direction.values().length] );
        //Gdx.app.debug(TAG, "Current Direction: " + ordinal() );
        //Gdx.app.debug(TAG, "Next Direction: " + Direction.values()[(ordinal()+1) % Direction.values().length] );
        val next: Direction
            get() = Direction.values()[(ordinal + 1) % Direction.values().size]

        val randomNext: Direction
            get() = Direction.values()[MathUtils.random(Direction.values().size - 1)]

        val opposite: Direction
            get() {
                if (this == LEFT) {
                    return RIGHT
                } else if (this == RIGHT) {
                    return LEFT
                } else if (this == UP) {
                    return DOWN
                } else {
                    return UP
                }
            }

    }


    fun update(delta: Float) {
        frameTime += delta

    }


    fun init(startX: Float, startY: Float) {
        this.currentPlayerPosition.x = startX
        this.currentPlayerPosition.y = startY
        this.nextPlayerPosition.x = startX
        this.nextPlayerPosition.y = startY

    }


    fun setDirection(direction: Direction) {
        this.previousDirection = this.currentDirection
        this.currentDirection = direction

        //Look into the appropriate variable when changing position

        when (currentDirection) {
            Entity.Direction.DOWN -> rotationDegrees = 0f
            Entity.Direction.LEFT -> rotationDegrees = 270f
            Entity.Direction.UP -> rotationDegrees = 180f
            Entity.Direction.RIGHT -> rotationDegrees = 90f
            else -> {
            }
        }

    }


    fun setNextPositionToCurrent() {

        frameSprite.x = nextPlayerPosition.x
        frameSprite.y = nextPlayerPosition.y
        currentPlayerPosition.set(nextPlayerPosition.x, nextPlayerPosition.y)
        Gdx.app.debug(TAG, "NOT BLOCKED: Setting nextPlayerPosition as Current: (" + nextPlayerPosition.x + "," + nextPlayerPosition.y + ")");
    }


    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {

        var testX = currentPlayerPosition.x
        var testY = currentPlayerPosition.y


        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Position: (" + currentPlayerPosition.x + "," + currentPlayerPosition.y + ")"  );
        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Direction: " + currentDirection  );

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


    private val TAG = Entity::class.java.simpleName


}
