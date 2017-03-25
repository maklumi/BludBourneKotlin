package com.packtpub.libgdx.bludbourne.dialog


class ConversationChoice {
    var sourceId = ""
    var destinationId = ""
    var choicePhrase = ""
    var conversationCommandEvent = ConversationGraphObserver.ConversationCommandEvent.NONE

    override fun toString(): String {
        return choicePhrase
    }
}
