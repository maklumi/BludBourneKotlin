package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.packtpub.libgdx.bludbourne.audio.AudioObserver

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, CastleDoomMap.mapPath) {

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(mapMgr, batch, delta) }
        mapQuestEntities.forEach { it.update(mapMgr, batch, delta) }
    }

    override fun unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_CASTLEDOOM)
    }

    override fun loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_CASTLEDOOM)
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_CASTLEDOOM)
    }

    companion object {
        private val mapPath = "maps/castle_of_doom.tmx"
    }

}
