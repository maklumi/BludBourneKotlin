package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.Utility

class GameOverScreen(private val _game: BludBourne) : Screen {
    private val _stage: Stage = Stage()

    init {

        //create
        val continueButton = TextButton("Continue", Utility.STATUSUI_SKIN)
        val mainMenuButton = TextButton("Main Menu", Utility.STATUSUI_SKIN)
        val messageLabel = Label(DEATH_MESSAGE, Utility.STATUSUI_SKIN)
        messageLabel.setWrap(true)

        val gameOverLabel = Label(GAMEOVER, Utility.STATUSUI_SKIN)
        gameOverLabel.setAlignment(Align.center)

        // Layout
        val table = Table().apply {
            // debugAll()
            setFillParent(true)
            add(messageLabel).pad(50f, 50f, 50f, 50f).expandX().fillX()
            row()
            add(gameOverLabel)
            row()
            add(continueButton).pad(50f, 50f, 10f, 50f)
            row()
            add(mainMenuButton).pad(10f, 50f, 50f, 50f)
        }

        _stage.addActor(table)

        //Listeners
        continueButton.addListener(object : ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(BludBourne.ScreenType.LoadGame)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        }
        )

        mainMenuButton.addListener(object : ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(BludBourne.ScreenType.MainMenu)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        }
        )

    }

    override fun render(delta: Float) {
        if (delta == 0f) {
            return
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        _stage.act(delta)
        _stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        _stage.viewport.setScreenSize(width, height)
    }

    override fun show() {
        Gdx.input.inputProcessor = _stage
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        _stage.clear()
        _stage.dispose()
    }

    companion object {
        private val DEATH_MESSAGE = "You have fought bravely, but alas, you have fallen during your epic struggle."
        private val GAMEOVER = "Game Over"
    }
}
