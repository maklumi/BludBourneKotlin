package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage
import com.packtpub.libgdx.bludbourne.battle.MonsterFactory
import com.packtpub.libgdx.bludbourne.profile.ProfileManager

class CutSceneScreen(game: BludBourne) : MainGameScreen(game) {
    private val _viewport = ScreenViewport(camera)
    private val _stage = Stage(_viewport)
    private var _followingActor = Actor().apply { setPosition(0f, 0f) }

    private val _UIViewport = ScreenViewport(hudCamera)
    private val _UIStage = Stage(_UIViewport)
    private val _messageBoxUI = Dialog("", Utility.STATUSUI_SKIN, "solidbackground")
    private val _label = Label("Test", Utility.STATUSUI_SKIN).apply { setWrap(true) }
    private var _isCameraFixed = true
    private val _transitionImage = Image()
    private val _screenFadeOutAction: Action
    private val _screenFadeInAction: Action

    init {

        _messageBoxUI.apply {
            isVisible = false
            contentTable.add(_label).width(_stage.width / 2).pad(10f, 10f, 10f, 0f)
            pack()
            setPosition(_stage.width / 2f - _messageBoxUI.width / 2f, _stage.height - _messageBoxUI.height)
        }

        _transitionImage.apply {
            setFillParent(true)
            drawable = drawable
            addAction(Actions.sequence(Actions.alpha(0f)))
        }

        _screenFadeOutAction = object : Action() {
            override fun act(delta: Float): Boolean {
                _transitionImage.addAction(Actions.sequence(
                        Actions.alpha(0f),
                        Actions.fadeIn(3f)
                ))
                return true
            }
        }

        _screenFadeInAction = object : Action() {
            override fun act(delta: Float): Boolean {
                _transitionImage.addAction(Actions.sequence(
                        Actions.alpha(1f),
                        Actions.fadeOut(3f)
                ))
                return true
            }
        }

        val blackSmith = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH)
        val animBlackSmith = AnimatedImage()
        animBlackSmith.setEntity(blackSmith)
        animBlackSmith.setPosition(10f, 16f)
        animBlackSmith.setSize(animBlackSmith.width * UNIT_SCALE, animBlackSmith.height * UNIT_SCALE)

