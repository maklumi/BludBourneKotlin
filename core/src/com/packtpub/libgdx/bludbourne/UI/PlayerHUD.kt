package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport

class PlayerHUD(camera: Camera) : Screen {

    companion object {
        val STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas"
    }

    val stage: Stage
    private val viewport: Viewport
    private val statusUI: StatusUI

    private val statusUISkin: Skin
    private val statusUITextureAtlas: TextureAtlas

    init {
        viewport = ScreenViewport(camera)
        stage = Stage(viewport)
        stage.setDebugAll(true)

        statusUITextureAtlas = TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH)
        statusUISkin = Skin(Gdx.files.internal("skins/statusui.json"), statusUITextureAtlas)

        statusUI = StatusUI(statusUISkin, statusUITextureAtlas)
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
