package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.BludBourne.ScreenType
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.audio.AudioObserver

class MainMenuScreen(private val _game: BludBourne) : GameScreen() {

    private val _stage: Stage = Stage()

    init {
        val title = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("bludbourne_title"))
        val newGameButton = TextButton("New Game", Utility.STATUSUI_SKIN)
        val loadGameButton = TextButton("Load Game", Utility.STATUSUI_SKIN)
        val watchIntroButton = TextButton("Watch Intro", Utility.STATUSUI_SKIN)
        val creditsButton = TextButton("Credits", Utility.STATUSUI_SKIN)
        val exitButton = TextButton("Exit", Utility.STATUSUI_SKIN)


        //Layout
        val table = Table().apply {
            setFillParent(true)
            add(title).spaceBottom(75f).row()
            add(newGameButton).spaceBottom(10f).row()
            add(loadGameButton).spaceBottom(10f).row()
            add(watchIntroButton).spaceBottom(10f).row()
            add(creditsButton).spaceBottom(10f).row()
            add(exitButton).spaceBottom(10f).row()
        }

        _stage.addActor(table)

        //Listeners
        newGameButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(ScreenType.NewGame)
            }
        }
        )

        loadGameButton.addListener(object : ClickListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(ScreenType.LoadGame)
            }
        }
        )

        exitButton.addListener(object : ClickListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                Gdx.app.exit()
            }

        }
        )

        watchIntroButton.addListener(object : ClickListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                this@MainMenuScreen.notify(com.packtpub.libgdx.bludbourne.audio.AudioObserver.AudioCommand.MUSIC_STOP, com.packtpub.libgdx.bludbourne.audio.AudioObserver.AudioTypeEvent.MUSIC_TITLE)
                _game.screen = _game.getScreenType(ScreenType.WatchIntro)
            }

        }
        )

        creditsButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(ScreenType.Credits)
            }

        }
        )

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TITLE)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        _stage.act(delta)
        _stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        _stage.viewport.setScreenSize(width, height)
    }

    override fun show() {
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE)
        Gdx.input.inputProcessor = _stage
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        _stage.dispose()
    }

}



