package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array


class TownMap : Map(MapFactory.MapType.TOWN, TownMap.mapPath) {

    private val mapEntities = Array<Entity>(4)

    init {
        npcStartPositions.forEach { position ->
            mapEntities.add(initEntity(Entity.getEntityConfig(townGuardWalking), position))
        }

        // Special cases
        mapEntities.add(initSpecialEntity(Entity.getEntityConfig(townBlacksmith)))
        mapEntities.add(initSpecialEntity(Entity.getEntityConfig(townMage)))
        mapEntities.add(initSpecialEntity(Entity.getEntityConfig(townInnKeeper)))

        // When we have multiple configs in one file
        val configs = Entity.getEntityConfigs(townFolk)
        configs.forEach { mapEntities.add(initSpecialEntity(it)) }

    }

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    private fun initEntity(config: EntityConfig, position: Vector2): Entity {
        val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
        entity.apply {
            entityConfig = config
            sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
            sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))
            sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.entityConfig.state))
            sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.entityConfig.direction))
        }
        return entity
    }

    private fun initSpecialEntity(entityConfig: EntityConfig): Entity {
        var position = Vector2(0f, 0f)

        if (specialNPCStartPositions.containsKey(entityConfig.entityID)) {
            position = specialNPCStartPositions[entityConfig.entityID]!!
        }
        return initEntity(entityConfig, position)
    }

    companion object {
        private val mapPath = "maps/town.tmx"
        private val townGuardWalking = "scripts/town_guard_walking.json"
        private val townBlacksmith = "scripts/town_blacksmith.json"
        private val townMage = "scripts/town_mage.json"
        private val townInnKeeper = "scripts/town_innkeeper.json"
        private val townFolk = "scripts/town_folk.json"
    }
}
