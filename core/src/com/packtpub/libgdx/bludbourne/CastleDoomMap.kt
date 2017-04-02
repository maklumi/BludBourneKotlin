package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, CastleDoomMap.mapPath) {

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
        mapQuestEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    override fun unloadMusic() {
    }

    override fun loadMusic() {
    }

    companion object {
        private val mapPath = "maps/castle_of_doom.tmx"
    }

}
