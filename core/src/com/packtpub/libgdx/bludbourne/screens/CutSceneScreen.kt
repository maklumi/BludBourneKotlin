package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage
import com.packtpub.libgdx.bludbourne.profile.ProfileManager

class CutSceneScreen(game: BludBourne) : MainGameScreen(game) {
    private val _viewport = ScreenViewport(camera)
    private val _stage = Stage(_viewport)
    private val _entity: Entity
    private val _animImage = AnimatedImage()
    private var _followingActor = Actor().apply { setPosition(0f, 0f) }

    private val _UIViewport = ScreenViewport(hudCamera)
    private val _UIStage = Stage(_UIViewport)
    private val _messageBoxUI: Dialog

    init {
        _messageBoxUI = object : Dialog("", Utility.STATUSUI_SKIN, "solidbackground") {
            override fun result(`object`: Any?) {
                cancel()
                isVisible = false
            }
        }
        _messageBoxUI.apply {
            text("")
            isVisible = false
            pack()
            setPosition(_stage.width / 2f - _messageBoxUI.width / 2f, _stage.height / 2f - _messageBoxUI.height / 2f)
        }

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
                        Actions.run { showMessage("We begin our adventure...") },
                        Actions.delay(3f),
                        Actions.run { hideMessage() },
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
        _UIStage.addActor(_messageBoxUI)
    }

    private fun followActor(actor: Actor) {
        _followingActor = actor
    }

    fun showMessage(message: String) {
        _messageBoxUI.apply {
            text(message)
            pack()
            isVisible = true
        }
    }

    fun hideMessage() {
        _messageBoxUI.isVisible = false
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)

        mapRenderer.batch.enableBlending()
        mapRenderer.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        if (mapMgr.hasMapChanged) {
            mapRenderer.map = mapMgr.getCurrentTiledMap()
            mapMgr.setMapChanged(false)
        }

        mapRenderer.render()

        camera.position.set(_followingActor.x, _followingActor.y, 0f)
        camera.update()

        _stage.act(delta)
        _stage.draw()

        _UIStage.act(delta)
        _UIStage.draw()
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
