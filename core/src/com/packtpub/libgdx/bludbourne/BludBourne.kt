package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen

class BludBourne : Game() {

    override fun create() {
        player = Entity()
        mainGameScreen = MainGameScreen()
        setScreen(mainGameScreen)
    }

    companion object {
        lateinit var mainGameScreen : MainGameScreen
        lateinit var player : Entity
    }

}
