package com.packtpub.libgdx.bludbourne

import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, CastleDoomMap.mapPath) {

    val candleEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE)

    init {
        for (position in candleEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE, position))
        }
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
