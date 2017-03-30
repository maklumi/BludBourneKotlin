package com.packtpub.libgdx.bludbourne.UI

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


class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"), BattleObserver {
    private val _image: AnimatedImage

    private val _enemyWidth = 160f
    private val _enemyHeight = 160f

    var battleState = BattleState()
    private var _attackButton: TextButton
    private var _runButton: TextButton

    private var _damageValLabel: Label

    private var _origDamageValLabelY = 0f

    init {

        battleState.addObserver(this)

        _damageValLabel = Label("0", Utility.STATUSUI_SKIN)
        _damageValLabel.isVisible = false

        _image = AnimatedImage()
        _image.touchable = Touchable.disabled


        val table = Table()
        _attackButton = TextButton("Attack", Utility.STATUSUI_SKIN, "inventory")
        _runButton = TextButton("Run", Utility.STATUSUI_SKIN)
        table.add(_attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(_runButton).pad(20f, 20f, 20f, 20f)

        //layout
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
        battleState.setCurrentOpponent(battleZoneValue)
    }


    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            PLAYER_TURN_START -> {
                _attackButton.isDisabled = true
                _attackButton.touchable = Touchable.disabled
            }
            OPPONENT_ADDED -> {
                _image.setAnimation(entity.getAnimation(Entity.AnimationType.IMMOBILE))
            }
            OPPONENT_HIT_DAMAGE -> {
                val damage = entity.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()).toInt()
                _damageValLabel.apply {
                    setText(damage.toString())
                    y = _origDamageValLabelY
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
            }
            PLAYER_TURN_DONE -> {
                battleState.opponentAttacks()
            }
            else -> {
            }
        }
    }

    override fun act(delta: Float) {
        if (_damageValLabel.isVisible) {
            _damageValLabel.y = _damageValLabel.y + 3
        }

        super.act(delta)
    }
}
