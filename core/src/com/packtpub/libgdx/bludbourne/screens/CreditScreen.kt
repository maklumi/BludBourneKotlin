package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.Utility

class CreditScreen(private val _game: BludBourne) : GameScreen() {
    private val _stage: Stage = Stage()
    private var _scrollPane: ScrollPane? = null

    init {
        Gdx.input.inputProcessor = _stage

        //Get text
        val file = Gdx.files.internal(CREDITS_PATH)
        val textString = file.readString()

        val text = Label(textString, Utility.STATUSUI_SKIN, "credits")
        text.setAlignment(Align.top or Align.center)
        text.setWrap(true)

        _scrollPane = ScrollPane(text)
        _scrollPane!!.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _scrollPane!!.scrollY = 0f
                _scrollPane!!.updateVisualScroll()
                _game.screen = _game.getScreenType(BludBourne.ScreenType.MainMenu)
            }
        }
        )

        val table = Table()
        table.center()
        table.setFillParent(true)
        table.defaults().width(Gdx.graphics.width.toFloat())
        table.add<ScrollPane>(_scrollPane)

        _stage.addActor(table)
    }

    override fun render(delta: Float) {
        if (delta == 0f) {
            return
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        _stage.act(delta)
        _stage.draw()

        _scrollPane!!.scrollY = _scrollPane!!.scrollY + delta * 20
    }

    override fun resize(width: Int, height: Int) {
        _stage.viewport.setScreenSize(width, height)
    }

    override fun show() {
        _scrollPane!!.isVisible = true
        Gdx.input.inputProcessor = _stage
    }

    override fun hide() {
        _scrollPane!!.isVisible = false
        _scrollPane!!.scrollY = 0f
        _scrollPane!!.updateVisualScroll()
        Gdx.input.inputProcessor = null
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        _stage.clear()
        _scrollPane = null
        _stage.dispose()
    }

    companion object {
        private val CREDITS_PATH = "licenses/credits.txt"
    }
}