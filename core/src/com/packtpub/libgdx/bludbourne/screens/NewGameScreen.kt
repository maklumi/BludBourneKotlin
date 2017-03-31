package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.BludBourne.ScreenType
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.profile.ProfileManager

class NewGameScreen(private val _game: BludBourne) : Screen {

    private val _stage: Stage = Stage()
    private val profileText: TextField
    private val overwriteDialog: Dialog

    init {
        val profileName = Label("Enter Profile Name: ", Utility.STATUSUI_SKIN)

        profileText = TextField("", Utility.STATUSUI_SKIN, "inventory")
        profileText.maxLength = 20


        overwriteDialog = Dialog("Overwrite?", Utility.STATUSUI_SKIN, "solidbackground")
        val overwriteLabel = Label("Overwrite existing profile name?", Utility.STATUSUI_SKIN)
        val cancelButton = TextButton("Cancel", Utility.STATUSUI_SKIN, "inventory")

        val overwriteButton = TextButton("Overwrite", Utility.STATUSUI_SKIN, "inventory")
        overwriteDialog.setKeepWithinStage(true)
        overwriteDialog.isModal = true
        overwriteDialog.isMovable = false
        overwriteDialog.text(overwriteLabel)

        val startButton = TextButton("Start", Utility.STATUSUI_SKIN)
        val backButton = TextButton("Back", Utility.STATUSUI_SKIN)

        //Layout
        overwriteDialog.row()
        overwriteDialog.button(overwriteButton).bottom().left()
        overwriteDialog.button(cancelButton).bottom().right()

        val topTable = Table()
        topTable.setFillParent(true)
        topTable.add(profileName).center()
        topTable.add(profileText).center()

        val bottomTable = Table()
        bottomTable.height = startButton.height
        bottomTable.width = Gdx.graphics.width.toFloat()
        bottomTable.center()
        bottomTable.add(startButton).padRight(50f)
        bottomTable.add(backButton)

        _stage.addActor(topTable)
        _stage.addActor(bottomTable)

        //Listeners
        cancelButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                overwriteDialog.hide()
                return true
            }
        }
        )

        overwriteButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val messageText = profileText.text
                ProfileManager.instance.writeProfileToStorage(messageText, "", true)
                ProfileManager.instance.setCurrentProfile(messageText)
                ProfileManager.instance.saveProfile()
                ProfileManager.instance.loadProfile()
                _game.screen = _game.getScreenType(ScreenType.MainGame)
                return true
            }
        }
        )

        startButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val messageText = profileText.text
                //check to see if the current profile matches one that already exists
                var exists = false

                exists = ProfileManager.instance.doesProfileExist(messageText)

                if (exists) {
                    //Pop up dialog for Overwrite
                    overwriteDialog.show(_stage)
                } else {
                    ProfileManager.instance.writeProfileToStorage(messageText, "", false)
                    ProfileManager.instance.setCurrentProfile(messageText)
                    ProfileManager.instance.saveProfile()
                    ProfileManager.instance.loadProfile()
                    _game.screen = _game.getScreenType(ScreenType.MainGame)
                }

                return true
            }
        }
        )

        backButton.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                _game.screen = _game.getScreenType(ScreenType.MainMenu)
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
        overwriteDialog.hide()
        profileText.text = ""
        Gdx.input.inputProcessor = _stage
    }

    override fun hide() {
        overwriteDialog.hide()
        profileText.text = ""
        Gdx.input.inputProcessor = null
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        _stage.clear()
        _stage.dispose()
    }


}
