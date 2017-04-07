package com.packtpub.libgdx.bludbourne

import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {

    val lanternEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE)

    init {
        for (position in lanternEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE, position))
        }
    }

    override fun unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD)
    }

    override fun loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD)
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD)
    }

    companion object {
        private val _mapPath = "maps/topworld.tmx"
    }
}
