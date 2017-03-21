package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2

abstract class Map(var currentMapType: MapFactory.MapType,
                   fullMapPath: String) {

    protected var playerStartPositionRect: Vector2 = Vector2(0f, 0f)
    protected var closestPlayerStartPosition: Vector2 = Vector2(0f, 0f)
    protected var convertedUnits: Vector2 = Vector2(0f, 0f)
    lateinit var currentTiledMap: TiledMap
        protected set
    var playerStart: Vector2 = Vector2(0f, 0f)
        protected set

    var collisionLayer: MapLayer
        protected set
    var portalLayer: MapLayer
        protected set
    protected var spawnsLayer: MapLayer

    init {
        Utility.loadMapAsset(fullMapPath)
        if (Utility.isAssetLoaded(fullMapPath)) {
            currentTiledMap = Utility.getMapAsset(fullMapPath)
        } else {
            Gdx.app.debug(TAG, "Map not loaded")
        }

        collisionLayer = currentTiledMap.layers.get(COLLISION_LAYER)
        portalLayer = currentTiledMap.layers.get(PORTAL_LAYER)
        spawnsLayer = currentTiledMap.layers.get(SPAWNS_LAYER)
        setClosestStartPosition(playerStart)

    }

    val playerStartUnitScaled: Vector2
        get() {
            val playerStart = this.playerStart.cpy()
            playerStart.set(this.playerStart.x * UNIT_SCALE, this.playerStart.y * UNIT_SCALE)
            return playerStart
        }

    private fun setClosestStartPosition(position: Vector2) {
        Gdx.app.debug(TAG, "setClosestStartPosition INPUT: (" + position.x + "," + position.y + ") " + currentMapType.toString())

        //Get last known position on this map
        playerStartPositionRect.set(0f, 0f)
        closestPlayerStartPosition.set(0f, 0f)
        var shortestDistance = 0f

        //Go through all player start positions and choose closest to last known position
        for (`object` in spawnsLayer.objects) {
            if (`object`.name.equals(PLAYER_START, ignoreCase = true)) {
                (`object` as RectangleMapObject).rectangle.getPosition(playerStartPositionRect)
                val distance = position.dst2(playerStartPositionRect)

                Gdx.app.debug(TAG, "DISTANCE: " + distance + " for " + currentMapType.toString())

                if (distance < shortestDistance || shortestDistance == 0f) {
                    closestPlayerStartPosition.set(playerStartPositionRect)
                    shortestDistance = distance
                    Gdx.app.debug(TAG, "closest START is: (" + closestPlayerStartPosition.x + "," + closestPlayerStartPosition.y + ") " + currentMapType.toString())
                }
            }
        }
        playerStart = closestPlayerStartPosition.cpy()
    }

    fun setClosestStartPositionFromScaledUnits(position: Vector2) {
        if (UNIT_SCALE <= 0)
            return

        convertedUnits.set(position.x / UNIT_SCALE, position.y / UNIT_SCALE)
        setClosestStartPosition(convertedUnits)
    }

    companion object {
        private val TAG = Map::class.java.simpleName

        val UNIT_SCALE = 1 / 16f

        //Map layers
        protected val COLLISION_LAYER = "MAP_COLLISION_LAYER"
        protected val SPAWNS_LAYER = "MAP_SPAWNS_LAYER"
        protected val PORTAL_LAYER = "MAP_PORTAL_LAYER"

        //Starting locations
        protected val PLAYER_START = "PLAYER_START"
    }
}
