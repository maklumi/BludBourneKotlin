package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.packtpub.libgdx.bludbourne.screens.*

class BludBourne : Game() {

    private lateinit var mainGameScreen: MainGameScreen
    private lateinit var mainMenuScreen: MainMenuScreen
    private lateinit var loadGameScreen: LoadGameScreen
    private lateinit var newGameScreen: NewGameScreen
    private lateinit var gameOverScreen: GameOverScreen
    private lateinit var cutSceneScreen: CutSceneScreen
    private lateinit var creditScreen: CreditScreen

    override fun create() {
        mainGameScreen = MainGameScreen(this)
        mainMenuScreen = MainMenuScreen(this)
        loadGameScreen = LoadGameScreen(this)
        newGameScreen = NewGameScreen(this)
        gameOverScreen = GameOverScreen(this)
        cutSceneScreen = CutSceneScreen(this)
        creditScreen = CreditScreen(this)
        setScreen(mainMenuScreen)
    }

    override fun dispose() {
        mainGameScreen.dispose()
        mainMenuScreen.dispose()
        loadGameScreen.dispose()
        newGameScreen.dispose()
        gameOverScreen.dispose()
        cutSceneScreen.dispose()
        creditScreen.dispose()
    }

    fun getScreenType(screenType: ScreenType): Screen {
        when (screenType) {
            ScreenType.MainMenu -> return mainMenuScreen
            ScreenType.MainGame -> return mainGameScreen
            ScreenType.LoadGame -> return loadGameScreen
            ScreenType.NewGame -> return newGameScreen
            ScreenType.GameOver -> return gameOverScreen
            ScreenType.WatchIntro -> return cutSceneScreen
            ScreenType.Credits -> return creditScreen
        }
    }

    enum class ScreenType {
        MainMenu,
        MainGame,
        LoadGame,
        NewGame,
        GameOver,
        WatchIntro,
        Credits
    }

}
