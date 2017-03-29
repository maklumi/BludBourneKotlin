package com.packtpub.libgdx.bludbourne.battle

import com.packtpub.libgdx.bludbourne.Entity

interface BattleObserver {
    enum class BattleEvent {
        OPPONENT_ADDED,
        NONE
    }

    fun onNotify(enemyEntity: Entity, event: BattleEvent)
}
