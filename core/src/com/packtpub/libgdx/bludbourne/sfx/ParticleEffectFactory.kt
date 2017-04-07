package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory.ParticleEffectType.*

object ParticleEffectFactory {

    enum class ParticleEffectType {
        CANDLE_FIRE, LAVA_SMOKE, WAND_ATTACK,
        NONE
    }

    fun getParticleEffect(particleEffectType: ParticleEffectType): ParticleEffect? {
        val effect = ParticleEffect()
        when (particleEffectType) {
            CANDLE_FIRE -> {
                effect.load(Gdx.files.internal("sfx/candle.p"), Gdx.files.internal("sfx"))
                return effect
            }
            LAVA_SMOKE -> return null
            WAND_ATTACK -> return null
            else -> return null
        }
    }

}
