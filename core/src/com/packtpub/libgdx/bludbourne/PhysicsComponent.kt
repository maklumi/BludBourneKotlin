package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_HEIGHT
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_WIDTH
import com.packtpub.libgdx.bludbourne.ComponentSubject

abstract class PhysicsComponent : ComponentSubject(), Component {
    private val TAG = PhysicsComponent::class.java.simpleName

    val velocity = Vector2(2f, 2f)
    var boundingBox = Rectangle()
    protected var boundingBoxLocation = BoundingBoxLocation.BOTTOM_LEFT
    var nextEntityPosition: Vector2 = Vector2(0f, 0f)
    var currentEntityPosition: Vector2 = Vector2(0f, 0f)
    var currentDirection = Entity.Direction.DOWN
    val json = Json()

    val tempEntities = Array<Entity>()

    var selectionRay = Ray(Vector3(), Vector3())
    val selectRayMaximumDistance = 32.0f

    enum class BoundingBoxLocation {
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        CENTER,
    }

    abstract fun update(entity: Entity, mapMgr: MapManager, delta: Float)

    fun isCollisionWithMapLayer(entity: Entity, mapMgr: MapManager): Boolean {
        val mapCollisionLayer = mapMgr.getCollisionLayer()

        mapCollisionLayer.objects.forEach {
            if (it is RectangleMapObject) {
                if (boundingBox.overlaps(it.rectangle)) {
                    entity.sendMessage(Component.MESSAGE.COLLISION_WITH_MAP)
                    return true
                }
            }
        }

        return false
    }

    protected open fun isCollisionWithMapEntities(entity: Entity, mapMgr: MapManager): Boolean {
        tempEntities.clear()
        tempEntities.addAll(mapMgr.getCurrentMapEntities())
        tempEntities.addAll(mapMgr.getCurrentMapQuestEntities())
        var isCollisionWithMapEntities = false

        for (mapEntity in tempEntities) {
            // check for testing against self
            if (mapEntity == entity) continue

            if (boundingBox.overlaps(mapEntity.getCurrentBoundingBox())) {
                entity.sendMessage(Component.MESSAGE.COLLISION_WITH_ENTITY)
                isCollisionWithMapEntities = true
                break
            }
        }
        tempEntities.clear()
        return isCollisionWithMapEntities

    }

    fun isCollision(entitySource: Entity, entityTarget: Entity): Boolean {
        var isCollisionWithMapEntities = false

        if (entitySource == entityTarget) return false

        if (entitySource.getCurrentBoundingBox().overlaps(entityTarget.getCurrentBoundingBox())) {
            entitySource.sendMessage(Component.MESSAGE.COLLISION_WITH_ENTITY)
            isCollisionWithMapEntities = true
        }

        return isCollisionWithMapEntities
    }

    fun setNextPositionToCurrent(entity: Entity) {
        currentEntityPosition.set(nextEntityPosition.x, nextEntityPosition.y)

        //  val text = Component.MESSAGE.CURRENT_POSITION + Component.MESSAGE.MESSAGE_TOKEN + json.toJson(currentEntityPosition)
        entity.sendMessage(Component.MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition))
    }

    fun calculateNextPosition(deltaTime: Float) {
        if( deltaTime > .7) return

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

    fun initBoundingBox(percentageWidthReduced: Float, percentageHeightReduced: Float) {
        //Update the current bounding box
        val width: Float
        val height: Float

        val origWidth = FRAME_WIDTH
        val origHeight = FRAME_HEIGHT

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

        //Need to account for the unitscale, since the map coordinates will be in pixels
        val minX: Float
        val minY: Float
        if (Map.UNIT_SCALE > 0) {
            minX = nextEntityPosition.x / Map.UNIT_SCALE
            minY = nextEntityPosition.y / Map.UNIT_SCALE
        } else {
            minX = nextEntityPosition.x
            minY = nextEntityPosition.y
        }

        boundingBox.setWidth(width)
        boundingBox.setHeight(height)

        when (boundingBoxLocation) {
            BoundingBoxLocation.BOTTOM_LEFT -> boundingBox.set(minX, minY, width, height)
            BoundingBoxLocation.BOTTOM_CENTER -> boundingBox.setCenter(minX + origWidth / 2, minY + origHeight / 4)
            BoundingBoxLocation.CENTER -> boundingBox.setCenter(minX + origWidth / 2, minY + origHeight / 2)
        }

        //Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }

    protected fun updateBoundingBoxPosition(position: Vector2) {
        //Need to account for the unitscale, since the map coordinates will be in pixels
        val minX: Float
        val minY: Float
        if (Map.UNIT_SCALE > 0) {
            minX = position.x / Map.UNIT_SCALE
            minY = position.y / Map.UNIT_SCALE
        } else {
            minX = position.x
            minY = position.y
        }

        when (boundingBoxLocation) {
            BoundingBoxLocation.BOTTOM_LEFT -> boundingBox.set(minX, minY, boundingBox.width, boundingBox.height)
            BoundingBoxLocation.BOTTOM_CENTER -> boundingBox.setCenter(minX + FRAME_WIDTH / 2, minY + FRAME_HEIGHT / 4)
            BoundingBoxLocation.CENTER -> boundingBox.setCenter(minX + FRAME_WIDTH / 2, minY + FRAME_HEIGHT / 2)
        }

    }

}
