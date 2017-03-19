package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json

class PhysicsComponent : Component {
    private val TAG = PhysicsComponent::class.java.simpleName

    private val velocity = Vector2(2f, 2f)

    private var boundingBox = Rectangle()
    private var nextEntityPosition: Vector2 = Vector2(0f, 0f)
    private var currentEntityPosition: Vector2 = Vector2(0f, 0f)
    private val json = Json()
    private var state = Entity.State.IDLE
    private var currentDirection = Entity.Direction.DOWN

    fun update(entity: Entity, mapMgr: MapManager, delta: Float) {
        //We want the hit box to be at the feet for a better feel
        setBoundingBoxSize(entity, 0f, 0.5f)

        if (!isCollisionWithMapLayer(mapMgr, boundingBox) &&
                state === Entity.State.WALKING) {
            setNextPositionToCurrent(entity)
        }

        val camera = mapMgr.camera
        camera.position.set(currentEntityPosition.x, currentEntityPosition.y, 0f)
        camera.update()

        updatePortalLayerActivation(mapMgr, boundingBox)

        calculateNextPosition(delta)

        //Gdx.app.debug(TAG, "update:: Next Position: (" + nextEntityPosition.x + "," + nextEntityPosition.y + ")" + "DELTA: " + delta);
    }

    override fun receiveMessage(message: String) {
        val string = message.split(Component.MESSAGE.MESSAGE_TOKEN.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.INIT_START_POSITION, ignoreCase = true)) {
                currentEntityPosition = json.fromJson(Vector2::class.java, string[1])
                nextEntityPosition.set(currentEntityPosition.x, currentEntityPosition.y)
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE, ignoreCase = true)) {
                state = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION, ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun dispose() {

    }

    private fun updatePortalLayerActivation(mapMgr: MapManager, boundingBox: Rectangle): Boolean {
        // portal layer specifies its name as the layer to go
        val portalLayer = mapMgr.portalLayer

        portalLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle)) {
                val mapName = it.getName() ?: return false
                // cache position in pixels just in case we need to return later
                mapMgr.setClosestStartPositionFromScaledUnits(currentEntityPosition)
                mapMgr.loadMap(mapName)
                currentEntityPosition.x = mapMgr.playerStartUnitScaled.x
                currentEntityPosition.y = mapMgr.playerStartUnitScaled.y
                nextEntityPosition.x = mapMgr.playerStartUnitScaled.x
                nextEntityPosition.y = mapMgr.playerStartUnitScaled.y

                Gdx.app.debug(TAG, "Portal to $mapName Activated")
                return true
            }
        }
        return false
    }


    private fun isCollisionWithMapLayer(mapMgr: MapManager, boundingBox: Rectangle): Boolean {
        val collisionLayer = mapMgr.collisionLayer

        collisionLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle))
                return true
        }
        return false
    }

    private fun setNextPositionToCurrent(entity: Entity) {
        currentEntityPosition.set(nextEntityPosition.x, nextEntityPosition.y)

        val text = Component.MESSAGE.CURRENT_POSITION + Component.MESSAGE.MESSAGE_TOKEN + json.toJson(currentEntityPosition)
        entity.sendMessage(Component.MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition))
    }

    private fun calculateNextPosition(deltaTime: Float) {
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
