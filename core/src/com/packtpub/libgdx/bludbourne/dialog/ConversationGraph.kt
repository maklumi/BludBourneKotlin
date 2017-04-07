package com.packtpub.libgdx.bludbourne.dialog

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import java.util.*

class ConversationGraph(private val conversations: Hashtable<String, Conversation>,
                        var currentConversationID: String) :
        ConversationGraphSubject() {

    private val associatedChoices: Hashtable<String, ArrayList<ConversationChoice>> = Hashtable(conversations.size)

    constructor() : this(Hashtable(), "")


    init {
        for (conversation in conversations.values) {
            associatedChoices.put(conversation.id, ArrayList<ConversationChoice>())
        }
    }

    val currentChoices: ArrayList<ConversationChoice>?
        get() = associatedChoices[currentConversationID]

    fun setCurrentConversation(id: String) {
        val conversation = getConversationByID(id) ?: return
        //Can we reach the new conversation from the current one?
        // check case where the current node is checked against itself
        if (currentConversationID.equals(id, true) || isReachable(currentConversationID, id)) {
            currentConversationID = id
        } else {
            //Gdx.app.debug(TAG, "New conversation node $id is not reachable from current node [$currentConversationID]!")
        }
    }

    fun isValid(conversationID: String): Boolean {
        return conversations[conversationID] != null
    }

    fun isReachable(sourceID: String, sinkID: String): Boolean {
        if (!isValid(sourceID) || !isValid(sinkID)) return false
        if (conversations[sourceID] == null) return false

        //First get edges/choices from the source
        val list: ArrayList<ConversationChoice> = associatedChoices[sourceID]!!
//        if (list == null) return false
        list.forEach { choice -> return choice.sourceId == sourceID && choice.destinationId == sinkID }
        return false
    }

    fun getConversationByID(id: String): Conversation? {
        if (!isValid(id)) {
            //Gdx.app.debug(TAG, "Id $id is not valid!")
            return null
        }
        return conversations[id]!!
    }

    fun displayCurrentConversation(): String {
        return conversations[currentConversationID]!!.dialog
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
            outputString.append(String.format("[%s]: ", id))

            for (choice in associatedChoices[id!!]!!) {
                outputString.append(String.format("%s ", choice.destinationId))
            }

            outputString.append(System.getProperty("line.separator"))
        }
        outputString.append("Number conversations: ${conversations.size}, Number of choices: $numberTotalChoices")
        outputString.append(System.getProperty("line.separator"))

        return outputString.toString()
    }

    fun toJson(): String {
        val json = Json()
        json.setOutputType(JsonWriter.OutputType.json) // as opposed toOutputType.minimal output
        return json.prettyPrint(this)
    }

}
