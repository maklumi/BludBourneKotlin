package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class LoadGameScreen(private val _game: BludBourne) : GameScreen() {
    private val _stage: Stage = Stage()
    private val _listItems: List<String> = List(Utility.STATUSUI_SKIN, "inventory")

    init {
        //create
        val loadButton = TextButton("Load", Utility.STATUSUI_SKIN)
        val backButton = TextButton("Back", Utility.STATUSUI_SKIN)

        ProfileManager.instance.storeAllProfiles()
        val list = ProfileManager.instance.getProfileList()
        _listItems.setItems(list)
        val scrollPane = ScrollPane(_listItems)

        scrollPane.setOverscroll(false, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setScrollbarsOnTop(true)

        val table = Table()
        val bottomTable = Table()

        //Layout
        table.center()
        table.setFillParent(true)
        table.padBottom(loadButton.height)
        table.add(scrollPane).center()

        bottomTable.height = loadButton.height
        bottomTable.width = Gdx.graphics.width.toFloat()
        bottomTable.center()
        bottomTable.add(loadButton).padRight(50f)
        bottomTable.add(backButton)

        _stage.addActor(table)
        _stage.addActor(bottomTable)

        //Listeners
        backButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                _game.screen = _game.getScreenType(BludBourne.ScreenType.MainMenu)
            }
        }
        )

        loadButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (_listItems.selected == null) return
                val fileName = _listItems.selected.toString()
                val file = ProfileManager.instance.getProfileFile(fileName)
                if (file != null && !fileName.isEmpty()) {
                    ProfileManager.instance.setCurrentProfile(fileName)
                    this@LoadGameScreen.notify(com.packtpub.libgdx.bludbourne.audio.AudioObserver.AudioCommand.MUSIC_STOP, com.packtpub.libgdx.bludbourne.audio.AudioObserver.AudioTypeEvent.MUSIC_TITLE)
                    _game.screen = _game.getScreenType(BludBourne.ScreenType.MainGame)
                }
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
        val list: Array<String> = ProfileManager.instance.getProfileList()
        _listItems.setItems(list)
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

}
