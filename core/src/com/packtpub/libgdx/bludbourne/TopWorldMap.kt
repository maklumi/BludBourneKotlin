package com.packtpub.libgdx.bludbourne

import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {

    init {
        val lanternEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE)
        for (position in lanternEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE, position))
        }

        val lavaSmokeEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.LAVA_SMOKE)
        for (position in lavaSmokeEffectPositions) {
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.LAVA_SMOKE, position))
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
