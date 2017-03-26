package com.packtpub.libgdx.bludbourne.quest


import com.badlogic.gdx.utils.ObjectMap

class QuestTask {
    private val IS_TASK_COMPLETE = "IS_TASK_COMPLETE"

    var taskProperties: ObjectMap<String, Any> = ObjectMap()
    var id: String = ""
    var taskPhrase: String = ""

    val isTaskComplete: Boolean
        get() {
            if (!taskProperties.containsKey(IS_TASK_COMPLETE)) {
                taskProperties.put(IS_TASK_COMPLETE, "false")
                return false
            }
            return taskProperties[IS_TASK_COMPLETE] as Boolean
        }

    override fun toString(): String {
        return taskPhrase
    }

}
