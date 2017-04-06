package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class ClockActor(text: CharSequence, skin: Skin) : Label(text, skin) {
    private var _totalTime = 0f
    var rateOfTime = 1f
    private var isAfternoon = false
    val time = String.format("%02d:%02d %s", 0, 0, if (isAfternoon) PM else AM)

    init {
        this.setText(time)
        this.pack()
    }

    override fun act(delta: Float) {
        _totalTime += delta * rateOfTime

        val seconds = MathUtils.floor(_totalTime % 60)
        val minutes = MathUtils.floor(_totalTime / 60 % 60)
        var hours = MathUtils.floor(_totalTime / 3600 % 24)

        isAfternoon = !(hours == 0 || hours / 12 == 0)

        hours %= 12

        if (hours == 0) {
            hours = 12
        }

        val time = String.format("%02d:%02d %s", hours, minutes, if (isAfternoon) PM else AM)

        this.setText(time)
    }

    companion object {
        private val PM = "PM"
        private val AM = "AM"
    }

}
