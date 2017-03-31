package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity
import java.util.*

object MonsterFactory {
    enum class MonsterEntityType {
        MONSTER001, MONSTER002, MONSTER003, MONSTER004, MONSTER005,
        MONSTER006, MONSTER007, MONSTER008, MONSTER009, MONSTER010,
        MONSTER011, MONSTER012, MONSTER013, MONSTER014, MONSTER015,
        MONSTER016, MONSTER017, MONSTER018, MONSTER019, MONSTER020,
        MONSTER021, MONSTER022, MONSTER023, MONSTER024, MONSTER025,
        MONSTER026, MONSTER027, MONSTER028, MONSTER029, MONSTER030,
        MONSTER031, MONSTER032, MONSTER033, MONSTER034, MONSTER035,
        MONSTER036, MONSTER037, MONSTER038, MONSTER039, MONSTER040,
        MONSTER041, MONSTER042,
        NONE
    }

    private val _entities: Hashtable<String, Entity> by lazy {
        val configs = Entity.getEntityConfigs("scripts/monsters.json")
        Entity.initEntities(configs)
    }

    private var _monsterZones: Hashtable<String, Array<MonsterEntityType>> =
            MonsterZone.getMonsterZones("scripts/monster_zones.json")


    fun getMonster(monsterEntityType: MonsterEntityType): Entity {
        val entity = _entities[monsterEntityType.toString()]
        return Entity(entity!!)
    }

    fun getRandomMonster(monsterZoneID: Int): Entity? {
        val monsters = _monsterZones[monsterZoneID.toString()] as Array<MonsterEntityType>
        val size = monsters.size
        if (size == 0) {
            return null
        }
        val randomIndex = MathUtils.random(size - 1)

        return getMonster(monsters[randomIndex])
    }

}
