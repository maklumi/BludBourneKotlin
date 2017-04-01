package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityFactory
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.MapFactory
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage
import com.packtpub.libgdx.bludbourne.profile.ProfileManager

class CutSceneScreen(game: BludBourne) : MainGameScreen(game) {
    private val _json = Json()
    private val _viewport = ScreenViewport(camera)
    private val _stage = Stage(_viewport)
    private val _entity: Entity
    private val _animImage = AnimatedImage()
    private var _followingActor = Actor().apply { setPosition(0f, 0f) }

    init {
        _entity = EntityFactory.getEntityByName(EntityFactory.EntityName.PLAYER_PUPPET)

        _animImage.setEntity(_entity)
        _animImage.setPosition(1f, 1f)
        _animImage.setSize(_animImage.width * UNIT_SCALE, _animImage.height * UNIT_SCALE)

        _animImage.addAction(
                Actions.sequence(
                        Actions.run { followActor(_animImage) },
                        Actions.run {
                            _animImage.setCurrentAnimation(Entity.AnimationType.WALK_RIGHT)
                            val width = _animImage.width * UNIT_SCALE
                            val height = _animImage.height * UNIT_SCALE
                            _animImage.setSize(width, height)
                        },
                        Actions.moveTo(10f, 1f, 10f),
                        Actions.run {
                            _animImage.setCurrentAnimation(Entity.AnimationType.WALK_UP)
                            val width = _animImage.width * UNIT_SCALE
                            val height = _animImage.height * UNIT_SCALE
                            _animImage.setSize(width, height)
                        },
                        Actions.moveTo(10f, 10f, 10f),
                        Actions.run { mapMgr.loadMap(MapFactory.MapType.CASTLE_OF_DOOM) })
        )

        _stage.addActor(_animImage)
    }

    private fun followActor(actor: Actor) {
        _followingActor = actor
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)

        if (mapMgr.hasMapChanged) {
            mapRenderer.map = mapMgr.getCurrentTiledMap()
            mapMgr.setMapChanged(false)
        }

        mapRenderer.render()

        camera.position.set(_followingActor.x, _followingActor.y, 0f)
        camera.update()

        _stage.act(delta)
        _stage.draw()
    }

    override fun show() {
//        ProfileManager.instance.removeAllObservers()
        if (mapRenderer == null) {
            mapRenderer = OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), UNIT_SCALE)
        }
    }

    override fun hide() {
        ProfileManager.instance.removeAllObservers()
        Gdx.input.inputProcessor = null
    }


}
