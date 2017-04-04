package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.Map
import com.packtpub.libgdx.bludbourne.Map.Companion.UNIT_SCALE
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage
import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.battle.MonsterFactory
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionAction
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionActor

class CutSceneScreen(val _game: BludBourne) : MainGameScreen(_game) {
    private val _viewport = ScreenViewport(camera)
    private val _stage = Stage(_viewport)
    private var _followingActor = Actor().apply { setPosition(0f, 0f) }

    private val _UIViewport = ScreenViewport(hudCamera)
    private val _UIStage = Stage(_UIViewport)
    private val _messageBoxUI = Dialog("", Utility.STATUSUI_SKIN, "solidbackground")
    private val _label = Label("Test", Utility.STATUSUI_SKIN).apply { setWrap(true) }
    private var _isCameraFixed = true

    private val _transitionActor = ScreenTransitionActor()

    private lateinit var _introCutSceneAction: Action
    private val _switchScreenAction: Action
    private val _setupScene01: Action
    private val _setupScene02: Action
    private val _setupScene03: Action
    private val _setupScene04: Action
    private val _setupScene05: Action

    private val _animBlackSmith = getAnimatedImage(EntityFactory.EntityName.TOWN_BLACKSMITH)
    private val _animInnKeeper = getAnimatedImage(EntityFactory.EntityName.TOWN_INNKEEPER)
    private val _animMage = getAnimatedImage(EntityFactory.EntityName.TOWN_MAGE)
    private val _animFire = getAnimatedImage(EntityFactory.EntityName.FIRE)
    private val _animDemon = getAnimatedImage(MonsterFactory.MonsterEntityType.MONSTER042)

    init {

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE)

        _messageBoxUI.apply {
            isVisible = false
            contentTable.add(_label).width(_stage.width / 2).pad(10f, 10f, 10f, 0f)
            pack()
            setPosition(_stage.width / 2f - _messageBoxUI.width / 2f, _stage.height - _messageBoxUI.height)
        }

        // actions
        _switchScreenAction = object : RunnableAction() {
            override fun run() {
                _game.screen = _game.getScreenType(BludBourne.ScreenType.MainMenu)
            }
        }

        _setupScene01 = object : RunnableAction() {
            override fun run() {
                hideMessage()
                mapMgr.loadMap(MapFactory.MapType.TOWN)
                mapMgr.disableCurrentmapMusic()
                setCameraPosition(10f, 16f)

                _animBlackSmith.isVisible = true
                _animInnKeeper.isVisible = true
                _animMage.isVisible = true

                _animBlackSmith.setPosition(10f, 16f)
                _animInnKeeper.setPosition(12f, 15f)
                _animMage.setPosition(11f, 17f)

                _animDemon.isVisible = false
                _animFire.isVisible = false
            }
        }

        _setupScene02 = object : RunnableAction() {
            override fun run() {
                hideMessage()
                mapMgr.loadMap(MapFactory.MapType.TOP_WORLD)
                mapMgr.disableCurrentmapMusic()
                setCameraPosition(50f, 30f)

                _animBlackSmith.setPosition(50f, 30f)
                _animInnKeeper.setPosition(52f, 30f)
                _animMage.setPosition(50f, 28f)

                _animFire.setPosition(52f, 28f)
                _animFire.isVisible = true
            }
        }

        _setupScene03 = object : RunnableAction() {
            override fun run() {
                _animDemon.setPosition(52f, 28f)
                _animDemon.isVisible = true
                hideMessage()
            }
        }

        _setupScene04 = object : RunnableAction() {
            override fun run() {
                hideMessage()
                _animBlackSmith.isVisible = false
                _animInnKeeper.isVisible = false
                _animMage.isVisible = false
                _animFire.isVisible = false

                mapMgr.loadMap(MapFactory.MapType.TOP_WORLD)
                mapMgr.disableCurrentmapMusic()

                _animDemon.isVisible = true
                _animDemon.setScale(1f, 1f)
                _animDemon.setSize(16 * Map.UNIT_SCALE, 16 * Map.UNIT_SCALE)
                _animDemon.setPosition(50f, 40f)

                followActor(_animDemon)
            }
        }

        _setupScene05 = object : RunnableAction() {
            override fun run() {
                hideMessage()
                _animBlackSmith.isVisible = false
                _animInnKeeper.isVisible = false
                _animMage.isVisible = false
                _animFire.isVisible = false

                mapMgr.loadMap(MapFactory.MapType.CASTLE_OF_DOOM)
                mapMgr.disableCurrentmapMusic()
                followActor(_animDemon)

                _animDemon.isVisible = true
                _animDemon.setPosition(15f, 1f)
            }
        }


