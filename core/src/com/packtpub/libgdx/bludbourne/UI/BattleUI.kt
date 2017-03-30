package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.battle.BattleObserver
import com.packtpub.libgdx.bludbourne.battle.BattleObserver.BattleEvent.OPPONENT_ADDED
import com.packtpub.libgdx.bludbourne.battle.BattleState


class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"), BattleObserver {
    private val _image: AnimatedImage

    private val _enemyWidth = 160f
    private val _enemyHeight = 160f

    var battleState = BattleState()
    private var _attackButton: TextButton
    private var _runButton: TextButton

    init {

        battleState.addObserver(this)

        _image = AnimatedImage()
        _image.touchable = Touchable.disabled


        val table = Table()
        _attackButton = TextButton("Attack", Utility.STATUSUI_SKIN)
        _runButton = TextButton("Run", Utility.STATUSUI_SKIN)
        table.add(_attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(_runButton).pad(20f, 20f, 20f, 20f)

        //layout
        this.add(_image).size(_enemyWidth, _enemyHeight).pad(20f, 20f, 20f, _enemyWidth / 2f)
        this.add(table)
        this.pack()

        _attackButton.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        battleState.playerAttacks()
                        //_attackButton.setDisabled(true);
                        //_attackButton.setTouchable(Touchable.disabled);
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
            OPPONENT_ADDED -> _image.setAnimation(entity.getAnimation(Entity.AnimationType.IMMOBILE))
            else -> {
            }
        }

    }
}
