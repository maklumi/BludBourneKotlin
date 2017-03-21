package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.utils.Array


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {

    val mapEntities: Array<Entity> = Array(1)

    init {
        mapEntities.add(EntityFactory.getEntity(EntityFactory.EntityType.NPC))
    }

    companion object {
        private val _mapPath = "maps/topworld.tmx"
    }
}
