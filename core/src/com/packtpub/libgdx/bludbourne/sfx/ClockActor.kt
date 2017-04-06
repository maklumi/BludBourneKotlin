package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class ClockActor(text: CharSequence, skin: Skin) : Label(text, skin) {
    enum class TimeOfDay {
        DAWN,
        AFTERNOON,
        DUSK,
        NIGHT
    }

    var totalTime = 0f
    var rateOfTime = 1f
    private var isAfternoon = false
    val time = String.format("%02d:%02d %s", 0, 0, if (isAfternoon) PM else AM)

    init {
        this.setText(time)
        this.pack()
    }

    override fun act(delta: Float) {
        totalTime += delta * rateOfTime

        val seconds = MathUtils.floor(totalTime % 60)
        val minutes = MathUtils.floor(totalTime / 60 % 60)
//        val minutes = MathUtils.floor(totalTime / 1 % 60)
        var hours = MathUtils.floor(totalTime / 3600 % 24)
//        var hours = MathUtils.floor(totalTime / 36 % 24)

        isAfternoon = !(hours == 0 || hours / 12 == 0)

        hours %= 12

        if (hours == 0) {
            hours = 12
        }

        val time = String.format("%02d:%02d %s", hours, minutes, if (isAfternoon) PM else AM)

        this.setText(time)
    }

    fun getCurrentTimeOfDay(): TimeOfDay {
        val hours = MathUtils.floor(totalTime / 3600 % 24)
//        val hours = MathUtils.floor(totalTime / 36 % 24)
        when (hours) {
            in 7..9 -> return TimeOfDay.DAWN
            in 10..16 -> return TimeOfDay.AFTERNOON
            in 17..19 -> return TimeOfDay.DUSK
            else -> return TimeOfDay.NIGHT
        }
    }

    companion object {
        private val PM = "PM"
        private val AM = "AM"
    }

}
