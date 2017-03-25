package com.packtpub.libgdx.bludbourne

interface ComponentObserver {
    enum class ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION
    }

    fun onNotify(value: String, event: ComponentEvent)
}
