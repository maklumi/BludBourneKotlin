package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.profile.ProfileManager
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver
import com.packtpub.libgdx.bludbourne.sfx.ClockActor

class MapManager : ProfileObserver {

    lateinit var player: Entity //set from MainGameScreen
    var camera = OrthographicCamera()
    var hasMapChanged = false
    //    private var currentMap: Map = MapFactory.getMap(MapFactory.MapType.TOWN)
    private var currentMap: Map? = null
    var currentSelectedEntity: Entity? = null
    var currentLightMap: MapLayer? = null
    var previousLightMap: MapLayer? = null
    private var _timeOfDay: ClockActor.TimeOfDay? = null
    private var _currentLightMapOpacity = 0f
    private var _previousLightMapOpacity = 1f
    private var _timeOfDayChanged = false

    override fun onNotify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent) {
        when (event) {
            ProfileObserver.ProfileEvent.PROFILE_LOADED -> {
                val currentMap = profileManager.getProperty("currentMapType", String::class.java)
                val mapType: MapFactory.MapType
                if (currentMap == null || currentMap.isEmpty()) {
                    mapType = MapFactory.MapType.TOWN
                } else {
                    mapType = MapFactory.MapType.valueOf(currentMap)
                }

                loadMap(mapType)

                // Persisted the closest player position values for different maps
                val topWorldMapStartPosition = profileManager.getProperty("topWorldMapStartPosition", Vector2::class.java)
                if (topWorldMapStartPosition != null) {
                    MapFactory.getMap(MapFactory.MapType.TOP_WORLD).playerStart = topWorldMapStartPosition
                }

                val castleOfDoomMapStartPosition = profileManager.getProperty("castleOfDoomMapStartPosition", Vector2::class.java)
                if (castleOfDoomMapStartPosition != null) {
                    MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).playerStart = castleOfDoomMapStartPosition
                }

                val townMapStartPosition = profileManager.getProperty("townMapStartPosition", Vector2::class.java)
                if (townMapStartPosition != null) {
                    MapFactory.getMap(MapFactory.MapType.TOWN).playerStart = townMapStartPosition
                }

            }
            ProfileObserver.ProfileEvent.SAVING_PROFILE -> {
                if (currentMap != null) {
                    profileManager.setProperty("currentMapType", currentMap!!.currentMapType.toString())
                }
                profileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOP_WORLD).playerStart)
                profileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).playerStart)
                profileManager.setProperty("townMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOWN).playerStart)
            }
            ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE -> {
                currentMap = null
                profileManager.setProperty("currentMapType", MapFactory.MapType.TOWN.toString())

                MapFactory.clearCache()

                profileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOP_WORLD).playerStart)
                profileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).playerStart)
                profileManager.setProperty("townMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOWN).playerStart)
            }
        }
    }

    fun loadMap(mapType: MapFactory.MapType) {
        val map = MapFactory.getMap(mapType)

        if (map == null) {
            Gdx.app.debug(TAG, "Map does not exist!")
        }

        currentMap?.unloadMusic()

        map.loadMusic()

        currentMap = map
        hasMapChanged = true
        clearCurrentSelectedMapEntity()
        previousLightMap = null
        currentLightMap = null
        Gdx.app.debug(TAG, "Player Start: (" + currentMap?.playerStart?.x + "," + currentMap?.playerStart?.y + ")")
    }

    fun unregisterCurrentMapEntityObservers() {
        if (currentMap != null) {
            val entities = currentMap!!.mapEntities
            entities.forEach(Entity::unregisterObservers)

            val questEntities = currentMap!!.mapQuestEntities
            questEntities.forEach(Entity::unregisterObservers)
        }
    }

    fun registerCurrentMapEntityObservers(observer: ComponentObserver) {
        currentMap?.apply {
            mapEntities.forEach { it.registerObserver(observer) }

            mapQuestEntities.forEach { it.registerObserver(observer) }
        }
    }

    fun getCurrentMapQuestEntities(): Array<Entity>? = currentMap?.mapQuestEntities

    fun addMapQuestEntities(entities: Array<Entity>) {
        currentMap!!.mapQuestEntities.addAll(entities)
    }

    fun removeMapQuestEntity(entity: Entity) {
        entity.unregisterObservers()
        val positions = ProfileManager.instance.getProperty(entity.entityConfig.entityID, Array::class.java)
        if (positions == null) return

        for (position in positions as Array<Vector2>) {
            if (position.x == entity.getCurrentPosition().x &&
                    position.y == entity.getCurrentPosition().y) {
                positions.removeValue(position, true)
                break
            }
        }
        currentMap!!.mapQuestEntities.removeValue(entity, true)
        ProfileManager.instance.setProperty(entity.entityConfig.entityID, positions)

    }

    fun clearAllMapQuestEntities() {
        currentMap!!.mapQuestEntities.clear()
    }

    fun disableCurrentmapMusic() = currentMap?.unloadMusic()

    fun enableCurrentmapMusic() = currentMap?.loadMusic()

    fun setClosestStartPositionFromScaledUnits(position: Vector2) {
        currentMap!!.setClosestStartPositionFromScaledUnits(position)
    }

    fun addMapEntities(entities: Array<Entity>) {
        currentMap!!.mapEntities.addAll(entities)
    }

    fun getQuestItemSpawnPositions(objectName: String, objectTaskID: String): Array<Vector2> {
        return currentMap!!.getQuestItemSpawnPositions(objectName, objectTaskID)
    }

    fun getQuestDiscoverLayer(): MapLayer? {
        return currentMap!!.questDiscoverLayer
    }

    fun getEnemySpawnLayer(): MapLayer? {
        return currentMap!!.enemySpawnLayer
    }

    fun getCurrentMapType(): MapFactory.MapType {
        return currentMap!!.currentMapType
    }

    fun updateCurrentMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        currentMap!!.updateMapEntities(mapMgr, batch, delta)
    }

    fun updateCurrentMapEffects(mapMgr: MapManager, batch: Batch, delta: Float) {
        currentMap!!.updateMapEffects(mapMgr, batch, delta)
    }

    fun getCurrentMapEntities(): Array<Entity> = currentMap!!.mapEntities

    fun clearCurrentSelectedMapEntity() {
        if (currentSelectedEntity == null) return
        currentSelectedEntity!!.sendMessage(Component.MESSAGE.ENTITY_DESELECTED)
        currentSelectedEntity = null
    }

    fun getCollisionLayer(): MapLayer {
        return currentMap!!.collisionLayer
    }

    fun getPortalLayer(): MapLayer {
        return currentMap!!.portalLayer
    }

    fun getPlayerStartUnitScaled(): Vector2 {
        return currentMap!!.playerStartUnitScaled
    }

    fun getCurrentTiledMap(): TiledMap {
        if (currentMap == null) loadMap(MapFactory.MapType.TOWN)
        return currentMap!!.currentMap
    }

    fun updateLightMaps(timeOfDay: ClockActor.TimeOfDay) {
        if (_timeOfDay != timeOfDay) {
            _currentLightMapOpacity = 0f
            _previousLightMapOpacity = 1f
            _timeOfDay = timeOfDay
            _timeOfDayChanged = true
            previousLightMap = currentLightMap

            Gdx.app.debug(TAG, "Time of Day CHANGED")
        }
        when (timeOfDay) {
            ClockActor.TimeOfDay.DAWN -> currentLightMap = currentMap!!.lightMapDawnLayer
            ClockActor.TimeOfDay.AFTERNOON -> currentLightMap = currentMap!!.lightMapAfternoonLayer
            ClockActor.TimeOfDay.DUSK -> currentLightMap = currentMap!!.lightMapDuskLayer
            ClockActor.TimeOfDay.NIGHT -> currentLightMap = currentMap!!.lightMapNightLayer
            else -> currentLightMap = currentMap!!.lightMapAfternoonLayer
        }
        if (_timeOfDayChanged) {
            if (previousLightMap != null && _previousLightMapOpacity != 0f) {
                previousLightMap!!.opacity = _previousLightMapOpacity
                _previousLightMapOpacity -= 0.5f
                _previousLightMapOpacity = MathUtils.clamp(_previousLightMapOpacity, 0f, 1f)

                if (_previousLightMapOpacity == 0f) {
                    previousLightMap = null
                }
            }

            if (currentLightMap != null && _currentLightMapOpacity != 1f) {
                currentLightMap!!.opacity = _currentLightMapOpacity
                _currentLightMapOpacity += 0.01f
                _currentLightMapOpacity = MathUtils.clamp(_currentLightMapOpacity, 0f, 1f)
            }
        } else {
            if (previousLightMap != null) {
                previousLightMap!!.setOpacity(0f)
            }
            if (currentLightMap != null) {
                currentLightMap!!.setOpacity(1f)
            }
            _timeOfDayChanged = false
        }
    }

    fun setMapChanged(hasMapChanged: Boolean) {
        this.hasMapChanged = hasMapChanged
    }

    companion object {
        private val TAG = MapManager::class.java.simpleName

    }

}
