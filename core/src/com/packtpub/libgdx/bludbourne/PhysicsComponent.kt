package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json

abstract class PhysicsComponent : Component {
    private val TAG = PhysicsComponent::class.java.simpleName

    val velocity = Vector2(2f, 2f)
    var boundingBox = Rectangle()
    var nextEntityPosition: Vector2 = Vector2(0f, 0f)
    var currentEntityPosition: Vector2 = Vector2(0f, 0f)
    var currentDirection = Entity.Direction.DOWN
    val json = Json()

    abstract fun update(entity: Entity, mapMgr: MapManager, delta: Float)

    fun isCollisionWithMapLayer(mapMgr: MapManager, boundingBox: Rectangle): Boolean {
        val collisionLayer = mapMgr.collisionLayer

        collisionLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle))
                return true
        }
        return false
    }

    fun setNextPositionToCurrent(entity: Entity) {
        currentEntityPosition.set(nextEntityPosition.x, nextEntityPosition.y)

        val text = Component.MESSAGE.CURRENT_POSITION + Component.MESSAGE.MESSAGE_TOKEN + json.toJson(currentEntityPosition)
        entity.sendMessage(Component.MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition))
    }

    fun calculateNextPosition(deltaTime: Float) {
        var testX = currentEntityPosition.x
        var testY = currentEntityPosition.y

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> testX -= velocity.x
            Entity.Direction.RIGHT -> testX += velocity.x
            Entity.Direction.UP -> testY += velocity.y
            Entity.Direction.DOWN -> testY -= velocity.y
            else -> {
            }
        }

        nextEntityPosition.x = testX
        nextEntityPosition.y = testY

        //velocity
        velocity.scl(1 / deltaTime)
    }

    fun setBoundingBoxSize(entity: Entity, percentageWidthReduced: Float, percentageHeightReduced: Float) {
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
            minX = nextEntityPosition.x / MapManager.UNIT_SCALE
            minY = nextEntityPosition.y / MapManager.UNIT_SCALE
        } else {
            minX = nextEntityPosition.x
            minY = nextEntityPosition.y
        }

        boundingBox.set(minX, minY, width, height)
        //Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }

}
