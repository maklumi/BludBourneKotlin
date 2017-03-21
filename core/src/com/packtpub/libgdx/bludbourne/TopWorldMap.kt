package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {

    private val mapEntities: Array<Entity> = Array(1)

    init {
        mapEntities.add(EntityFactory.getEntity(EntityFactory.EntityType.NPC))
    }

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    companion object {
        private val _mapPath = "maps/topworld.tmx"
    }
}
