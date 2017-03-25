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

class MapManager : ProfileObserver {

    lateinit var player: Entity //set from MainGameScreen
    var camera = OrthographicCamera()
    var hasMapChanged = false
    private var currentMap: Map = MapFactory.getMap(MapFactory.MapType.TOWN)

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
                profileManager.setProperty("currentMapType", currentMap.currentMapType.toString())
                profileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOP_WORLD).playerStart)
                profileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).playerStart)
                profileManager.setProperty("townMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOWN).playerStart)
            }
        }
    }

    fun loadMap(mapType: MapFactory.MapType) {

        // unregister observers
        val entities : Array<Entity> = currentMap.mapEntities
        entities.forEach(Entity::unregisterObservers)

        currentMap = MapFactory.getMap(mapType)
        hasMapChanged = true

        Gdx.app.debug(TAG, "Player Start: (" + currentMap.playerStart.x + "," + currentMap.playerStart.y + ")")
    }

    fun setClosestStartPositionFromScaledUnits(position: Vector2) {
        currentMap.setClosestStartPositionFromScaledUnits(position)
    }

    fun updateCurrentMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        currentMap.updateMapEntities(mapMgr, batch, delta)
    }

    fun getCurrentMapEntities(): Array<Entity> = currentMap.mapEntities

    fun getCollisionLayer(): MapLayer {
        return currentMap.collisionLayer
    }

    fun getPortalLayer(): MapLayer {
        return currentMap.portalLayer
    }

    fun getPlayerStartUnitScaled(): Vector2 {
        return currentMap.playerStartUnitScaled
    }

    fun getCurrentTiledMap(): TiledMap {
        return currentMap.currentTiledMap
    }

    fun setMapChanged(hasMapChanged: Boolean) {
        this.hasMapChanged = hasMapChanged
    }

    companion object {
        private val TAG = MapManager::class.java.simpleName

    }

}
