package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionAction.ScreenTransitionType.*

class ScreenTransitionAction : Action {
    enum class ScreenTransitionType {
        FADE_IN,
        FADE_OUT,
        NONE
    }

    var transitionType = NONE
    var transitionDuration = 3f

    constructor() {}

    constructor(type: ScreenTransitionType, duration: Float) {
        this.transitionType = type
        this.transitionDuration = duration
    }

    override fun act(delta: Float): Boolean {
        val actor = getTarget() ?: return false
        when (transitionType) {
            FADE_IN -> {
                val fadeIn = Actions.sequence(
                        Actions.alpha(1f),
                        Actions.fadeOut(transitionDuration))
                actor.addAction(fadeIn)
            }
            FADE_OUT -> {
                val fadeOut = Actions.sequence(
                        Actions.alpha(0f),
                        Actions.fadeIn(transitionDuration))
                actor.addAction(fadeOut)
            }
            NONE -> {
            }
            else -> {
            }
        }
        return true
    }
}
