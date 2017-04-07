package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory.ParticleEffectType.*

object ParticleEffectFactory {

    enum class ParticleEffectType(val fullFilePath: String) {
        CANDLE_FIRE("sfx/candle.p"),
        LANTERN_FIRE("sfx/candle.p"),
        LAVA_SMOKE("sfx/smoke.p"),
        WAND_ATTACK("sfx/magic_attack.p"),
        NONE("")
    }

    private val SFX_ROOT_DIR = "sfx"

    fun getParticleEffect(particleEffectType: ParticleEffectType, positionX: Float, positionY: Float): ParticleEffect? {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal(particleEffectType.fullFilePath), Gdx.files.internal(SFX_ROOT_DIR))
        effect.setPosition(positionX, positionY)
        when (particleEffectType) {
            CANDLE_FIRE -> {
                effect.scaleEffect(.04f)
            }
            LANTERN_FIRE -> {
                effect.scaleEffect(.02f)
            }
            LAVA_SMOKE -> {
                effect.scaleEffect(.04f)
            }
            WAND_ATTACK -> {
                effect.scaleEffect(1.0f)
            }
            else -> {
            }
        }
        effect.start()
        return effect
    }

    fun getParticleEffect(particleEffectType: ParticleEffectType, position: Vector2): ParticleEffect? {
        return getParticleEffect(particleEffectType, position.x, position.y)
    }
}
