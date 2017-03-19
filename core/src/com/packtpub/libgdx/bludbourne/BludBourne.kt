package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen

class BludBourne : Game() {

    override fun create() {
        mainGameScreen = MainGameScreen()
        setScreen(mainGameScreen)
    }

    override fun dispose() {
        mainGameScreen.dispose()
    }

    companion object {
        lateinit var mainGameScreen : MainGameScreen
    }

}
