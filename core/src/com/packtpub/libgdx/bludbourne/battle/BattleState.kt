package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Timer
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent
import com.packtpub.libgdx.bludbourne.UI.InventoryObserver.InventoryEvent.*
import com.packtpub.libgdx.bludbourne.battle.BattleObserver.BattleEvent
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class BattleState : BattleSubject(), InventoryObserver {
    private val TAG = BattleState::class.java.simpleName
    private var _currentOpponent: Entity? = null
    private var _currentPlayerAP: Int = 0
    private var _currentPlayerDP: Int = 0
    private var _currentZoneLevel = 0
    private var _currentPlayerWandAPPoints = 0
    private var _chanceOfAttack = 25
    private var _chanceOfEscape = 40
    private var _criticalChance = 90
    //    private var _timer = 0f
//    private var _checkTimer = 0f
    private val _playerAttackCalculations: Timer.Task = getPlayerAttackCalculationTimer()
    private val _opponentAttackCalculations: Timer.Task = getOpponentAttackCalculationTimer()
    private val _checkPlayerMagicUse: Timer.Task = getPlayerMagicUseCheckTimer()

    fun setCurrentZoneLevel(zoneLevel: Int) {
        _currentZoneLevel = zoneLevel
    }

    fun getCurrentZoneLevel(): Int {
        return _currentZoneLevel
    }

    fun isOpponentReady(): Boolean {
        if (_currentZoneLevel == 0) return false
        val randomVal = MathUtils.random(1, 100)

        //Gdx.app.debug(TAG, "CHANGE OF ATTACK: " + _chanceOfAttack + " randomval: " + randomVal);

        if (_chanceOfAttack > randomVal) {
            setCurrentOpponent()
            return true
        } else {
            return false
        }
    }

    fun setCurrentOpponent() {
        //Gdx.app.debug(TAG, "Entered BATTLE ZONE: " + _currentZoneLevel)
        val entity = MonsterFactory.getRandomMonster(_currentZoneLevel) ?: return
        this._currentOpponent = entity
        notify(entity, BattleEvent.OPPONENT_ADDED)
    }

    fun playerAttacks() {
        if (_currentOpponent == null) return

        //Check for magic if used in attack; If we don't have enough MP, then return
        var mpVal = ProfileManager.instance.getProperty("currentPlayerMP", Int::class.java) as Int
        notify(_currentOpponent as Entity, BattleEvent.PLAYER_TURN_START)

        if (_currentPlayerWandAPPoints == 0) {
            if (!_playerAttackCalculations.isScheduled) {
                Timer.schedule(_playerAttackCalculations, 1f)
            }
        } else if (_currentPlayerWandAPPoints > mpVal) {
            this@BattleState.notify(_currentOpponent as Entity, BattleEvent.PLAYER_TURN_DONE)
            return
        } else {
            if (!_checkPlayerMagicUse.isScheduled && !_playerAttackCalculations.isScheduled) {
                Timer.schedule(_checkPlayerMagicUse, .5f)
                Timer.schedule(_playerAttackCalculations, 1f)
            }
        }
    }

    private fun getPlayerMagicUseCheckTimer(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                var mpVal = ProfileManager.instance.getProperty("currentPlayerMP", Int::class.java) as Int
                mpVal -= _currentPlayerWandAPPoints
                ProfileManager.instance.setProperty("currentPlayerMP", mpVal)
                this@BattleState.notify(_currentOpponent as Entity, BattleEvent.PLAYER_USED_MAGIC)
            }
        }
    }

    private fun getPlayerAttackCalculationTimer(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                var currentOpponentHP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()))
                val currentOpponentDP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()))

                val damage = MathUtils.clamp(_currentPlayerAP - currentOpponentDP, 0, _currentPlayerAP)

                //Gdx.app.debug(TAG, "ENEMY HAS $currentOpponentHP hit with damage: $damage")

                currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP)
                _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), currentOpponentHP.toString())

                //Gdx.app.debug(TAG, "Player attacks " + _currentOpponent!!.entityConfig.entityID + " leaving it with HP: " + currentOpponentHP)

                _currentOpponent!!.entityConfig.setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), damage.toString())

                if (damage > 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleEvent.OPPONENT_HIT_DAMAGE)
                }

                if (currentOpponentHP == 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleEvent.OPPONENT_DEFEATED)
                }

                this@BattleState.notify(_currentOpponent as Entity, BattleEvent.PLAYER_TURN_DONE)
            }
        }
    }


    fun opponentAttacks() {
        if (_currentOpponent == null) {
            return
        }

        if (!_opponentAttackCalculations.isScheduled) {
            Timer.schedule(_opponentAttackCalculations, 1f)
        }
    }

    private fun getOpponentAttackCalculationTimer(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                val currentOpponentHP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()))

                if (currentOpponentHP <= 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleEvent.OPPONENT_TURN_DONE)
                    return
                }

                val currentOpponentAP = Integer.parseInt(_currentOpponent!!.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_ATTACK_POINTS.toString()))
                val damage = MathUtils.clamp(currentOpponentAP - _currentPlayerDP, 0, currentOpponentAP)
                var hpVal = ProfileManager.instance.getProperty("currentPlayerHP", Int::class.java) as Int
                hpVal = MathUtils.clamp(hpVal - damage, 0, hpVal)
                ProfileManager.instance.setProperty("currentPlayerHP", hpVal)
                this@BattleState.notify(_currentOpponent as Entity, BattleEvent.PLAYER_HIT_DAMAGE)

                if (damage > 0) {
                    this@BattleState.notify(_currentOpponent as Entity, BattleEvent.PLAYER_HIT_DAMAGE)
                }

                //Gdx.app.debug(TAG, "Player HIT for " + damage + " BY " + _currentOpponent!!.entityConfig.entityID + " leaving player with HP: " + hpVal)

                this@BattleState.notify(_currentOpponent as Entity, BattleEvent.OPPONENT_TURN_DONE)
            }
        }
    }

    fun playerRuns() {
        val randomVal = MathUtils.random(1, 100)
        if (_chanceOfEscape > randomVal) {
            notify(_currentOpponent!!, BattleEvent.PLAYER_RUNNING)
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
                //Gdx.app.debug(TAG, "APVAL: " + _currentPlayerAP)
            }
            UPDATED_DP -> {
                val dpVal = value.toInt()
                _currentPlayerDP = dpVal
                //Gdx.app.debug(TAG, "DPVAL: " + _currentPlayerDP)
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
