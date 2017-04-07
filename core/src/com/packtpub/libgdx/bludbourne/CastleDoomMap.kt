package com.packtpub.libgdx.bludbourne

import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, CastleDoomMap.mapPath) {

    init {
        val candleEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE)
        for (position in candleEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE, position))
        }

        val lavaSmokeEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.LAVA_SMOKE)
        for (position in lavaSmokeEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.LAVA_SMOKE, position))
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
