package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class PhysicsComponent {
    private val TAG = PhysicsComponent::class.java.simpleName

    private val velocity = Vector2(2f, 2f)

    var boundingBox = Rectangle()
    var nextPlayerPosition: Vector2 = Vector2(0f, 0f)
    var currentPlayerPosition: Vector2 = Vector2(0f, 0f)


    fun update(mapMgr: MapManager, entity: Entity, delta: Float) {
        //We want the hit box to be at the feet for a better feel
        setBoundingBoxSize(entity, 0f, 0.5f)

        if (!isCollisionWithMapLayer(mapMgr, boundingBox) &&
                entity.state === Entity.State.WALKING) {
            setNextPositionToCurrent()
        }

        calculateNextPosition(entity.direction, delta)

        //Gdx.app.debug(TAG, "update:: Next Position: (" + nextPlayerPosition.x + "," + nextPlayerPosition.y + ")" + "DELTA: " + delta);
    }

    fun dispose() {

    }

    private fun isCollisionWithMapLayer(mapMgr: MapManager, boundingBox: Rectangle): Boolean {
        val collisionLayer = mapMgr.collisionLayer

        collisionLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle))
                return true
        }
        return false
    }

    fun init(startX: Float, startY: Float) {
        currentPlayerPosition.set(startX, startY)
        nextPlayerPosition.set(startX, startY)
    }

    private fun setCurrentPosition(currentPositionX: Float, currentPositionY: Float) {
        currentPlayerPosition.set(currentPositionX, currentPositionY)
    }

    private fun setNextPositionToCurrent() {
        setCurrentPosition(nextPlayerPosition.x, nextPlayerPosition.y)
    }

    private fun calculateNextPosition(currentDirection: Entity.Direction, deltaTime: Float) {
        var testX = currentPlayerPosition.x
        var testY = currentPlayerPosition.y

        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Position: (" + _currentPlayerPosition.x + "," + _currentPlayerPosition.y + ")"  );
        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Direction: " + _currentDirection  );

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> testX -= velocity.x
            Entity.Direction.RIGHT -> testX += velocity.x
            Entity.Direction.UP -> testY += velocity.y
            Entity.Direction.DOWN -> testY -= velocity.y
            else -> {
            }
        }

        nextPlayerPosition.x = testX
        nextPlayerPosition.y = testY

        //velocity
        velocity.scl(1 / deltaTime)
    }

    private fun setBoundingBoxSize(entity: Entity, percentageWidthReduced: Float, percentageHeightReduced: Float) {
        //Update the current bounding box
        val width: Float
        val height: Float

        val widthReductionAmount = 1.0f - percentageWidthReduced //.8f for 20% (1 - .20)
        val heightReductionAmount = 1.0f - percentageHeightReduced //.8f for 20% (1 - .20)

        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = Entity.FRAME_WIDTH * widthReductionAmount
        } else {
            width = Entity.FRAME_WIDTH.toFloat()
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = Entity.FRAME_HEIGHT * heightReductionAmount
        } else {
            height = Entity.FRAME_HEIGHT.toFloat()
        }

        if (width == 0f || height == 0f) {
            Gdx.app.debug(TAG, "Width and Height are 0!! $width:$height")
        }

        //Need to account for the unitscale, since the map coordinates will be in pixels
        val minX: Float
        val minY: Float
        if (MapManager.UNIT_SCALE > 0) {
            minX = nextPlayerPosition.x / MapManager.UNIT_SCALE
            minY = nextPlayerPosition.y / MapManager.UNIT_SCALE
        } else {
            minX = nextPlayerPosition.x
            minY = nextPlayerPosition.y
        }

        boundingBox.set(minX, minY, width, height)
        //Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }

}
