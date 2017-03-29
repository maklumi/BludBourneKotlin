package com.packtpub.libgdx.bludbourne.battle

import com.packtpub.libgdx.bludbourne.Entity
import java.util.*

class MonsterFactory {
    enum class MonsterEntityType {
        MONSTER001,
        NONE
    }

    private val _entities: Hashtable<String, Entity>

    init {
        val configs = Entity.getEntityConfigs("scripts/monsters.json")
        _entities = Entity.initEntities(configs)
    }

    fun getMonster(monsterEntityType: MonsterEntityType): Entity {
        val entity = _entities[monsterEntityType.toString()]
        return Entity(entity!!)
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
