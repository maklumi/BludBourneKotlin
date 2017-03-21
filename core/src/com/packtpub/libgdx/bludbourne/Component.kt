package com.packtpub.libgdx.bludbourne


interface Component {

    enum class MESSAGE {
        CURRENT_POSITION,
        INIT_START_POSITION,
        CURRENT_DIRECTION,
        CURRENT_STATE,
        COLLISION_WITH_MAP
    }

    fun dispose()
    fun receiveMessage(message: String)

    companion object {
        val MESSAGE_TOKEN = ":::::"
    }
}