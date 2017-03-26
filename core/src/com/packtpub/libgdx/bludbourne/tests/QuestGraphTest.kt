package com.packtpub.libgdx.bludbourne.tests

import com.packtpub.libgdx.bludbourne.quest.QuestGraph
import com.packtpub.libgdx.bludbourne.quest.QuestTask
import com.packtpub.libgdx.bludbourne.quest.QuestTaskDependency

import java.util.Hashtable

object QuestGraphTest {
    lateinit internal var _questTasks: Hashtable<String, QuestTask>
    lateinit internal var _graph: QuestGraph
    internal var quit = "q"
    internal var _input = ""


    @JvmStatic fun main(arg: Array<String>) {
        _questTasks = Hashtable<String, QuestTask>()

        val firstTask = QuestTask()
        firstTask.id = "500"
        firstTask.taskPhrase = "Come back to me with the bones"

        val secondTask = QuestTask()
        secondTask.id = "601"
        secondTask.taskPhrase = "Pickup 5 bones from the Isle of Death"

        _questTasks.put(firstTask.id, firstTask)
        _questTasks.put(secondTask.id, secondTask)

        _graph = QuestGraph()
        _graph.setTasks(_questTasks)

        val firstDep = QuestTaskDependency()
        firstDep.sourceId = firstTask.id
        firstDep.destinationId = secondTask.id

        val cycleDep = QuestTaskDependency()
        cycleDep.sourceId = secondTask.id
        cycleDep.destinationId = firstTask.id

        _graph.addDependency(firstDep)
        _graph.addDependency(cycleDep)

        println(_graph.toString())
    }
}
