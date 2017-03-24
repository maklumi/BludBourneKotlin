package com.packtpub.libgdx.bludbourne.tests


import com.packtpub.libgdx.bludbourne.dialog.Conversation
import com.packtpub.libgdx.bludbourne.dialog.ConversationChoice
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object ConversationGraphTest {
    internal lateinit var _conversations: Hashtable<Int, Conversation>
    internal lateinit var _graph: ConversationGraph
    internal var quit = "q"
    internal var _input = ""

    @JvmStatic fun main(arg: Array<String>) {
        _conversations = Hashtable<Int, Conversation>()

        val start = Conversation()
        start.id = 500
        start.dialog = "Do you want to play a game?"

        val yesAnswer = Conversation()
        yesAnswer.id = 601
        yesAnswer.dialog = "BOOM! Bombs dropping everywhere"

        val noAnswer = Conversation()
        noAnswer.id = 802
        noAnswer.dialog = "Too bad!"

        val unconnectedTest = Conversation()
        unconnectedTest.id = 250
        unconnectedTest.dialog = "I am unconnected"

        _conversations.put(start.id, start)
        _conversations.put(noAnswer.id, noAnswer)
        _conversations.put(yesAnswer.id, yesAnswer)
        _conversations.put(unconnectedTest.id, unconnectedTest)

        _graph = ConversationGraph(_conversations, start.id)

        val yesChoice = ConversationChoice()
        yesChoice.sourceId = start.id
        yesChoice.destinationId = yesAnswer.id
        yesChoice.choicePhrase = "YES"

        val noChoice = ConversationChoice()
        noChoice.sourceId = start.id
        noChoice.destinationId = noAnswer.id
        noChoice.choicePhrase = "NO"

        val startChoice01 = ConversationChoice()
        startChoice01.sourceId = yesAnswer.id
        startChoice01.destinationId = start.id
        startChoice01.choicePhrase = "Go to beginning!"

        val startChoice02 = ConversationChoice()
        startChoice02.sourceId = noAnswer.id
        startChoice02.destinationId = start.id
        startChoice02.choicePhrase = "Go to beginning!"

        _graph.addChoice(yesChoice)
        _graph.addChoice(noChoice)
        _graph.addChoice(startChoice01)
        _graph.addChoice(startChoice02)


        println(_graph.toString())
        println(_graph.displayCurrentConversation())

        while (!_input.equals(quit, ignoreCase = true)) {
            val conversation = nextChoice
            _graph.setCurrentConversation(conversation.id)
            println(_graph.displayCurrentConversation())
        }
    }

    val nextChoice: Conversation
        get() {
            val choices = _graph.currentChoices
            choices.forEach { choice ->
                println("" + choice.destinationId + " " + choice.choicePhrase)
            }
//            _input = System.console().readLine()
            val br = BufferedReader(InputStreamReader(System.`in`))
            _input = br.readLine()
            val choice: Conversation
            try {
                choice = _graph.getConversationByID(Integer.parseInt(_input))
            } catch (nfe: NumberFormatException) {
                return Conversation()
            }

            return choice
        }

}
