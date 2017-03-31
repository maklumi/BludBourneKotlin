package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.packtpub.libgdx.bludbourne.BludBourne
import com.packtpub.libgdx.bludbourne.Component
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityFactory
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage

class CutSceneScreen(game: BludBourne) : MainGameScreen(game) {
    private val _json = Json()
    private val _viewport = ScreenViewport(camera)
    private val _stage = Stage(_viewport)
    private val _entity: Entity
    private val _image: Image
    private val _animImage = AnimatedImage()

    init {
        _entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
        _entity.entityConfig = Entity.loadEntityConfigByPath(EntityFactory.PLAYER_CONFIG)
        _entity.sendMessage(Component.MESSAGE.INIT_STATE, _json.toJson(Entity.State.WALKING))
        _entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, _json.toJson(_entity.entityConfig))

        _image = Image(_entity.getAnimation(Entity.AnimationType.WALK_RIGHT).getKeyFrame(0f))
        _image.setPosition(0f, 0f)
        _image.setSize(_image.width * UNIT_SCALE, _image.height * UNIT_SCALE)

        val animation = _entity.getAnimation(Entity.AnimationType.WALK_RIGHT)
        _animImage.setAnimation(animation)
        _animImage.setPosition(1f, 1f)
        _animImage.setSize(_animImage.width * UNIT_SCALE, _animImage.height * UNIT_SCALE)
//        _animImage.debug = true

        _animImage.addAction(
                Actions.sequence(
                        Actions.run {
                            _animImage.setAnimation(_entity.getAnimation(Entity.AnimationType.WALK_RIGHT))
                            val width = _animImage.width * UNIT_SCALE
                            val height = _animImage.height * UNIT_SCALE
                            _animImage.setSize(width, height)
                        },
                        Actions.moveTo(10f, 1f, 10f),
                        Actions.run {
                            _animImage.setAnimation(_entity.getAnimation(Entity.AnimationType.WALK_UP))
                            val width = _animImage.width * UNIT_SCALE
                            val height = _animImage.height * UNIT_SCALE
                            _animImage.setSize(width, height)
                        },
                        Actions.moveTo(10f, 10f, 10f))
        )

        _stage.addActor(_animImage)
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

        camera.position.set(_animImage.x, _animImage.y, 0f)
        camera.update()

        //_player.updateInput(delta);
        //_player.updatePhysics(mapMgr, delta);

        _stage.act(delta)
        _stage.draw()
    }

    override fun show() {
        if (mapRenderer == null) {
            mapRenderer = OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), UNIT_SCALE)
        }
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }


}
