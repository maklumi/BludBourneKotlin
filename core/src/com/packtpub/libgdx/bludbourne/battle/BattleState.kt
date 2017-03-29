package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.UPDATED_AP
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.UPDATED_DP


class BattleState : BattleSubject(), InventoryObserver {
    private var _currentOpponent: Entity? = null
    private var _currentPlayerAP: Int = 0
    private var _currentPlayerDP: Int = 0

    private fun setCurrentOpponent(monsterType: MonsterFactory.MonsterEntityType) {
        val entity = MonsterFactory.instance.getMonster(monsterType) ?: return
        this._currentOpponent = entity
        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED)
    }

    fun battleZoneEntered(battleZoneID: Int) {
        when (battleZoneID) {
            1 -> {
                System.out.print("Entered BATTLE ZONE: " + battleZoneID)
                setCurrentOpponent(MonsterFactory.MonsterEntityType.MONSTER001)
            }
            else -> {
            }
        }
    }

    fun playerAttacks() {
        var currentOpponentHP = _currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()).toInt()
        val currentOpponentDP = _currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()).toInt()

        val damage = MathUtils.clamp(_currentPlayerAP - currentOpponentDP, 0, _currentPlayerAP)

        currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP)
        _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), currentOpponentHP.toString())
        System.out.println("Player attacks " + _currentOpponent!!.entityConfig.entityID + " leaving it with HP: " + currentOpponentHP)

        if (currentOpponentHP == 0) {
            notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_DEFEATED)
        }
    }

    fun playerRuns() {
        //TODO FINISH
        notify(_currentOpponent as Entity, BattleObserver.BattleEvent.PLAYER_RUNNING)
    }

    override fun onNotify(value: String, event: InventoryEvent) {
        when (event) {
            UPDATED_AP -> {
                val apVal = Integer.valueOf(value)!!
                _currentPlayerAP = apVal
                System.out.println("APVAL: " + _currentPlayerAP)
            }
            UPDATED_DP -> {
                val dpVal = Integer.valueOf(value)!!
                _currentPlayerDP = dpVal
                System.out.println("DPVAL: " + _currentPlayerDP)
            }
            else -> {
            }
        }
    }
}
