package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class PlayerPhysicsComponent : PhysicsComponent() {
    private val TAG = PlayerPhysicsComponent::class.java.simpleName

    private var state = Entity.State.IDLE

    override fun dispose() {

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

    override fun update(entity: Entity, mapMgr: MapManager, delta: Float) {
        //We want the hit box to be at the feet for a better feel
        setBoundingBoxSize(entity, 0f, 0.5f)

        if (!isCollisionWithMapLayer(entity, mapMgr, boundingBox) &&
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
}
