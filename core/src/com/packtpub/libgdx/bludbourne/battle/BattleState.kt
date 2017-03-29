package com.packtpub.libgdx.bludbourne.battle

import com.packtpub.libgdx.bludbourne.Entity

class BattleState : BattleSubject() {
    private var _currentOpponent: Entity? = null

    private fun setCurrentOpponent(monsterType: MonsterFactory.MonsterEntityType) {
        val entity = MonsterFactory.instance.getMonster(monsterType) ?: return
        this._currentOpponent = entity
        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED)
    }

    fun battleZoneEntered(battleZoneID: Int) {
        when (battleZoneID) {
            1 -> setCurrentOpponent(MonsterFactory.MonsterEntityType.MONSTER001)
            else -> {
            }
        }
    }
}
