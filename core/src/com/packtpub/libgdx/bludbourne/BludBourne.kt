package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.packtpub.libgdx.bludbourne.screens.LoadGameScreen
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen
import com.packtpub.libgdx.bludbourne.screens.MainMenuScreen
import com.packtpub.libgdx.bludbourne.screens.NewGameScreen

class BludBourne : Game() {

    private lateinit var mainGameScreen: MainGameScreen
    private lateinit var mainMenuScreen: MainMenuScreen
    private lateinit var loadGameScreen: LoadGameScreen
    private lateinit var newGameScreen: NewGameScreen

    override fun create() {
        mainGameScreen = MainGameScreen(this)
        mainMenuScreen = MainMenuScreen(this)
        loadGameScreen = LoadGameScreen(this)
        newGameScreen = NewGameScreen(this)
        setScreen(mainMenuScreen)
    }

    override fun dispose() {
        mainGameScreen.dispose()
        mainMenuScreen.dispose()
        loadGameScreen.dispose()
        newGameScreen.dispose()
    }

    fun getScreenType(screenType: ScreenType): Screen {
        when (screenType) {
            ScreenType.MainMenu -> return mainMenuScreen
            ScreenType.MainGame -> return mainGameScreen
            ScreenType.LoadGame -> return loadGameScreen
            ScreenType.NewGame -> return newGameScreen
        }
    }

    enum class ScreenType {
        MainMenu,
        MainGame,
        LoadGame,
        NewGame
    }

}
