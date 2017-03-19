package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen

class BludBourne : Game() {

    override fun create() {
        player = Entity()
        player.init(20f,20f)
        setScreen(mainGameScreen)
    }

    companion object {
        val mainGameScreen = MainGameScreen()
        lateinit var player : Entity
    }

}
