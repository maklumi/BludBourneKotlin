package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.MapManager


class MainGameScreen : Screen {
    private val TAG = MainGameScreen::class.java.simpleName

    internal var viewportWidth: Float = 0f
    internal var viewportHeight: Float = 0f
    internal var virtualWidth: Float = 0f
    internal var virtualHeight: Float = 0f
    internal var physicalWidth: Float = 0f
    internal var physicalHeight: Float = 0f
    internal var aspectRatio: Float = 0f

    private val player = Entity()
    private val mapMgr = MapManager()
    private lateinit var mapRenderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera

    override fun show() {
        setupViewport(10, 10)

        //get the current size
        camera = OrthographicCamera()
        camera.setToOrtho(false, viewportWidth, viewportHeight)

        mapRenderer = OrthogonalTiledMapRenderer(mapMgr.currentMap, MapManager.UNIT_SCALE)
        mapRenderer.setView(camera)
    }


    override fun hide() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)
        mapRenderer.render()

        player.update(mapMgr, mapRenderer.batch, delta)

        updatePortalLayerActivation(player.boundingBox)

        // lock and center the camera to player's position
        camera.position.set(player.currentPlayerPosition.x, player.currentPlayerPosition.y, 0f)
        camera.update()

    }

    private fun updatePortalLayerActivation(boundingBox: Rectangle): Boolean {
        // portal layer specifies its name as the layer to go
        val portalLayer = mapMgr.portalLayer

        portalLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle)) {
                val mapName = it.getName() ?: return false
                // cache position in pixels just in case we need to return later
                mapMgr.setClosestStartPositionFromScaledUnits(player.currentPlayerPosition)
                mapMgr.loadMap(mapName)

                player.currentPlayerPosition.set(mapMgr.playerStartUnitScaled.x, mapMgr.playerStartUnitScaled.y)
                player.nextPlayerPosition.set(mapMgr.playerStartUnitScaled.x, mapMgr.playerStartUnitScaled.y)

                mapRenderer.map = mapMgr.currentMap
                Gdx.app.debug(TAG, "Portal to $mapName Activated")
                return true
            }
        }
        return false
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
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
