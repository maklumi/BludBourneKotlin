package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport

class PlayerHUD(camera: Camera) : Screen {

    val stage: Stage
    private val viewport: Viewport
    private val statusUI: StatusUI

    init {
        viewport = ScreenViewport(camera)

        stage = Stage(viewport)

        statusUI = StatusUI()
        statusUI.setPosition(0f, 0f)

        stage.addActor(statusUI)
    }

    override fun show() {}

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }
}
