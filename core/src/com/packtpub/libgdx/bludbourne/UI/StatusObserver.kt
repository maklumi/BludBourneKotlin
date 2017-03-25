package com.packtpub.libgdx.bludbourne.UI


interface StatusObserver {
    enum class StatusEvent {
        UPDATED_GP,
        UPDATED_LEVEL,
        UPDATED_HP,
        UPDATED_MP,
        UPDATED_XP
    }

    fun onNotify(value: Int, event: StatusEvent)
}
