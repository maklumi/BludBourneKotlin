package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.audio.AudioManager
import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.audio.AudioSubject
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory
import java.util.*

abstract class Map(var currentMapType: MapFactory.MapType,
                   fullMapPath: String) : AudioSubject {

    private val _observers = Array<AudioObserver>()
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
    var _particleEffectSpawnLayer: MapLayer? = null
        protected set
    var lightMapDawnLayer: MapLayer? = null
        protected set
    var lightMapAfternoonLayer: MapLayer? = null
        protected set
    var lightMapDuskLayer: MapLayer? = null
        protected set
    var lightMapNightLayer: MapLayer? = null
        protected set
    protected val npcStartPositions: Array<Vector2>
    protected val specialNPCStartPositions: Hashtable<String, Vector2>
    protected val json = Json()
    var mapEntities: Array<Entity> = Array(10)
    var mapQuestEntities: Array<Entity> = Array()
    var mapParticleEffects = Array<ParticleEffect>()

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
        lightMapDawnLayer = currentMap.layers.get(LIGHTMAP_DAWN_LAYER)
        if (lightMapDawnLayer == null) {
            Gdx.app.debug(TAG, "No dawn lightmap layer found!")
        }
        lightMapAfternoonLayer = currentMap.layers.get(LIGHTMAP_AFTERNOON_LAYER)
        if (lightMapAfternoonLayer == null) {
            Gdx.app.debug(TAG, "No lightmap layer found!")
        }
        lightMapDuskLayer = currentMap.layers.get(LIGHTMAP_DUSK_LAYER)
        if (lightMapDuskLayer == null) {
            Gdx.app.debug(TAG, "No dusk lightmap layer found!")
        }
        lightMapNightLayer = currentMap.layers.get(LIGHTMAP_NIGHT_LAYER)
        if (lightMapNightLayer == null) {
            Gdx.app.debug(TAG, "No night lightmap layer found!")
        }
        _particleEffectSpawnLayer = currentMap.layers.get(PARTICLE_EFFECT_SPAWN_LAYER)
        if (_particleEffectSpawnLayer == null) {
            Gdx.app.debug(TAG, "No particle effect spawn layer")
        }
        npcStartPositions = getNPCStartPositions()
        specialNPCStartPositions = getExtraNPCStartPositions()

        // Observers
        this.addObserver(AudioManager)
    }

    fun getParticleEffectSpawnPositions(particleEffectType: ParticleEffectFactory.ParticleEffectType): Array<Vector2> {
        val objects = Array<MapObject>()
        val positions = Array<Vector2>()

        for (mapObject in _particleEffectSpawnLayer!!.objects) {
            val name = mapObject.name

            if (name == null || name.isEmpty() ||
                    name != particleEffectType.toString()) {
                continue
            }

            val rect = (mapObject as RectangleMapObject).rectangle
            //Get center of rectangle
            var x = rect.getX() + (rect.getWidth() / 2)
            var y = rect.getY() + (rect.getHeight() / 2)

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE
            y *= UNIT_SCALE

            positions.add(Vector2(x, y))
        }
        return positions
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

    fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        // style 1
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
        // style 2
        for (i in 0..mapQuestEntities.size - 1) {
            mapQuestEntities[i].update(mapMgr, batch, delta)
        }
    }

    fun updateMapEffects(mapMgr: MapManager, batch: Batch, delta: Float) {
        // style 3
        for (particleEffect in mapParticleEffects) {
            batch.begin()
            particleEffect.draw(batch, delta)
            batch.end()
        }
    }

    internal fun dispose() {
        for (i in 0..mapEntities.size - 1) {
            mapEntities.get(i).dispose()
        }
        for (i in 0..mapQuestEntities.size - 1) {
            mapQuestEntities.get(i).dispose()
        }
        for (i in 0..mapParticleEffects.size - 1) {
            mapParticleEffects.get(i).dispose()
        }
    }

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

    abstract fun unloadMusic()
    abstract fun loadMusic()

    override fun addObserver(audioObserver: AudioObserver) {
        _observers.add(audioObserver)
    }

    override fun removeObserver(audioObserver: AudioObserver) {
        _observers.removeValue(audioObserver, true)
    }

    override fun removeAllObservers() {
        _observers.removeAll(_observers, true)
    }

    override fun notify(command: AudioObserver.AudioCommand, event: AudioObserver.AudioTypeEvent) {
        _observers.forEach { it.onNotify(command, event) }
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
        protected val PARTICLE_EFFECT_SPAWN_LAYER = "PARTICLE_EFFECT_SPAWN_LAYER"

        val BACKGROUND_LAYER = "Background_Layer"
        val GROUND_LAYER = "Ground_Layer"
        val DECORATION_LAYER = "Decoration_Layer"

        val LIGHTMAP_DAWN_LAYER = "MAP_LIGHTMAP_LAYER_DAWN"
        val LIGHTMAP_AFTERNOON_LAYER = "MAP_LIGHTMAP_LAYER_AFTERNOON"
        val LIGHTMAP_DUSK_LAYER = "MAP_LIGHTMAP_LAYER_DUSK"
        val LIGHTMAP_NIGHT_LAYER = "MAP_LIGHTMAP_LAYER_NIGHT"

        // starting locations
        protected val NPC_START = "NPC_START"
        protected val PLAYER_START = "PLAYER_START"

    }
}
