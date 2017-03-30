package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.packtpub.libgdx.bludbourne.Entity
import java.util.*

class MonsterFactory {
    enum class MonsterEntityType {
        MONSTER001,
        MONSTER002,
        MONSTER003,
        MONSTER004,
        MONSTER005,
        NONE
    }

    private val _entities: Hashtable<String, Entity>
    private var _monsterZones: Hashtable<String, com.badlogic.gdx.utils.Array<MonsterEntityType>>? = null

    init {
        val configs = Entity.getEntityConfigs("scripts/monsters.json")
        _entities = Entity.initEntities(configs)
        _monsterZones = MonsterZone.getMonsterZones("scripts/monster_zones.json")
    }

    fun getMonster(monsterEntityType: MonsterEntityType): Entity {
        val entity = _entities[monsterEntityType.toString()]
        return Entity(entity!!)
    }

    fun getRandomMonster(monsterZoneID: Int): Entity? {
        val monsters = _monsterZones!!.get(monsterZoneID.toString())
        val size = monsters!!.size
        if (size == 0) {
            return null
        }
        val randomIndex = MathUtils.random(size - 1)

        return getMonster(monsters[randomIndex])
    }

    companion object {

        private var _instance: MonsterFactory? = null

        val instance: MonsterFactory
            get() {
                if (_instance == null) {
                    _instance = MonsterFactory()
                }

                return _instance!!
            }
    }

}
