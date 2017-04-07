package com.packtpub.libgdx.bludbourne.sfx


import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class ClockActor : Label {

    enum class TimeOfDay {
        DAWN,
        AFTERNOON,
        DUSK,
        NIGHT
    }

    var totalTime = 0f
    var rateOfTime = 1f
    private var _isAfternoon = false

    constructor(text: CharSequence, skin: Skin) : super(text, skin) {
        init()
    }

    constructor(text: CharSequence, skin: Skin, styleName: String) : super(text, skin, styleName) {
        init()
    }

    constructor(text: CharSequence, skin: Skin, fontName: String, color: Color) : super(text, skin, fontName, color) {
        init()
    }

    constructor(text: CharSequence, skin: Skin, fontName: String, colorName: String) : super(text, skin, fontName, colorName) {
        init()
    }

    constructor(text: CharSequence, style: Label.LabelStyle) : super(text, style) {
        init()
    }

    private fun init() {
        val time = String.format(FORMAT, 0, 0, if (_isAfternoon) PM else AM)
        this.setText(time)
        this.pack()
    }

    val currentTimeOfDay: TimeOfDay
        get() {
            val hours = currentTimeHours
            if (hours in 7..9) {
                return TimeOfDay.DAWN
            } else if (hours in 10..16) {
                return TimeOfDay.AFTERNOON
            } else if (hours in 17..19) {
                return TimeOfDay.DUSK
            } else {
                return TimeOfDay.NIGHT
            }
        }

    override fun act(delta: Float) {
        totalTime += delta * rateOfTime

        val seconds = currentTimeSeconds
        val minutes = currentTimeMinutes
        var hours = currentTimeHours

        _isAfternoon = !(hours == 24 || hours / 12 == 0)

        hours %= 12

        if (hours == 0) {
            hours = 12
        }

        val time = String.format(FORMAT, hours, minutes, if (_isAfternoon) PM else AM)
        this.setText(time)
    }

    val currentTimeSeconds: Int
        get() = MathUtils.floor(totalTime % 60)

    val currentTimeMinutes: Int
        get() = MathUtils.floor(totalTime / 60 % 60)

    val currentTimeHours: Int
        get() {
            var hours = MathUtils.floor(totalTime / 3600 % 24)

            if (hours == 0) {
                hours = 24
            }

            return hours
        }

    companion object {
        private val PM = "PM"
        private val AM = "AM"
        private val FORMAT = "%02d:%02d %s"
    }

}
