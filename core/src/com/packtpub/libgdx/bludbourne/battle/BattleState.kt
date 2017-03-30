package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.UPDATED_AP
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.UPDATED_DP
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class BattleState : BattleSubject(), InventoryObserver {
    private var _currentOpponent: Entity? = null
    private var _currentPlayerAP: Int = 0
    private var _currentPlayerDP: Int = 0

    fun setCurrentOpponent(battleZoneLevel: Int) {
        println("Entered BATTLE ZONE: $battleZoneLevel")
        val entity = MonsterFactory.instance.getRandomMonster(battleZoneLevel)
        this._currentOpponent = entity
        notify(entity!!, BattleObserver.BattleEvent.OPPONENT_ADDED)
    }


    fun playerAttacks() {
        if (_currentOpponent == null) return

        notify(_currentOpponent!!, BattleObserver.BattleEvent.PLAYER_TURN_START)

        var currentOpponentHP = _currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()).toInt()
        val currentOpponentDP = _currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()).toInt()

        val damage = MathUtils.clamp(_currentPlayerAP - currentOpponentDP, 0, _currentPlayerAP)

        println("ENEMY HAS $currentOpponentHP hit with damage: $damage")

        currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP)
        _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), currentOpponentHP.toString())
        System.out.println("Player attacks " + _currentOpponent!!.entityConfig.entityID + " leaving it with HP: " + currentOpponentHP)

        _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), damage.toString())
        notify(_currentOpponent!!, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE)

        if (currentOpponentHP == 0) {
            notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_DEFEATED)
        }

        notify(_currentOpponent!!, BattleObserver.BattleEvent.PLAYER_TURN_DONE)
    }

    fun opponentAttacks() {
        if (_currentOpponent == null) {
            return
        }

        val currentOpponentAP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_ATTACK_POINTS.toString()))
        val damage = MathUtils.clamp(currentOpponentAP - _currentPlayerDP, 0, currentOpponentAP)
        var hpVal = ProfileManager.instance.getProperty("currentPlayerHP", Int::class.java) as Int
        hpVal = MathUtils.clamp(hpVal - damage, 0, hpVal)
        ProfileManager.instance.setProperty("currentPlayerHP", hpVal)
        notify(_currentOpponent!!, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE)

        println("Player HIT for " + damage + " BY " + _currentOpponent!!.entityConfig.entityID + " leaving player with HP: " + hpVal)

        notify(_currentOpponent!!, BattleObserver.BattleEvent.OPPONENT_TURN_DONE)
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
