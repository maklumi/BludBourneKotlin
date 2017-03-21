package com.packtpub.libgdx.bludbourne


interface Component {

    enum class MESSAGE {
        CURRENT_POSITION,
        INIT_START_POSITION,
        CURRENT_DIRECTION,
        CURRENT_STATE,
        COLLISION_WITH_MAP,
        COLLISION_WITH_ENTITY,
        LOAD_ANIMATIONS,
        INIT_DIRECTION,
        INIT_STATE
    }

    fun dispose()
    fun receiveMessage(message: String)

    companion object {
        val MESSAGE_TOKEN = ":::::"
    }
}