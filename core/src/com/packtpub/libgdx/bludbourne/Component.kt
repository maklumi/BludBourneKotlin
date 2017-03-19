package com.packtpub.libgdx.bludbourne


interface Component {

    object MESSAGE {
        val MESSAGE_TOKEN = ":::::"
        val CURRENT_POSITION = "currentPosition"
        val INIT_START_POSITION = "initStartPosition"
        val CURRENT_DIRECTION = "currentDirection"
        val CURRENT_STATE = "currentState"
    }


    fun dispose()
    fun receiveMessage(message: String)
}
