package com.packtpub.libgdx.bludbourne.dialog

import com.badlogic.gdx.utils.Json
import java.util.*

class ConversationGraph(private val conversations: Hashtable<Int, Conversation>, private var currentConversationId: Int) {

    private val associatedChoices: Hashtable<Int, ArrayList<ConversationChoice>> = Hashtable(conversations.size)

    init {
        for (conversation in conversations.values) {
            associatedChoices.put(conversation.id, ArrayList<ConversationChoice>())
        }
    }

    val currentChoices: ArrayList<ConversationChoice>
        get() = associatedChoices[currentConversationId]!!

    fun setCurrentConversation(id: Int) {
        val conversation = getConversationByID(id) ?: return
        //Can we reach the new conversation from the current one?
        if (isReachable(currentConversationId, id)) {
            currentConversationId = id
        } else {
            println("New conversation node is not reachable from current node!")
        }
    }

    fun isValid(conversationID: Int): Boolean {
        return conversations[conversationID] != null
    }

    fun isReachable(sourceID: Int, sinkID: Int): Boolean {
        if (!isValid(sourceID) || !isValid(sinkID)) return false
        if (conversations[sourceID] == null) return false

        //First get edges/choices from the source
        val list: ArrayList<ConversationChoice> = associatedChoices[sourceID]!!
        list.forEach { choice -> return choice.sourceId == sourceID && choice.destinationId == sinkID }
        return false
    }

    fun getConversationByID(id: Int): Conversation? {
        if (!isValid(id)) {
            println("Id $id is not valid!")
            return null
        }
        return conversations[id]!!
    }

    fun displayCurrentConversation(): String {
        return conversations[currentConversationId]!!.dialog
    }

    fun addChoice(conversationChoice: ConversationChoice) {

        val list = associatedChoices[conversationChoice.sourceId] ?: return

        list.add(conversationChoice)
    }


    override fun toString(): String {
        val outputString = StringBuilder()
        var numberTotalChoices = 0

        val keys = associatedChoices.keys
        for (id in keys) {
            outputString.append(String.format("[%d]: ", id))

            for (choice in associatedChoices[id!!]!!) {
                outputString.append(String.format("%d ", choice.destinationId))
            }

            outputString.append(System.getProperty("line.separator"))
        }
        outputString.append("Number conversations: ${conversations.size}, Number of choices: $numberTotalChoices")
        outputString.append(System.getProperty("line.separator"))

        return outputString.toString()
    }

    fun toJson(): String {
        return Json().prettyPrint(this)
    }

}
