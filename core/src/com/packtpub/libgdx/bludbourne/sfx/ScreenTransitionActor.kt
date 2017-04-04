package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class ScreenTransitionActor : Image {
    var transitionColor = Color.BLACK

    constructor() {
        init()
    }

    constructor(color: Color) {
        this.transitionColor = color

        init()
    }

    private fun init() {
        toFront()
        setFillParent(true)

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(transitionColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        clearListeners()
        touchable = Touchable.disabled
    }
}
