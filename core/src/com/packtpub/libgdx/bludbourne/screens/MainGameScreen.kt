package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.Map.Companion.BACKGROUND_LAYER
import com.packtpub.libgdx.bludbourne.Map.Companion.DECORATION_LAYER
import com.packtpub.libgdx.bludbourne.Map.Companion.GROUND_LAYER
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.PlayerHUD
import com.packtpub.libgdx.bludbourne.audio.AudioManager
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


open class MainGameScreen(val game: BludBourne) : GameScreen() {
    private val TAG = MainGameScreen::class.java.simpleName

    internal var viewportWidth: Float = 0f
    internal var viewportHeight: Float = 0f
    internal var virtualWidth: Float = 0f
    internal var virtualHeight: Float = 0f
    internal var physicalWidth: Float = 0f
    internal var physicalHeight: Float = 0f
    internal var aspectRatio: Float = 0f

    private var player: Entity
    private var playerHUD: PlayerHUD
    var mapMgr: MapManager
    var mapRenderer: OrthogonalTiledMapRenderer
    val camera: OrthographicCamera = OrthographicCamera()
    val hudCamera: OrthographicCamera = OrthographicCamera()
    private val json = Json()

    enum class GameState {
        SAVING,
        LOADING,
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    val multiplexer: InputMultiplexer

    companion object {
        var gameState: GameState = GameState.RUNNING
            set(gameState) {
                when (gameState) {
                    MainGameScreen.GameState.RUNNING -> {
                        field = GameState.RUNNING
                    }
                    GameState.LOADING -> {
                        ProfileManager.instance.loadProfile()
                        field = GameState.RUNNING
                    }
                    GameState.SAVING -> {
                        ProfileManager.instance.saveProfile()
                        field = GameState.PAUSED
                    }
                    MainGameScreen.GameState.PAUSED -> if (field == GameState.PAUSED) {
                        field = GameState.RUNNING
                    } else if (field == GameState.RUNNING) {
                        field = GameState.PAUSED
                    }
                    GameState.GAME_OVER -> {
                        field = GameState.GAME_OVER
                    }
                    else -> field = GameState.RUNNING
                }
            }
    }

    init {
        setupViewport(10, 10)
        mapMgr = MapManager()

        //get the current size
        camera.setToOrtho(false, viewportWidth, viewportHeight)

        mapRenderer = OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), UNIT_SCALE)

