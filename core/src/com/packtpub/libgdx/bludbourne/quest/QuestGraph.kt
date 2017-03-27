package com.packtpub.libgdx.bludbourne.quest

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.Map
import com.packtpub.libgdx.bludbourne.MapManager
import java.util.*

class QuestGraph {
    private var questTasks: Hashtable<String, QuestTask> = Hashtable()
    private var questTaskDependencies: Hashtable<String, ArrayList<QuestTaskDependency>> = Hashtable()

    var questTitle: String = ""
    var questID: String = ""
    var isQuestComplete: Boolean = false

    fun setTasks(questTasks: Hashtable<String, QuestTask>) {
        if (questTasks.size < 0) {
            throw IllegalArgumentException("Can't have a negative amount of conversations")
        }

        this.questTasks = questTasks
        this.questTaskDependencies = Hashtable<String, ArrayList<QuestTaskDependency>>(questTasks.size)

        for (questTask in questTasks.values) {
            questTaskDependencies.put(questTask.id, ArrayList<QuestTaskDependency>())
        }
    }

    fun getAllQuestTasks(): ArrayList<QuestTask> {
        val enumeration = questTasks.elements()
        return Collections.list(enumeration)
    }

    fun clear() {
        questTasks.clear()
        questTaskDependencies.clear()
    }

    fun isValid(taskID: String): Boolean {
        val questTask = questTasks[taskID] ?: return false
        return true
    }

    fun isReachable(sourceID: String, sinkID: String): Boolean {
        if (!isValid(sourceID) || !isValid(sinkID)) return false
        if (questTasks[sourceID] == null) return false

        val list = questTaskDependencies[sourceID] ?: return false
        for (dependency in list) {
            if (dependency.sourceId.equals(sourceID, ignoreCase = true) && dependency.destinationId.equals(sinkID, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun getQuestTaskByID(id: String): QuestTask? {
        if (!isValid(id)) {
            println("Id $id is not valid!")
            return null
        }
        return questTasks[id]
    }

    fun addDependency(questTaskDependency: QuestTaskDependency) {
        val list = questTaskDependencies[questTaskDependency.sourceId] ?: return

        //Will not add if creates cycles
        if (doesCycleExist(questTaskDependency)) {
            println("Cycle exists! Not adding")
            return
        }

        list.add(questTaskDependency)
    }

    fun doesCycleExist(questTaskDep: QuestTaskDependency): Boolean {
        val keys = questTasks.keys
        for (id in keys) {
            if (doesQuestTaskHaveDependencies(id) && questTaskDep.destinationId.equals(id, ignoreCase = true)) {
                println("ID: " + id + " destID: " + questTaskDep.destinationId)
                return true
            }
        }
        return false
    }

    fun doesQuestTaskHaveDependencies(id: String): Boolean {
        val task = getQuestTaskByID(id) ?: return false
        val list = questTaskDependencies[id]

        return !(list!!.isEmpty() || list.size == 0)
    }

    fun isQuestTaskAvailable(id: String): Boolean {
        val task = getQuestTaskByID(id) ?: return false
        val list = questTaskDependencies[id]

        for (dep in list!!) {
            val depTask = getQuestTaskByID(dep.destinationId) ?: continue
            if (dep.sourceId.equals(id, ignoreCase = true) && !depTask.isTaskComplete) {
                return false
            }
        }
        return true
    }

    fun update(mapMgr: MapManager) {
        val allQuestTasks = getAllQuestTasks()
        abc@ for (questTask in allQuestTasks) {
            //We first want to make sure the task is available and is relevant to current location
            if (!isQuestTaskAvailable(questTask.id)) continue

            val taskLocation = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_LOCATION.toString())
            if (taskLocation == null || taskLocation.isEmpty() ||
                    !taskLocation.equals(mapMgr.getCurrentMapType().toString(), ignoreCase = true))
                continue

            when (questTask.questType) {
                QuestTask.QuestType.FETCH -> {
                    val entities = Array<Entity>()
                    val positions = mapMgr.getQuestItemSpawnPositions(questID, questTask.id)
                    val taskConfig = questTask.getPropertyValue(QuestTask.QuestTaskPropertyType.TARGET_TYPE.toString())
                    if (taskConfig == null || taskConfig.isEmpty()) continue@abc
                    for (position in positions) {
                        val config = Entity.getEntityConfig(taskConfig)
                        val entity = Map.initEntity(config, position)
                        entities.add(entity)
                    }
                    mapMgr.addMapQuestEntities(entities)
                }
                QuestTask.QuestType.KILL -> {
                }
                QuestTask.QuestType.DELIVERY -> {
                }
                QuestTask.QuestType.GUARD -> {
                }
                QuestTask.QuestType.ESCORT -> {
                }
                QuestTask.QuestType.RETURN -> {
                }
                QuestTask.QuestType.DISCOVER -> {
                }
            }
        }
    }

    override fun toString(): String {
        return questTitle
    }

    fun toJson(): String {
        val json = Json()
        return json.prettyPrint(this)
    }

}
