package com.packtpub.libgdx.bludbourne.tests


import com.packtpub.libgdx.bludbourne.dialog.Conversation
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph
import java.io.BufferedReader
import java.io.InputStreamReader

import java.util.ArrayList
import java.util.Hashtable

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
        start.choicePhrase = "Go to beginning"

        val yesAnswer = Conversation()
        yesAnswer.id = 601
        yesAnswer.dialog = "BOOM! Bombs dropping everywhere"
        yesAnswer.choicePhrase = "YES"

        val noAnswer = Conversation()
        noAnswer.id = 802
        noAnswer.dialog = "Too bad!"
        noAnswer.choicePhrase = "NO"

        val unconnectedTest = Conversation()
        unconnectedTest.id = 250
        unconnectedTest.dialog = "I am unconnected"
        unconnectedTest.choicePhrase = "MUHAHAHAHA"

        _conversations.put(start.id, start)
        _conversations.put(noAnswer.id, noAnswer)
        _conversations.put(yesAnswer.id, yesAnswer)
        _conversations.put(unconnectedTest.id, unconnectedTest)

        _graph = ConversationGraph(_conversations, start)

        _graph.addChoice(start, yesAnswer)
        _graph.addChoice(start, noAnswer)
        _graph.addChoice(noAnswer, start)
        _graph.addChoice(yesAnswer, start)

        println(_graph.toString())
        println(_graph.displayCurrentConversation())

        while (!_input.equals(quit, ignoreCase = true)) {
            val conversation = nextChoice
            _graph.setCurrentConversation(conversation)
            println(_graph.displayCurrentConversation())
        }
    }

    val nextChoice: Conversation
        get() {
            val choices = _graph.currentChoices
            for (conversation in choices) {
                println(conversation.id.toString() + " " + conversation.choicePhrase)
            }
//            _input = System.console().readLine()
            val br = BufferedReader(InputStreamReader(System.`in`))
            _input = br.readLine()

            var choice: Conversation
            try {
                choice = _graph.getConversationByID(Integer.parseInt(_input))
            } catch (nfe: NumberFormatException) {
                return Conversation()
            }

            return choice
        }

}
