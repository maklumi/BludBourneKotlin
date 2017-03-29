package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import java.util.*

abstract class Map(var currentMapType: MapFactory.MapType,
                   fullMapPath: String) {

    protected var playerStartPositionRect: Vector2 = Vector2(0f, 0f)
    protected var closestPlayerStartPosition: Vector2 = Vector2(0f, 0f)
    protected var convertedUnits: Vector2 = Vector2(0f, 0f)
    lateinit var currentMap: TiledMap
        protected set
    var playerStart: Vector2 = Vector2(0f, 0f)

    var collisionLayer: MapLayer
        protected set
    var portalLayer: MapLayer
        protected set
    var spawnsLayer: MapLayer
        protected set
    var questItemSpawnLayer: MapLayer? = null
        protected set
    var questDiscoverLayer: MapLayer? = null
        protected set
    var enemySpawnLayer: MapLayer? = null
        protected set
    protected val npcStartPositions: Array<Vector2>
    protected val specialNPCStartPositions: Hashtable<String, Vector2>
    protected val json = Json()
    var mapEntities: Array<Entity> = Array(10)
    var mapQuestEntities: Array<Entity> = Array()

    init {
        Utility.loadMapAsset(fullMapPath)
        if (Utility.isAssetLoaded(fullMapPath)) {
            currentMap = Utility.getMapAsset(fullMapPath)
        } else {
            Gdx.app.debug(TAG, "Map not loaded")
        }

        collisionLayer = currentMap.layers.get(COLLISION_LAYER)
        portalLayer = currentMap.layers.get(PORTAL_LAYER)
        spawnsLayer = currentMap.layers.get(SPAWNS_LAYER)
        setClosestStartPosition(playerStart)

        questItemSpawnLayer = currentMap.layers.get(QUEST_ITEM_SPAWN_LAYER)
        questDiscoverLayer = currentMap.layers.get(QUEST_DISCOVER_LAYER)
        enemySpawnLayer = currentMap.layers.get(ENEMY_SPAWN_LAYER)
        if (enemySpawnLayer == null) {
            Gdx.app.debug(TAG, "No enemy layer found!")
        }
        npcStartPositions = getNPCStartPositions()
        specialNPCStartPositions = getExtraNPCStartPositions()
    }

    fun getQuestItemSpawnPositions(objectName: String, objectTaskID: String): Array<Vector2> {
        val objects = Array<MapObject>()
        val positions = Array<Vector2>()

        for (mapObject in questItemSpawnLayer!!.objects) {
            val name = mapObject.name
            val taskID = mapObject.properties.get("taskID") as String

            if (name == null || taskID == null ||
                    name.isEmpty() || taskID.isEmpty() ||
                    !name.equals(objectName, true) ||
                    !taskID.equals(objectTaskID, true)) {
                continue
            }
            //Get center of rectangle
            var x = (mapObject as RectangleMapObject).rectangle.getX()
            var y = mapObject.rectangle.getY()

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE
            y *= UNIT_SCALE

            positions.add(Vector2(x, y))
        }
        return positions
    }

    fun addMapQuestEntities(entities: Array<Entity>) {
        mapQuestEntities.addAll(entities)
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
        for (mapObject in spawnsLayer.objects) {
            if (mapObject.name.equals(PLAYER_START, ignoreCase = true)) {
                if (mapObject.name == null || mapObject.name.isEmpty()) continue
                (mapObject as RectangleMapObject).rectangle.getPosition(playerStartPositionRect)
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

    abstract fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float)

    fun getNPCStartPositions(): Array<Vector2> {
        val positions = Array<Vector2>()

        spawnsLayer.objects.forEach {
            val name = it.name

            if (name == null || name.isEmpty()) return@forEach

            if (name.equals(NPC_START, true)) {
                // get center of rectangle
                it as RectangleMapObject
                var x = it.rectangle.x + it.rectangle.width / 2
                var y = it.rectangle.y + it.rectangle.height / 2

                // convert from map coordinates
                x *= UNIT_SCALE
                y *= UNIT_SCALE

                positions.add(Vector2(x, y))
            }
        }
        return positions
    }

    private fun getExtraNPCStartPositions(): Hashtable<String, Vector2> {
        val positionsTable = Hashtable<String, Vector2>()

        spawnsLayer.objects.forEach {
            val name = it.name

            if (name == null || name.isEmpty()) return@forEach

            // exclude non special characters
            if (name.equals(NPC_START, true) || name.equals(PLAYER_START, true)) return@forEach

            // get center of rectangle
            it as RectangleMapObject
            var x = it.rectangle.x + it.rectangle.width / 2
            var y = it.rectangle.y + it.rectangle.height / 2

            // convert from map coordinates
            x *= UNIT_SCALE
            y *= UNIT_SCALE

            positionsTable.put(name, Vector2(x, y))

        }
        return positionsTable
    }

    companion object {
        private val TAG = Map::class.java.simpleName

        val UNIT_SCALE = 1 / 16f

        //Map layers
        protected val COLLISION_LAYER = "MAP_COLLISION_LAYER"
        protected val SPAWNS_LAYER = "MAP_SPAWNS_LAYER"
        protected val PORTAL_LAYER = "MAP_PORTAL_LAYER"
        protected val QUEST_ITEM_SPAWN_LAYER = "MAP_QUEST_ITEM_SPAWN_LAYER"
        protected val QUEST_DISCOVER_LAYER = "MAP_QUEST_DISCOVER_LAYER"
        protected val ENEMY_SPAWN_LAYER = "MAP_ENEMY_SPAWN_LAYER"

        // starting locations
        protected val NPC_START = "NPC_START"
        protected val PLAYER_START = "PLAYER_START"

    }
}
