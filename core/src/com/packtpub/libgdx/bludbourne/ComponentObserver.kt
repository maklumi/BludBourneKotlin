package com.packtpub.libgdx.bludbourne

interface ComponentObserver {
    enum class UIEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION
    }

    fun onNotify(value: String, event: UIEvent)
}
