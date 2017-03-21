package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array


class TownMap : Map(MapFactory.MapType.TOWN, TownMap.mapPath) {

    private val mapEntities = Array<Entity>(4)

    init {
        npcStartPositions.forEach { position ->
            mapEntities.add(initEntity(position, townGuardWalking))
        }

        // Special cases
        mapEntities.add(initSpecialEntity(TOWNBLACKSMITH, townBlacksmith))
        mapEntities.add(initSpecialEntity(TOWNMAGE, townMage))
        mapEntities.add(initSpecialEntity(TOWNINNKEEPER, townInnKeeper))
    }

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    private fun initEntity(position: Vector2, configFile: String): Entity {
        val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
        entity.apply {
            loadConfig(configFile)
            sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
            sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))
            sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.entityConfig.state))
            sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.entityConfig.direction))
        }
        return entity
    }

    private fun initSpecialEntity(positionName: String, configFile: String): Entity {
        var position = Vector2(0f, 0f)

        if (specialNPCStartPositions.containsKey(positionName)) {
            position = specialNPCStartPositions[positionName]!!
        }
        return initEntity(position, configFile)
    }

    companion object {
        private val mapPath = "maps/town.tmx"
        private val townGuardWalking = "scripts/town_guard_walking.json"

        private val townBlacksmith = "scripts/town_blacksmith.json"
        private val TOWNBLACKSMITH = "TOWN_BLACKSMITH"

        private val townMage = "scripts/town_mage.json"
        private val TOWNMAGE = "TOWN_MAGE"

        private val townInnKeeper = "scripts/town_innkeeper.json"
        private val TOWNINNKEEPER = "TOWN_INNKEEPER"
    }
}
