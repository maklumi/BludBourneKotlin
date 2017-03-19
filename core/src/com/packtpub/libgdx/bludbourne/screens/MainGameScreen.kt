package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.PlayerController
import com.packtpub.libgdx.bludbourne.Utility


class MainGameScreen : Screen {

    private val unitScale = 1 / 16f
    private val overviewMap = "maps/town.tmx"

    internal var viewportWidth: Float = 0f
    internal var viewportHeight: Float = 0f
    internal var virtualWidth: Float = 0f
    internal var virtualHeight: Float = 0f
    internal var physicalWidth: Float = 0f
    internal var physicalHeight: Float = 0f
    internal var aspectRatio: Float = 0f


    private lateinit var currentMap: TiledMap
    private val MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER"

    private lateinit var mapRenderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera
    private lateinit var controller: PlayerController

    // textures
    lateinit var currentPlayerSprite: Sprite
    lateinit var currentPlayerFrame: TextureRegion

    override fun show() {
        setupViewport(Gdx.graphics.width, Gdx.graphics.height)

        Utility.loadMapAsset(overviewMap)
        if (Utility.isAssetLoaded(overviewMap)) {
            currentMap = Utility.getMapAsset(overviewMap)
        } else {
            Gdx.app.debug(TAG, "Map not loaded")
        }

        //get the current size
        camera = OrthographicCamera(viewportWidth, viewportHeight)
        camera.setToOrtho(false, 10 * aspectRatio, 10f)

        mapRenderer = OrthogonalTiledMapRenderer(currentMap, unitScale)
        mapRenderer.setView(camera)

        // textures
        currentPlayerSprite = BludBourne.player.frameSprite

        controller = PlayerController()
        Gdx.input.inputProcessor = controller

    }

    fun isCollisionWithMap(boundingBox: Rectangle): Boolean {
        val mapCollisionLayer = currentMap.layers.get(MAP_COLLISION_LAYER)

        if (mapCollisionLayer != null) {
            return isCollisionWithMapLayer(boundingBox, mapCollisionLayer)
        } else {
            return false
        }

    }

    override fun hide() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        currentPlayerFrame = BludBourne.player.currentFrame

        // lock and center the camera to player's position
        camera.position.set(currentPlayerSprite.x, currentPlayerSprite.y, 0f)
        camera.update()

        BludBourne.player.update(delta)
        if (!isCollisionWithMap(BludBourne.player.boundingBox)) {
            BludBourne.player.setNextPositionToCurrent()
        }
        controller.update(delta)


        mapRenderer.setView(camera)
        mapRenderer.render()

        mapRenderer.batch.begin()
        mapRenderer.batch.draw(currentPlayerFrame,
                currentPlayerSprite.x, currentPlayerSprite.y, 1f, 1f)
        mapRenderer.batch.end()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        controller.dispose()
        Gdx.input.inputProcessor = null
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

    private fun isCollisionWithMapLayer(boundingBox: Rectangle, collisionLayer: MapLayer): Boolean {

        //Gdx.app.debug(TAG, "Checking collision layer...");
        //Need to account for the unit-scale, since the map coordinates will be in pixels
        // x = 10 unit, new box x = 10 * 16 = 160 pixel
        boundingBox.setPosition(boundingBox.x / unitScale, boundingBox.y / unitScale)

//        var rectangle: Rectangle
//
//        for (`object` in collisionLayer.objects) {
//            if (`object` is RectangleMapObject) {
//                rectangle = `object`.rectangle
//                if (boundingBox.overlaps(rectangle!!)) {
//                    Gdx.app.debug(TAG, "Collision Rect (" + rectangle.x + "," + rectangle.y + ")");
//                    Gdx.app.debug(TAG, "Player Rect (" + boundingBox.x + "," + boundingBox.y + ")");
//                    return true
//                }
//            }
//        }
        collisionLayer.objects.forEach {
            if (it is RectangleMapObject && boundingBox.overlaps(it.rectangle))
                return true
        }

        return false
    }


    private val TAG = MainGameScreen::class.java.simpleName

}