        player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER)
        mapMgr.player = player
        mapMgr.camera = camera


        hudCamera.setToOrtho(false, physicalWidth, physicalHeight)
        playerHUD = PlayerHUD(hudCamera, player, mapMgr)

        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(playerHUD.stage)
        multiplexer.addProcessor(player.inputComponent)
        Gdx.input.inputProcessor = multiplexer

        ProfileManager.instance.addObserver(playerHUD)
        ProfileManager.instance.addObserver(mapMgr)

        player.registerObserver(playerHUD)

    }

    override fun show() {
        ProfileManager.instance.addObserver(mapMgr)
        ProfileManager.instance.addObserver(playerHUD)
        gameState = GameState.LOADING
        Gdx.input.inputProcessor = multiplexer
    }

    override fun hide() {
        if (gameState != GameState.GAME_OVER) {
            gameState = GameState.SAVING
        }
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        if (gameState == GameState.GAME_OVER) {
            game.screen = game.getScreenType(BludBourne.ScreenType.GameOver)
        }
        if (gameState == GameState.PAUSED) {
            player.updateInput(delta)
            playerHUD.render(delta)
            return
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)

        mapRenderer.batch.enableBlending()
        mapRenderer.batch.setBlendFunction(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        if (mapMgr.hasMapChanged) {
            mapRenderer.map = mapMgr.getCurrentTiledMap()
            player.sendMessage(Component.MESSAGE.INIT_START_POSITION,
                    json.toJson(mapMgr.getPlayerStartUnitScaled()))

            camera.position.set(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y, 0f)
            camera.update()

            playerHUD.updateEntityObservers()

            mapMgr.hasMapChanged = false

            playerHUD.addTransitionToScreen()

        }

        mapMgr.updateLightMaps(playerHUD.getCurrentTimeOfDay())
        val lightMap = mapMgr.currentLightMap
        val previousLightMap = mapMgr.previousLightMap

        if (lightMap != null) {
            val backgroundMapLayer = mapMgr.getCurrentTiledMap().layers.get(BACKGROUND_LAYER)
            val groundMapLayer = mapMgr.getCurrentTiledMap().layers.get(GROUND_LAYER)
            val decorationMapLayer = mapMgr.getCurrentTiledMap().layers.get(DECORATION_LAYER)

            mapRenderer.batch.begin()
            backgroundMapLayer?.let {
                mapRenderer.renderTileLayer(backgroundMapLayer as TiledMapTileLayer)
            }
            groundMapLayer?.let {
                mapRenderer.renderTileLayer(groundMapLayer as TiledMapTileLayer)
            }
            decorationMapLayer?.let {
                mapRenderer.renderTileLayer(decorationMapLayer as TiledMapTileLayer)
            }
            mapRenderer.batch.end()

            mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.batch, delta)
            player.update(mapMgr, mapRenderer.batch, delta)
            mapMgr.updateCurrentMapEffects(mapMgr, mapRenderer.batch, delta)

            mapRenderer.batch.begin()
            mapRenderer.batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR)
            mapRenderer.renderImageLayer(lightMap as TiledMapImageLayer)
            mapRenderer.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            mapRenderer.batch.end()

            previousLightMap?.let {
                mapRenderer.batch.begin()
                mapRenderer.batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR)
                mapRenderer.renderImageLayer(previousLightMap as TiledMapImageLayer)
                mapRenderer.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                mapRenderer.batch.end()
            }

        } else {
            mapRenderer.render()
            mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.batch, delta)
            player.update(mapMgr, mapRenderer.batch, delta)
            mapMgr.updateCurrentMapEffects(mapMgr, mapRenderer.batch, delta)
        }

        playerHUD.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        setupViewport(10, 10)
        camera.setToOrtho(false, viewportWidth, viewportHeight)
        playerHUD.resize(physicalWidth.toInt(), physicalHeight.toInt())
    }

    override fun pause() {
        gameState = GameState.PAUSED
        playerHUD.pause()
    }

    override fun resume() {
        gameState = GameState.LOADING
        playerHUD.resume()
    }

    override fun dispose() {
        player.unregisterObservers()
        player.dispose()
        mapRenderer.dispose()
        AudioManager.dispose()
        MapFactory.clearCache()
    }

    private fun setupViewport(width: Int, height: Int) {
        //Make the viewport a percentage of the total display area
        virtualWidth = width.toFloat()
        virtualHeight = height.toFloat()

        //Current viewport dimensions
        viewportWidth = virtualWidth
        viewportHeight = virtualHeight

        //pixel dimensions of display
        physicalWidth = Gdx.graphics.width.toFloat()
        physicalHeight = Gdx.graphics.height.toFloat()

        //aspect ratio for current viewport
        aspectRatio = virtualWidth / virtualHeight

        //update viewport if there could be skewing
        if (physicalWidth / physicalHeight >= aspectRatio) {
            //Letterbox left and right
            viewportWidth = viewportHeight * (physicalWidth / physicalHeight)
            viewportHeight = virtualHeight
        } else {
            //letterbox above and below
            viewportWidth = virtualWidth
            viewportHeight = viewportWidth * (physicalHeight / physicalWidth)
        }

        Gdx.app.debug(TAG, "WorldRenderer: virtual: ($virtualWidth,$virtualHeight)")
        Gdx.app.debug(TAG, "WorldRenderer: viewport: ($viewportWidth,$viewportHeight)")
        Gdx.app.debug(TAG, "WorldRenderer: physical: ($physicalWidth,$physicalHeight)")
    }

}
