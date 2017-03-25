package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.PlayerHUD
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class MainGameScreen(game: BludBourne) : Screen {
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
    private var mapMgr: MapManager
    private var mapRenderer: OrthogonalTiledMapRenderer
    private val camera: OrthographicCamera = OrthographicCamera()
    private val hudCamera: OrthographicCamera = OrthographicCamera()
    private val json = Json()

    enum class GameState {
        RUNNING,
        PAUSED
    }

    val multiplexer: InputMultiplexer

    companion object {
        var gameState: GameState = GameState.RUNNING
            set(gameState) {
                when (gameState) {
                    MainGameScreen.GameState.RUNNING -> {
                        field = GameState.RUNNING
                        ProfileManager.instance.loadProfile()
                    }
                    MainGameScreen.GameState.PAUSED -> if (field == GameState.PAUSED) {
                        field = GameState.RUNNING
                    } else if (field == GameState.RUNNING) {
                        field = GameState.PAUSED
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
        mapRenderer.setView(camera)
        mapMgr.camera = camera

        Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.unitScale)

        player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER)
        mapMgr.player = player

        hudCamera.setToOrtho(false, physicalWidth, physicalHeight)
        playerHUD = PlayerHUD(hudCamera, player)

        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(playerHUD.stage)
        multiplexer.addProcessor(player.inputComponent)
        Gdx.input.inputProcessor = multiplexer

        ProfileManager.instance.addObserver(playerHUD)
        ProfileManager.instance.addObserver(mapMgr)

        player.registerObserver(playerHUD)

    }

    override fun show() {
        gameState = GameState.RUNNING
        Gdx.input.inputProcessor = multiplexer
    }

    override fun hide() {
        gameState = GameState.PAUSED
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        if (gameState == GameState.PAUSED) {
            player.updateInput(delta)
            return
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)

//        mapRenderer.batch.enableBlending()
//        mapRenderer.batch.setBlendFunction(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        if (mapMgr.hasMapChanged) {
            mapRenderer.map = mapMgr.getCurrentTiledMap()
            player.sendMessage(Component.MESSAGE.INIT_START_POSITION,
                    json.toJson(mapMgr.getPlayerStartUnitScaled()))

            camera.position.set(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y, 0f)
            camera.update()

            // register observers
            val entities = mapMgr.getCurrentMapEntities()
            entities.forEach { entity -> entity.registerObserver(playerHUD) }

            mapMgr.hasMapChanged = false

        }

        mapRenderer.render()

        mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.batch, delta)

        player.update(mapMgr, mapRenderer.batch, delta)
        playerHUD.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        setupViewport(10, 10)
        camera.setToOrtho(false, viewportWidth, viewportHeight)
        playerHUD.resize(physicalWidth.toInt(), physicalHeight.toInt())
    }

    override fun pause() {
        gameState = GameState.PAUSED
        ProfileManager.instance.saveProfile()
    }

    override fun resume() {
        gameState = GameState.RUNNING
        ProfileManager.instance.loadProfile()
    }

    override fun dispose() {
        player.unregisterObservers()
        player.dispose()
        mapRenderer.dispose()
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
