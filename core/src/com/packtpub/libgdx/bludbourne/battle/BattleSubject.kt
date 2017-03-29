package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity

open class BattleSubject {
    private val _observers: Array<BattleObserver> = Array()

    fun addObserver(battleObserver: BattleObserver) {
        _observers.add(battleObserver)
    }

    fun removeObserver(battleObserver: BattleObserver) {
        _observers.removeValue(battleObserver, true)
    }

    protected fun notify(entity: Entity, event: BattleObserver.BattleEvent) {
        for (observer in _observers) {
            observer.onNotify(entity, event)
        }
    }
}
