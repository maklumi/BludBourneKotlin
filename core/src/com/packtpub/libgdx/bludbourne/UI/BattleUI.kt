package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.battle.BattleObserver
import com.packtpub.libgdx.bludbourne.battle.BattleObserver.BattleEvent.*
import com.packtpub.libgdx.bludbourne.battle.BattleState
import com.packtpub.libgdx.bludbourne.sfx.ShakeCamera


class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"), BattleObserver {
    private val _image: AnimatedImage

    private val _enemyWidth = 96f
    private val _enemyHeight = 96f

    var battleState = BattleState()
    private var _attackButton: TextButton
    private var _runButton: TextButton

    private var _damageValLabel: Label

    private var _battleTimer = 0f
    private val _checkTimer = 1f

    private var _battleShakeCam: ShakeCamera? = null
    private var _origDamageValLabelY = 0f

    init {

        battleState.addObserver(this)

        _damageValLabel = Label("0", Utility.STATUSUI_SKIN)
        _damageValLabel.isVisible = false

        _image = AnimatedImage()
        _image.touchable = Touchable.disabled


        val table = Table()
        _attackButton = TextButton("Attack", Utility.STATUSUI_SKIN, "inventory")
        _runButton = TextButton("Run", Utility.STATUSUI_SKIN, "inventory")
        table.add(_attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(_runButton).pad(20f, 20f, 20f, 20f)

        //layout
        this.setFillParent(true)
        this.add(_damageValLabel).align(Align.left).padLeft(_enemyWidth / 2f).row()
        this.add(_image).size(_enemyWidth, _enemyHeight).pad(10f, 10f, 10f, _enemyWidth / 2f)
        this.add(table)
        this.pack()

        _origDamageValLabelY = _damageValLabel.y + _enemyHeight

        _attackButton.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        battleState.playerAttacks()
                    }
                }
        )
        _runButton.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        battleState.playerRuns()
                    }
                }
        )
    }

    fun battleZoneTriggered(battleZoneValue: Int) {
        battleState.setCurrentZoneLevel(battleZoneValue);
    }

    fun isBattleReady(): Boolean {
        if (_battleTimer > _checkTimer) {
            _battleTimer = 0f
            return battleState.isOpponentReady()
        } else {
            return false
        }
    }

    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            PLAYER_TURN_START -> {
                _runButton.isDisabled = true
                _runButton.touchable = Touchable.disabled
                _attackButton.isDisabled = true
                _attackButton.touchable = Touchable.disabled
            }
            OPPONENT_ADDED -> {
                _image.setEntity(entity)
                _image.setCurrentAnimation(Entity.AnimationType.IMMOBILE)
                _image.setSize(_enemyWidth, _enemyHeight)

                val vector = Vector2(_image.x, _image.y)
                if (_battleShakeCam == null) {
                    _battleShakeCam = ShakeCamera(vector.x, vector.y, 30f)
                }

                this.titleLabel.setText("Level " + battleState.getCurrentZoneLevel() + " " + entity.entityConfig.entityID)
            }
            OPPONENT_HIT_DAMAGE -> {
                val damage = entity.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()).toInt()
                _damageValLabel.apply {
                    setText(damage.toString())
                    y = _origDamageValLabelY
                    _battleShakeCam?.startShaking()
                    isVisible = true
                }
            }
            OPPONENT_DEFEATED -> {
                _damageValLabel.isVisible = false
                _damageValLabel.y = _origDamageValLabelY
            }
            OPPONENT_TURN_DONE -> {
                _attackButton.isDisabled = false
                _attackButton.touchable = Touchable.enabled
                _runButton.isDisabled = false
                _runButton.touchable = Touchable.enabled
            }
            PLAYER_TURN_DONE -> {
                battleState.opponentAttacks()
            }
            else -> {
            }
        }
    }

    override fun act(delta: Float) {
        _battleTimer = (_battleTimer + delta) % 60
        if (_damageValLabel.isVisible and (_damageValLabel.y < this.height)) {
            _damageValLabel.y = _damageValLabel.y + 5
        }

        if (_battleShakeCam != null && _battleShakeCam!!.isCameraShaking) {
            val shakeCoords = _battleShakeCam!!.newShakePosition
            _image.setPosition(shakeCoords.x, shakeCoords.y)
        }

        super.act(delta)
    }
}
