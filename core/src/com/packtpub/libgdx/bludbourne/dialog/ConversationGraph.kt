package com.packtpub.libgdx.bludbourne.dialog

import java.util.ArrayList
import java.util.Hashtable

class ConversationGraph(private val _conversations: Hashtable<Int, Conversation>, rootID: Int) {

    var numChoices = 0

    private val _associatedChoices: Hashtable<Int, ArrayList<Int>> = Hashtable(_conversations.size)
    private var _currentConversation = getConversationByID(rootID)

    init {
        for (conversation in _conversations.values) {
            _associatedChoices.put(conversation.id, ArrayList<Int>())
        }
    }

    val currentChoices: ArrayList<Int>
        get() = _associatedChoices[_currentConversation.id]!!

    fun setCurrentConversation(id: Int) {
        val conversation = getConversationByID(id)
//Can we reach the new conversation from the current one?
        if (isReachable(_currentConversation.id, id)) {
            _currentConversation = conversation
        } else {
            println("New conversation node is not reachable from current node!")
        }
    }

    fun isValid(conversationID: Int): Boolean {
        return _conversations[conversationID] != null
    }

    fun isReachable(sourceID: Int, sinkID: Int): Boolean {
        if (!isValid(sourceID) || !isValid(sinkID)) return false
        if (_conversations[sourceID] == null) return false

        //First get edges/choices from the source
        val list = _associatedChoices[sourceID]!!
        return list.contains(sinkID)
    }

    fun getConversationByID(id: Int): Conversation {
        if (!isValid(id)) {
            println("Id $id is not valid!")
            return Conversation()
        }
        return _conversations[id]!!
    }

    fun displayCurrentConversation(): String {
        return _currentConversation.dialog
    }

    val numConversations: Int = _conversations.size

    fun addChoice(sourceConversationID: Int, targetConversationID: Int) {

        val list = _associatedChoices[sourceConversationID] ?: return

        list.add(targetConversationID)
        numChoices++
    }


    override fun toString(): String {
        val outputString = StringBuilder()
        outputString.append("Number conversations: " + _conversations.size + ", Number of choices:" + numChoices)
        outputString.append(System.getProperty("line.separator"))

        val keys = _associatedChoices.keys
        for (id in keys) {
            outputString.append(String.format("[%d]: ", id))

            for (choiceID in _associatedChoices[id!!]!!) {
                outputString.append(String.format("%d ", choiceID))
            }

            outputString.append(System.getProperty("line.separator"))
        }

        return outputString.toString()
    }


}
