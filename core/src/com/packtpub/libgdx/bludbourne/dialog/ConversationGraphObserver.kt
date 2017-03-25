package com.packtpub.libgdx.bludbourne.dialog

interface ConversationGraphObserver {
    enum class ConversationCommandEvent {
        LOAD_STORE_INVENTORY,
        EXIT_CONVERSATION,
        NONE
    }

    fun onNotify(graph: ConversationGraph, event: ConversationCommandEvent)
}
