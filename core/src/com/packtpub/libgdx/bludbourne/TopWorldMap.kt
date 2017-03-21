package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    companion object {
        private val _mapPath = "maps/topworld.tmx"
    }
}
