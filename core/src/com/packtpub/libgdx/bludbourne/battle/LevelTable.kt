package com.packtpub.libgdx.bludbourne.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import java.util.*

class LevelTable {
    var levelID: String? = null
    var xpMax: Int = 0
    var hpMax: Int = 0
    var mpMax: Int = 0

    companion object {

        fun getLevelTables(configFilePath: String): Array<LevelTable> {
            val json = Json()
            val levelTable = Array<LevelTable>()

            val list = json.fromJson(ArrayList::class.java, Gdx.files.internal(configFilePath)) as ArrayList<JsonValue>

            for (jsonVal in list) {
                val table = json.readValue(LevelTable::class.java, jsonVal)
                levelTable.add(table)
            }

            return levelTable
        }
    }
}
