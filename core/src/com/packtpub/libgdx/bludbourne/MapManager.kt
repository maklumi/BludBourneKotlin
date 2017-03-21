package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2

class MapManager {

    var camera = OrthographicCamera()
    var hasMapChanged = false
    private var currentMap: Map = MapFactory.getMap(MapFactory.MapType.TOWN)

    fun loadMap(mapType: MapFactory.MapType) {
        currentMap = MapFactory.getMap(mapType)
        hasMapChanged = true

        Gdx.app.debug(TAG, "Player Start: (" + currentMap.playerStart.x + "," + currentMap.playerStart.y + ")")
    }

    fun setClosestStartPositionFromScaledUnits(position: Vector2) {
        currentMap.setClosestStartPositionFromScaledUnits(position)
    }

    fun updateCurrentMapEntities(mapMgr: MapManager, batch: Batch, delta: Float){
        currentMap.updateMapEntities(mapMgr, batch, delta)
    }

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
