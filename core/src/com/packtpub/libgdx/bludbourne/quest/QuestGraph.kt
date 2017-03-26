package com.packtpub.libgdx.bludbourne.quest

import com.badlogic.gdx.utils.Json
import java.util.*

class QuestGraph {
    private var questTasks: Hashtable<String, QuestTask> = Hashtable()
    private var questTaskDependencies: Hashtable<String, ArrayList<QuestTaskDependency>> = Hashtable()

    var questTitle: String = ""

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

    override fun toString(): String {
        return questTitle
    }

    fun toJson(): String {
        val json = Json()
        return json.prettyPrint(this)
    }

}
