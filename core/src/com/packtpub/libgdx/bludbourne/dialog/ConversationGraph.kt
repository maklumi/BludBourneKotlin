package com.packtpub.libgdx.bludbourne.dialog

import java.util.ArrayList
import java.util.Hashtable

class ConversationGraph(private val _conversations: Hashtable<Int, Conversation>, root: Conversation) {
    var numChoices = 0
        private set
    private val _associatedChoices: Hashtable<Conversation, ArrayList<Conversation>>
    private var _currentConversation = root

    init {
        if (_conversations.size < 0) {
            throw IllegalArgumentException("Can't have a negative amount of conversations")
        }
        _associatedChoices = Hashtable(_conversations.size)

        for (conversation in _conversations.values) {
            _associatedChoices.put(conversation, ArrayList<Conversation>())
        }
    }

    val currentChoices: ArrayList<Conversation> = _associatedChoices[_currentConversation]!!

    fun setCurrentConversation(conversation: Conversation) {
//        if ( _associatedChoices[conversation] == null) return
        //Can we reach the new conversation from the current one?
        if (isReachable(_currentConversation, conversation)) {
            _currentConversation = conversation
        } else {
            println("New conversation node is not reachable from current node!")
        }
    }

    fun isValid(conversation: Conversation): Boolean {
        if (_conversations[conversation.id] == null) return false
        return true
    }

    fun isReachable(source: Conversation, sink: Conversation): Boolean {
        if (!isValid(source) || !isValid(sink)) return false

        //First get edges/choices from the source
        val list = _associatedChoices[source]!!
        for (conversation in list) {
            if (conversation.id == sink.id) {
                return true
            }
        }
        return false
    }

    fun getConversationByID(id: Int): Conversation = _conversations[id]!!

    fun displayCurrentConversation(): String = _currentConversation.dialog

    val numConversations: Int = _conversations.size

    fun addChoice(sourceConversation: Conversation, targetConversation: Conversation) {
        val list = _associatedChoices[sourceConversation] ?: return

        list.add(targetConversation)
        numChoices++
    }


    override fun toString(): String {
        val outputString = StringBuilder()
        outputString.append("Number conversations: " + _conversations.size + ", Number of choices:" + numChoices)
        outputString.append(System.getProperty("line.separator"))

        val keys = _associatedChoices.keys
        for (conversation in keys) {
            outputString.append(String.format("[%d]: ", conversation.id))

            for (choices in _associatedChoices[conversation]!!) {
                outputString.append(String.format("%d ", choices.id))
            }

            outputString.append(System.getProperty("line.separator"))
        }

        return outputString.toString()
    }


}
