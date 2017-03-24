package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.BludBourne.ScreenType
import com.packtpub.libgdx.bludbourne.Utility

class MainMenuScreen(private val _game: BludBourne) : Screen {

    private val _stage: Stage = Stage()

    init {
        //creation
        val table = Table()
        table.setFillParent(true)

        val title = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("bludbourne_title"))
        val newGameButton = TextButton("New Game", Utility.STATUSUI_SKIN)
        val loadGameButton = TextButton("Load Game", Utility.STATUSUI_SKIN)
        val exitButton = TextButton("Exit", Utility.STATUSUI_SKIN)


        //Layout
        table.add(title).spaceBottom(75f).row()
        table.add(newGameButton).spaceBottom(10f).row()
        table.add(loadGameButton).spaceBottom(10f).row()
        table.add(exitButton).spaceBottom(10f).row()

        _stage.addActor(table)

        //Listeners
        newGameButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                _game.screen = _game.getScreenType(ScreenType.NewGame)
                return true
            }
        }
        )

        loadGameButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                _game.screen = _game.getScreenType(ScreenType.LoadGame)
                return true
            }
        }
        )

        exitButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                Gdx.app.exit()
                return true
            }

        }
        )


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



