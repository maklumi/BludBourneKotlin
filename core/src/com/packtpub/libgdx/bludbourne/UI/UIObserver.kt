package com.packtpub.libgdx.bludbourne.UI

interface UIObserver {
    enum class UIEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION
    }

    fun onNotify(value: String, event: UIEvent)
}
