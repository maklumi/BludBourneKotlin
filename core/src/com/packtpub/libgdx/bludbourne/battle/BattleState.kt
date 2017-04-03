package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Timer
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.*
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class BattleState : BattleSubject(), InventoryObserver {
    private var _currentOpponent: Entity? = null
    private var _currentPlayerAP: Int = 0
    private var _currentPlayerDP: Int = 0
    private var _currentZoneLevel = 0
    private var _currentPlayerWandAPPoints = 0
    private var _chanceOfAttack = 25
    private var _chanceOfEscape = 40
    private var _criticalChance = 90
    private var _timer = 0f
    private var _checkTimer = 0f

    fun setCurrentZoneLevel(zoneLevel: Int) {
        _currentZoneLevel = zoneLevel
    }

    fun getCurrentZoneLevel(): Int {
        return _currentZoneLevel
    }

    fun isOpponentReady(): Boolean {
        if (_currentZoneLevel == 0) return false
        val randomVal = MathUtils.random(1, 100)

        //System.out.println("CHANGE OF ATTACK: " + _chanceOfAttack + " randomval: " + randomVal);

        if (_chanceOfAttack > randomVal) {
            setCurrentOpponent()
            return true
        } else {
            return false
        }
    }

    fun setCurrentOpponent() {
        System.out.print("Entered BATTLE ZONE: " + _currentZoneLevel)
        val entity = MonsterFactory.getRandomMonster(_currentZoneLevel) ?: return
        this._currentOpponent = entity
        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED)
    }

    fun playerAttacks() {
        if (_currentOpponent == null) return

        //Check for magic if used in attack; If we don't have enough MP, then return
        var mpVal = ProfileManager.instance.getProperty("currentPlayerMP", Int::class.java) as Int
        if (_currentPlayerWandAPPoints > mpVal) {
            return
        } else {
            mpVal -= _currentPlayerWandAPPoints
            ProfileManager.instance.setProperty("currentPlayerMP", mpVal)
            notify(_currentOpponent as Entity, BattleObserver.BattleEvent.PLAYER_USED_MAGIC)
        }

        notify(_currentOpponent!!, BattleObserver.BattleEvent.PLAYER_TURN_START)

        Timer.schedule(playerAttackCalculations(), 1f)
    }

    private fun playerAttackCalculations(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                var currentOpponentHP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()))
                val currentOpponentDP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()))

                val damage = MathUtils.clamp(_currentPlayerAP - currentOpponentDP, 0, _currentPlayerAP)

                println("ENEMY HAS $currentOpponentHP hit with damage: $damage")

                currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP)
                _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), currentOpponentHP.toString())

                System.out.println("Player attacks " + _currentOpponent!!.entityConfig.entityID + " leaving it with HP: " + currentOpponentHP)

                _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), damage.toString())
                this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE)


                if (currentOpponentHP == 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_DEFEATED)
                }

                this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.PLAYER_TURN_DONE)
            }
        }
    }


    fun opponentAttacks() {
        if (_currentOpponent == null) {
            return
        }

        Timer.schedule(opponentAttackCalculations(), 1f)
    }

    private fun opponentAttackCalculations(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                val currentOpponentHP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()))

                if (currentOpponentHP <= 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_TURN_DONE)
                    return
                }

                val currentOpponentAP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_ATTACK_POINTS.toString()))
                val damage = MathUtils.clamp(currentOpponentAP - _currentPlayerDP, 0, currentOpponentAP)
                var hpVal = ProfileManager.instance.getProperty("currentPlayerHP", Int::class.java) as Int
                hpVal = MathUtils.clamp(hpVal - damage, 0, hpVal)
                ProfileManager.instance.setProperty("currentPlayerHP", hpVal)
                this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE)

                println("Player HIT for " + damage + " BY " + _currentOpponent!!.entityConfig.entityID + " leaving player with HP: " + hpVal)

                this@BattleState.notify(_currentOpponent as Entity, BattleObserver.BattleEvent.OPPONENT_TURN_DONE)
            }
        }
    }

    fun playerRuns() {
        val randomVal = MathUtils.random(1, 100)
        if (_chanceOfEscape > randomVal) {
            notify(_currentOpponent!!, BattleObserver.BattleEvent.PLAYER_RUNNING)
        } else if (randomVal > _criticalChance) {
            opponentAttacks()
        } else {
            return
        }
    }

    override fun onNotify(value: String, event: InventoryEvent) {
        when (event) {
            UPDATED_AP -> {
                val apVal = value.toInt()
                _currentPlayerAP = apVal
                System.out.println("APVAL: " + _currentPlayerAP)
            }
            UPDATED_DP -> {
                val dpVal = value.toInt()
                _currentPlayerDP = dpVal
                System.out.println("DPVAL: " + _currentPlayerDP)
            }
            ADD_WAND_AP -> {
                val wandAP = value.toInt()
                _currentPlayerWandAPPoints += wandAP
            }
            REMOVE_WAND_AP -> {
                val removeWandAP = value.toInt()
                _currentPlayerWandAPPoints -= removeWandAP
            }
            else -> {
            }
        }
    }
}
