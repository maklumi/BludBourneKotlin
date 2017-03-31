package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN


class PlayerPhysicsComponent : PhysicsComponent() {
    private val TAG = PlayerPhysicsComponent::class.java.simpleName

    private var state = Entity.State.IDLE
    private var mouseSelectCoordinates: Vector3 = Vector3.Zero
    private var isMouseSelectEnabled = false
    private var previousDiscovery: String = ""
    private var previousEnemySpawn: String = ""

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
                previousDiscovery = ""
                previousEnemySpawn = ""
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
        updateDiscoverLayerActivation(mapMgr)
        updateEnemySpawnLayerActivation(mapMgr)

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
        tempEntities.clear()
        tempEntities.addAll(mapMgr.getCurrentMapEntities())
        tempEntities.addAll(mapMgr.getCurrentMapQuestEntities())

        //Convert screen coordinates to world coordinates, then to unit scale coordinates
        mapMgr.camera.unproject(mouseSelectCoordinates)
        mouseSelectCoordinates.x /= Map.UNIT_SCALE
        mouseSelectCoordinates.y /= Map.UNIT_SCALE

//        Gdx.app.debug(TAG, "Mouse Coordinates " + "(" + mouseSelectCoordinates.x + "," + mouseSelectCoordinates.y + ")")

        tempEntities.forEach { mapEntity ->
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
        tempEntities.clear()
    }

    private fun updateDiscoverLayerActivation(mapMgr: MapManager): Boolean {
        val mapDiscoverLayer = mapMgr.getQuestDiscoverLayer() ?: return false

        var rectangle: Rectangle?

        for (`object` in mapDiscoverLayer.objects) {
            if (`object` is RectangleMapObject) {
                rectangle = `object`.rectangle

                if (boundingBox.overlaps(rectangle)) {
                    val questID = `object`.getName()
                    val questTaskID = `object`.getProperties().get("taskID") as String
                    val `val` = questID + MESSAGE_TOKEN + questTaskID

                    if (questID == null) {
                        return false
                    }

                    if (previousDiscovery.equals(`val`, true)) {
                        return true
                    } else {
                        previousDiscovery = `val`
                    }

                    notify(json.toJson(`val`), ComponentObserver.ComponentEvent.QUEST_LOCATION_DISCOVERED)
                    Gdx.app.debug(TAG, "Discover Area Activated")
                    return true
                }
            }
        }
        return false
    }

    private fun updateEnemySpawnLayerActivation(mapMgr: MapManager): Boolean {
        val mapEnemySpawnLayer = mapMgr.getEnemySpawnLayer() ?: return false

        var rectangle: Rectangle?

        for (mapObject in mapEnemySpawnLayer.objects) {
            if (mapObject is RectangleMapObject) {
                rectangle = mapObject.rectangle

                if (boundingBox.overlaps(rectangle)) {
                    val enemySpawnID = mapObject.getName() ?: return false

                    if (previousEnemySpawn.equals(enemySpawnID, true)) {
                        return true
                    } else {
                        Gdx.app.debug(TAG, "Enemy Spawn Area $enemySpawnID Activated with previous Spawn value: $previousEnemySpawn")
                        previousEnemySpawn = enemySpawnID
                    }

                    notify(enemySpawnID, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED)
                    Gdx.app.debug(TAG, "Enemy Spawn Area Activated")
                    return true
                }
            }
        }

        //If no collision, reset the value
        if (!previousEnemySpawn.equals("0", true)) {
            previousEnemySpawn = "0"
            notify(previousEnemySpawn, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED)
            Gdx.app.debug(TAG, "Enemy Spawn Area RESET with previous value " + previousEnemySpawn)
        }
        return false
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
