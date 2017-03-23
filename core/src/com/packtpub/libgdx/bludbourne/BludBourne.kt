package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen

class BludBourne : Game() {

    private lateinit var mainGameScreen: MainGameScreen

    override fun create() {
        mainGameScreen = MainGameScreen()
        setScreen(mainGameScreen)
    }

    override fun dispose() {
        mainGameScreen.dispose()
    }

}
