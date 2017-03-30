package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import java.util.*

class MonsterZone {
    var zoneID: String? = null
    var monsters: Array<MonsterFactory.MonsterEntityType>? = null

    companion object {

        fun getMonsterZones(configFilePath: String): Hashtable<String, Array<MonsterFactory.MonsterEntityType>> {
            val json = Json()
            val monsterZones = Hashtable<String, Array<MonsterFactory.MonsterEntityType>>()

            val list = json.fromJson(ArrayList::class.java, Gdx.files.internal(configFilePath)) as ArrayList<JsonValue>

            for (jsonVal in list) {
                val zone = json.readValue(MonsterZone::class.java, jsonVal)
                monsterZones.put(zone.zoneID, zone.monsters)
            }

            return monsterZones
        }
    }
}
