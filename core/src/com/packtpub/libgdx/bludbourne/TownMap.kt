package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array


class TownMap : Map(MapFactory.MapType.TOWN, TownMap._mapPath) {

    private val mapEntities = Array<Entity>(4)

    init {
        npcStartPositions.forEach { position ->
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.apply {
                loadConfig(townGuardWalking)
                sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
                sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))
            }
            mapEntities.add(entity)
        }
    }

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    companion object {
        private val _mapPath = "maps/town.tmx"
        private val townGuardWalking = "scripts/town_guard_walking.json"

    }
}
