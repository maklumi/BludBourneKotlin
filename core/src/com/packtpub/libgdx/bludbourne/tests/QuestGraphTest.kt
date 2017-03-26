package com.packtpub.libgdx.bludbourne.tests

import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.quest.QuestGraph
import com.packtpub.libgdx.bludbourne.quest.QuestTask
import com.packtpub.libgdx.bludbourne.quest.QuestTaskDependency
import java.util.*


object QuestGraphTest {
    lateinit internal var _questTasks: Hashtable<String, QuestTask>
    lateinit internal var _graph: QuestGraph
    internal var quit = "q"
    internal var _input = ""
    var _json = Json()

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

        _questTasks.clear()
        _graph.clear()

        val q1 = QuestTask()
        q1.id = "1"
        q1.taskPhrase = "Come back to me with the items"

        val q2 = QuestTask()
        q2.id = "2"
        q2.taskPhrase = "Collect 5 horns"

        val q3 = QuestTask()
        q3.id = "3"
        q3.taskPhrase = "Collect 5 furs"

        val q4 = QuestTask()
        q4.id = "4"
        q4.taskPhrase = "Find the area where the Tuskan beast feasts"

        _questTasks.put(q1.id, q1)
        _questTasks.put(q2.id, q2)
        _questTasks.put(q3.id, q3)
        _questTasks.put(q4.id, q4)

        _graph.setTasks(_questTasks)

        val qDep1 = QuestTaskDependency()
        qDep1.sourceId = q1.id
        qDep1.destinationId = q2.id

        val qDep2 = QuestTaskDependency()
        qDep2.sourceId = q1.id
        qDep2.destinationId = q3.id

        val qDep3 = QuestTaskDependency()
        qDep3.sourceId = q2.id
        qDep3.destinationId = q4.id

        val qDep4 = QuestTaskDependency()
        qDep4.sourceId = q3.id
        qDep4.destinationId = q4.id

        _graph.addDependency(qDep1)
        _graph.addDependency(qDep2)
        _graph.addDependency(qDep3)
        _graph.addDependency(qDep4)

        System.out.println(_json.prettyPrint(_graph))

        _questTasks.clear()
        _graph.clear()

        val q01 = QuestTask()
        q01.id = "1"
        q01.taskPhrase = "Come back to me with the herbs"

        val q02 = QuestTask()
        q02.id = "2"
        q02.taskPhrase = "Please collect 5 herbs for my sick mother"

        _questTasks.put(q01.id, q01)
        _questTasks.put(q02.id, q02)

        _graph.setTasks(_questTasks)

        val qDep01 = QuestTaskDependency()
        qDep01.sourceId = q01.id
        qDep01.destinationId = q02.id

        _graph.addDependency(qDep01)

        System.out.println(_json.prettyPrint(_graph))

    }
}
