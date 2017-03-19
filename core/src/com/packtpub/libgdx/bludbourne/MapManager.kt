package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import java.util.*

class MapManager {

    //All maps for the game
    private val mapTable: Hashtable<String, String> = Hashtable()
    private val playerStartLocationTable: Hashtable<String, Vector2> = Hashtable()
    private val playerStart: Vector2 = Vector2(0f, 0f)
    private var currentMapName: String = TOWN
    var currentMap: TiledMap

    lateinit var collisionLayer: MapLayer
        private set
    lateinit var portalLayer: MapLayer
        private set
    lateinit var spawnsLayer: MapLayer

    init {
        mapTable.put(TOP_WORLD, "maps/topworld.tmx")
        mapTable.put(TOWN, "maps/town.tmx")
        mapTable.put(CASTLE_OF_DOOM, "maps/castle_of_doom.tmx")

        playerStartLocationTable.put(TOP_WORLD, playerStart.cpy())
        playerStartLocationTable.put(TOWN, playerStart.cpy())
        playerStartLocationTable.put(CASTLE_OF_DOOM, playerStart.cpy())

        currentMap = loadMap(currentMapName)
    }

    fun loadMap(mapName: String): TiledMap {
//        playerStart.set(0f, 0f)

        val mapFullPath: String = mapTable[mapName]!!

//        currentMap.dispose()

        Utility.loadMapAsset(mapFullPath)
        if (Utility.isAssetLoaded(mapFullPath)) {
            currentMap = Utility.getMapAsset(mapFullPath)
            currentMapName = mapName
        }

        collisionLayer = currentMap.layers.get(MAP_COLLISION_LAYER)
        portalLayer = currentMap.layers.get(MAP_PORTAL_LAYER)
        spawnsLayer = currentMap.layers.get(MAP_SPAWNS_LAYER)

        var start = playerStartLocationTable[currentMapName]!!
        if (start.isZero) {
            setClosestStartPosition(playerStart)
            start = playerStartLocationTable[currentMapName]!!
        }
        playerStart.set(start.x, start.y)


        Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")")
        return currentMap
    }

    val playerStartUnitScaled: Vector2
        get() {
            val playerStart = playerStart.cpy()
            playerStart.set(this.playerStart.x * UNIT_SCALE, this.playerStart.y * UNIT_SCALE)
            return playerStart
        }

    private fun setClosestStartPosition(position: Vector2) {
        Gdx.app.debug(TAG, "setClosestStartPosition INPUT: (" + position.x + "," + position.y + ") " + currentMapName)

        //Get last known position on this map
        val playerStartPositionRect = Vector2(0f, 0f)
        val closestPlayerStartPosition = Vector2(0f, 0f)
        var shortestDistance = 0f

        //Go through all player start positions and choose closest to last known position
        for (`object` in spawnsLayer.objects) {
            if (`object`.name.equals(PLAYER_START, ignoreCase = true)) {
                (`object` as RectangleMapObject).rectangle.getPosition(playerStartPositionRect)
                val distance = position.dst2(playerStartPositionRect)

                Gdx.app.debug(TAG, "distance: $distance for $currentMapName")

                if (distance < shortestDistance || shortestDistance == 0f) {
                    closestPlayerStartPosition.set(playerStartPositionRect)
                    shortestDistance = distance
                    Gdx.app.debug(TAG, "closest START is: (" + closestPlayerStartPosition.x + "," + closestPlayerStartPosition.y + ") " + currentMapName)
                }
            }
        }
        playerStartLocationTable.put(currentMapName, closestPlayerStartPosition.cpy())
    }

    fun setClosestStartPositionFromScaledUnits(position: Vector2) {
        val convertedUnits = Vector2(position.x / UNIT_SCALE, position.y / UNIT_SCALE)
        setClosestStartPosition(convertedUnits)
    }

    companion object {
        private val TAG = MapManager::class.java.simpleName

        //maps
        private val TOP_WORLD = "TOP_WORLD"
        private val TOWN = "TOWN"
        private val CASTLE_OF_DOOM = "CASTLE_OF_DOOM"

        //Map layers
        private val MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER"
        private val MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER"
        private val MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER"

        private val PLAYER_START = "PLAYER_START"

        val UNIT_SCALE = 1 / 16f
    }

}