        //layout
        _stage.addActor(_animMage)
        _stage.addActor(_animBlackSmith)
        _stage.addActor(_animInnKeeper)
        _stage.addActor(_animFire)
        _stage.addActor(_animDemon)

        _UIStage.addActor(_messageBoxUI)

    }

    private fun getCutsceneAction(): Action {
        _setupScene01.reset()
        _setupScene02.reset()
        _setupScene03.reset()
        _setupScene04.reset()
        _setupScene05.reset()
        _switchScreenAction.reset()

        return Actions.sequence(
                Actions.addAction(_setupScene01),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3f), _transitionActor),
                Actions.delay(3f),
                Actions.run { showMessage("BLACKSMITH: We have planned this long enough. The time is now! I have had enough talk...") },
                Actions.delay(7f),
                Actions.run { showMessage("MAGE: This is dark magic you fool. We must proceed with caution, or this could end badly for all of us") },
                Actions.delay(7f),
                Actions.run { showMessage("INNKEEPER: Both of you need to keep it down. If we get caught using black magic, we will all be hanged!") },
                Actions.delay(5f),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3f), _transitionActor),
                Actions.delay(3f),
                Actions.addAction(_setupScene02),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3f), _transitionActor),
                Actions.delay(3f),
                Actions.run { showMessage("BLACKSMITH: Now, let's get on with this. I don't like the cemeteries very much...") },
                Actions.delay(7f),
                Actions.run { showMessage("MAGE: I told you, we can't rush the spell. Bringing someone back to life isn't simple!") },
                Actions.delay(7f),
                Actions.run { showMessage("INNKEEPER: I know you loved your daughter, but this just isn't right...") },
                Actions.delay(7f),
                Actions.run { showMessage("BLACKSMITH: You have never had a child of your own. You just don't understand!") },
                Actions.delay(7f),
                Actions.run { showMessage("MAGE: You both need to concentrate, wait...Oh no, something is wrong!!") },
                Actions.delay(7f),
                Actions.addAction(_setupScene03),
                Actions.addAction(Actions.fadeOut(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeOut(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeOut(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), _animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.scaleBy(40f, 40f, 5f, Interpolation.linear), _animDemon),
                Actions.delay(5f),
                Actions.addAction(Actions.moveBy(20f, 0f), _animDemon),
                Actions.delay(2f),
                Actions.run { showMessage("BLACKSMITH: What...What have we done...") },
                Actions.delay(3f),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3f), _transitionActor),
                Actions.delay(3f),
                Actions.addAction(_setupScene04),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3f), _transitionActor),
                Actions.addAction(Actions.moveTo(54f, 65f, 13f, Interpolation.linear), _animDemon),
                Actions.delay(10f),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3f), _transitionActor),
                Actions.delay(3f),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3f), _transitionActor),
                Actions.addAction(_setupScene05),
                Actions.addAction(Actions.moveTo(15f, 76f, 15f, Interpolation.linear), _animDemon),
                Actions.delay(15f),
                Actions.run { showMessage("DEMON: I will now send my legions of demons to destroy these sacks of meat!") },
                Actions.delay(5f),
                Actions.addAction(ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3f), _transitionActor),
                Actions.delay(5f),
                Actions.after(_switchScreenAction)
        )

    }

    private fun getAnimatedImage(entityName: EntityFactory.EntityName): AnimatedImage {
        val entity = EntityFactory.getEntityByName(entityName)
        return setEntityAnimation(entity)
    }

    private fun getAnimatedImage(entityType: MonsterFactory.MonsterEntityType): AnimatedImage {
        val entity = MonsterFactory.getMonster(entityType)
        return setEntityAnimation(entity)
    }

    private fun setEntityAnimation(entity: Entity): AnimatedImage {
        val animEntity = AnimatedImage()
        animEntity.setEntity(entity)
        animEntity.setSize(animEntity.width * UNIT_SCALE, animEntity.height * UNIT_SCALE)
        return animEntity
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
        _introCutSceneAction = getCutsceneAction()
        _stage.addAction(_introCutSceneAction)
        notify(AudioObserver.AudioCommand.MUSIC_STOP_ALL, AudioObserver.AudioTypeEvent.NONE)
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE)
//        ProfileManager.instance.removeAllObservers()
        if (mapRenderer == null) {
            mapRenderer = OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), UNIT_SCALE)
        }
    }

    override fun hide() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE)
//        ProfileManager.instance.removeAllObservers()
        Gdx.input.inputProcessor = null
    }


}