        val innKeeper = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_INNKEEPER)
        val animInnKeeper = AnimatedImage()
        animInnKeeper.setEntity(innKeeper)
        animInnKeeper.setPosition(12f, 15f)
        animInnKeeper.setSize(animInnKeeper.width * UNIT_SCALE, animInnKeeper.height * UNIT_SCALE)

        val mage = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_MAGE)
        val animMage = AnimatedImage()
        animMage.setEntity(mage)
        animMage.setPosition(11f, 17f)
        animMage.setSize(animMage.width * UNIT_SCALE, animMage.height * UNIT_SCALE)

        val fire = EntityFactory.getEntityByName(EntityFactory.EntityName.FIRE)
        val animFire = AnimatedImage()
        animFire.setEntity(fire)
        animFire.setSize(animFire.width * UNIT_SCALE, animFire.height * UNIT_SCALE)

        val demon = MonsterFactory.getMonster(MonsterFactory.MonsterEntityType.MONSTER042)
        val animDemon = AnimatedImage()
        animDemon.setEntity(demon)
        animDemon.setSize(animDemon.width * UNIT_SCALE, animDemon.height * UNIT_SCALE)
        animDemon.isVisible = false

        _stage.addAction(
                Actions.sequence(
                        Actions.run {
                            mapMgr.loadMap(MapFactory.MapType.TOWN)
                            setCameraPosition(10f, 16f)
                            showMessage("BLACKSMITH: We have planned this long enough. The time is now! I have had enough talk...")

                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("MAGE: This is dark magic you fool. We must proceed with caution, or this could end badly for all of us")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("INNKEEPER: Both of you need to keep it down. If we get caught using black magic, we will all be hanged!")
                        },
                        Actions.delay(5f),
                        Actions.addAction(_screenFadeOutAction),
                        Actions.delay(3f),
                        Actions.run {
                            hideMessage()
                            mapMgr.loadMap(MapFactory.MapType.TOP_WORLD)
                            setCameraPosition(50f, 30f)
                            animBlackSmith.setPosition(50f, 30f)
                            animInnKeeper.setPosition(52f, 30f)
                            animMage.setPosition(50f, 28f)
                            animFire.setPosition(52f, 28f)
                        },
                        Actions.addAction(_screenFadeInAction),
                        Actions.delay(3f),
                        Actions.run {
                            showMessage("BLACKSMITH: Now, let's get on with this. I don't like the cemeteries very much...")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("MAGE: I told you, we can't rush the spell. Bringing someone back to life isn't simple!")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("INNKEEPER: I know you loved your daughter, but this just isn't right...")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("BLACKSMITH: You have never had a child of your own. You just don't understand!")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            showMessage("MAGE: You both need to concentrate, wait...Oh no, something is wrong!!")
                        },
                        Actions.delay(7f),
                        Actions.run {
                            hideMessage()
                            animDemon.setPosition(52f, 28f)
                            animDemon.isVisible = true
                        },
                        Actions.addAction(Actions.fadeOut(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.fadeIn(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.fadeOut(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.fadeIn(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.fadeOut(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.fadeIn(2f), animDemon),
                        Actions.delay(2f),
                        Actions.addAction(Actions.scaleBy(20f, 20f, 5f, Interpolation.bounce), animDemon),
                        Actions.delay(5f),
                        Actions.addAction(Actions.moveBy(20f, 0f), animDemon),
                        Actions.delay(2f),
                        Actions.run {
                            showMessage("BLACKSMITH: What...What have we done...")
                        },
                        Actions.delay(3f),
                        Actions.addAction(_screenFadeOutAction),
                        Actions.delay(3f),
                        Actions.run {
                            hideMessage()
                            animBlackSmith.isVisible = false
                            animInnKeeper.isVisible = false
                            animMage.isVisible = false
                            animFire.isVisible = false

                            mapMgr.loadMap(MapFactory.MapType.TOP_WORLD)

                            animDemon.isVisible = true
                            animDemon.setScale(1f, 1f)
                            animDemon.setSize(16 * UNIT_SCALE, 16 * UNIT_SCALE)
                            animDemon.setPosition(50f, 40f)

                            followActor(animDemon)
                        },
                        Actions.addAction(_screenFadeInAction),
                        Actions.addAction(Actions.moveTo(54f, 65f, 13f, Interpolation.linear), animDemon),
                        Actions.delay(10f),
                        Actions.addAction(_screenFadeOutAction),
                        Actions.delay(3f),
                        Actions.addAction(_screenFadeInAction),
                        Actions.run {
                            hideMessage()
                            animBlackSmith.isVisible = false
                            animInnKeeper.isVisible = false
                            animMage.isVisible = false
                            animFire.isVisible = false
                            mapMgr.loadMap(MapFactory.MapType.CASTLE_OF_DOOM)
                            followActor(animDemon)
                            animDemon.isVisible = true
                            animDemon.setPosition(15f, 1f)
                        },
                        Actions.addAction(Actions.moveTo(15f, 76f, 15f, Interpolation.linear), animDemon),
                        Actions.delay(15f),
                        Actions.run {
                            showMessage("DEMON: I will now send my legions of demons to destroy these sacks of meat!")
                        },
                        Actions.delay(5f),
                        Actions.addAction(_screenFadeOutAction)
                )
        )

        _stage.apply {
            addActor(animFire)
            addActor(animDemon)
            addActor(animMage)
            addActor(animBlackSmith)
            addActor(animInnKeeper)
            addActor(_transitionImage)
        }
        _transitionImage.toFront()

        _UIStage.addActor(_messageBoxUI)
    }

    private fun followActor(actor: Actor) {
        _followingActor = actor
        _isCameraFixed = false
    }

    fun setCameraPosition(x: Float, y: Float) {
        camera.position.set(x, y, 0f)
        _isCameraFixed = true
    }

    fun showMessage(message: String) {
        _label.setText(message)
        _messageBoxUI.apply {
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

        if (!_isCameraFixed) {
            camera.position.set(_followingActor.x, _followingActor.y, 0f)
            camera.update()
        }

        _UIStage.act(delta)
        _UIStage.draw()

        _stage.act()
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
