package com.packtpub.libgdx.bludbourne

interface ComponentObserver {
    enum class ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION,
        QUEST_LOCATION_DISCOVERED,
        ENEMY_SPAWN_LOCATION_CHANGED,
        PLAYER_HAS_MOVED
    }

    fun onNotify(value: String, event: ComponentEvent)
}
