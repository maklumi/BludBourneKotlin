package com.packtpub.libgdx.bludbourne.dialog


class ConversationChoice {
    var sourceId = ""
    var destinationId = ""
    var choicePhrase = ""

    override fun toString(): String {
        return choicePhrase
    }
}
