package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen

class BludBourne : Game() {

    val mainGameScreen = MainGameScreen()

    override fun create() {
        setScreen(mainGameScreen)
    }


}
