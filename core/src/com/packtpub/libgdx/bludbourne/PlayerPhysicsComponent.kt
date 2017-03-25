package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN
import com.packtpub.libgdx.bludbourne.ComponentObserver

class PlayerPhysicsComponent : PhysicsComponent() {
    private val TAG = PlayerPhysicsComponent::class.java.simpleName

    private var state = Entity.State.IDLE
    private var mouseSelectCoordinates: Vector3 = Vector3.Zero
    private var isMouseSelectEnabled = false

    init {
        boundingBoxLocation = BoundingBoxLocation.BOTTOM_CENTER
        initBoundingBox(0.3f, 0.5f)
    }

    override fun dispose() {

    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.INIT_START_POSITION.toString(), ignoreCase = true)) {
                currentEntityPosition = json.fromJson(Vector2::class.java, string[1])
                nextEntityPosition.set(currentEntityPosition.x, currentEntityPosition.y)
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE.toString(), ignoreCase = true)) {
                state = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION.toString(), ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.INIT_SELECT_ENTITY.toString(), true)) {
                mouseSelectCoordinates = json.fromJson(Vector3::class.java, string[1])
                isMouseSelectEnabled = true
            }
        }
    }

    override fun update(entity: Entity, mapMgr: MapManager, delta: Float) {
        //We want the hit box to be at the feet for a better feel
        updateBoundingBoxPosition(nextEntityPosition)
        updatePortalLayerActivation(mapMgr)

        if (isMouseSelectEnabled) {
            selectMapEntityCandidate(mapMgr)
            isMouseSelectEnabled = false
        }

        if (!isCollisionWithMapLayer(entity, mapMgr) &&
                !isCollisionWithMapEntities(entity, mapMgr) &&
                state === Entity.State.WALKING) {
            setNextPositionToCurrent(entity)
        } else {
            updateBoundingBoxPosition(currentEntityPosition)
        }

        val camera = mapMgr.camera
        camera.position.set(currentEntityPosition.x, currentEntityPosition.y, 0f)
        camera.update()


        calculateNextPosition(delta)

        //Gdx.app.debug(TAG, "update:: Next Position: (" + nextEntityPosition.x + "," + nextEntityPosition.y + ")" + "DELTA: " + delta);
    }

    private fun selectMapEntityCandidate(mapMgr: MapManager) {
        val currentEntities = mapMgr.getCurrentMapEntities()

        //Convert screen coordinates to world coordinates, then to unit scale coordinates
        mapMgr.camera.unproject(mouseSelectCoordinates)
        mouseSelectCoordinates.x /= Map.UNIT_SCALE
        mouseSelectCoordinates.y /= Map.UNIT_SCALE

//        Gdx.app.debug(TAG, "Mouse Coordinates " + "(" + mouseSelectCoordinates.x + "," + mouseSelectCoordinates.y + ")")

        currentEntities.forEach { mapEntity ->
            //Don't break, reset all entities
            mapEntity.sendMessage(Component.MESSAGE.ENTITY_DESELECTED)
            val mapEntityBoundingBox = mapEntity.getCurrentBoundingBox()
//            Gdx.app.debug(TAG, "Entity Candidate Location " + "(" + mapEntityBoundingBox.x + "," + mapEntityBoundingBox.y + ")")
            if (mapEntity.getCurrentBoundingBox().contains(mouseSelectCoordinates.x, mouseSelectCoordinates.y)) {
                //Check distance
                selectionRay.set(boundingBox.x, boundingBox.y, 0.0f, mapEntityBoundingBox.x, mapEntityBoundingBox.y, 0.0f)
                val distance = selectionRay.origin.dst(selectionRay.direction)

                if (distance <= selectRayMaximumDistance) {
                    //We have a valid entity selection
                    //Picked/Selected
                    Gdx.app.debug(TAG, "Selected Entity! " + mapEntity.entityConfig.entityID)
                    mapEntity.sendMessage(Component.MESSAGE.ENTITY_SELECTED)
                    notify(json.toJson(mapEntity.entityConfig), ComponentObserver.ComponentEvent.LOAD_CONVERSATION)
                }
            }
        }
    }

    private fun updatePortalLayerActivation(mapMgr: MapManager): Boolean {
        // portal layer specifies its name as the layer to go
        val portalLayer = mapMgr.getPortalLayer()

        portalLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle)) {
                val mapName = it.getName() ?: return false
                // cache position in pixels just in case we need to return later
                mapMgr.setClosestStartPositionFromScaledUnits(currentEntityPosition)
                mapMgr.loadMap(MapFactory.MapType.valueOf(mapName))
                currentEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x
                currentEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y
                nextEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x
                nextEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y

//                Gdx.app.debug(TAG, "Portal to $mapName Activated")
                return true
            }
        }
        return false
    }
}
