package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
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

    fun getCurrentLightMapLayer(timeOfDay: ClockActor.TimeOfDay): MapLayer? {
        when (timeOfDay) {
            ClockActor.TimeOfDay.DAWN -> return currentMap!!.lightMapDawnLayer
            ClockActor.TimeOfDay.AFTERNOON -> return null
            ClockActor.TimeOfDay.DUSK -> return currentMap!!.lightMapDuskLayer
            ClockActor.TimeOfDay.NIGHT -> return currentMap!!.lightMapNightLayer
        }
    }

    fun setMapChanged(hasMapChanged: Boolean) {
        this.hasMapChanged = hasMapChanged
    }

    companion object {
        private val TAG = MapManager::class.java.simpleName

    }

}
