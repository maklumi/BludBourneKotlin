package com.packtpub.libgdx.bludbourne.audio

interface AudioObserver {

    enum class AudioTypeEvent(val value: String) {
        MUSIC_TITLE("audio/10112013.wav"),
        MUSIC_TOWN(""),
        MUSIC_TOPWORLD(""),
        MUSIC_CASTLEDOOM(""),
        MUSIC_BATTLE("audio/Random Battle.mp3")
    }

    enum class AudioCommand {
        MUSIC_LOAD,
        MUSIC_PLAY_ONCE,
        MUSIC_PLAY_LOOP,
        MUSIC_STOP,
        SOUND_LOAD,
        SOUND_PLAY_ONCE,
        SOUND_PLAY_LOOP,
        SOUND_STOP
    }

    fun onNotify(command: AudioCommand, event: AudioTypeEvent)
}
