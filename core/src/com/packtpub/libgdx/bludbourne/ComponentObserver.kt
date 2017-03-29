package com.packtpub.libgdx.bludbourne

interface ComponentObserver {
    enum class ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION,
        QUEST_LOCATION_DISCOVERED
    }

    fun onNotify(value: String, event: ComponentEvent)
}
