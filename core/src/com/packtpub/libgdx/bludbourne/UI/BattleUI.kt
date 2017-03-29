package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.battle.BattleObserver
import com.packtpub.libgdx.bludbourne.battle.BattleState

class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"), BattleObserver {
    private val _image: AnimatedImage

    private val _enemyWidth = 160
    private val _enemyHeight = 160

    private var _battleState = BattleState()

    init {

        _battleState.addObserver(this)

        _image = AnimatedImage()
        _image.touchable = Touchable.disabled

        this.add(_image).size(_enemyWidth.toFloat(), _enemyHeight.toFloat())
        this.pack()
    }

    fun battleZoneTriggered(battleZoneValue: Int) {
        _battleState.battleZoneEntered(battleZoneValue)
    }


    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            BattleObserver.BattleEvent.OPPONENT_ADDED -> _image.setAnimation(entity.getAnimation(Entity.AnimationType.IMMOBILE))
            else -> {
            }
        }

    }
}
