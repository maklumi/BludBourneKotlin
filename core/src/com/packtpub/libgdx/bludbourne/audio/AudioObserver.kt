package com.packtpub.libgdx.bludbourne.audio

interface AudioObserver {

    enum class AudioTypeEvent(val value: String) {
        MUSIC_TITLE("audio/10112013.wav"),
        MUSIC_TOWN("audio/Magic Town_0.mp3"),
        MUSIC_TOPWORLD("audio/n3535n5n335n35nj.ogg"),
        MUSIC_CASTLEDOOM("audio/Dark chamber.mp3"),
        MUSIC_BATTLE("audio/Random Battle.mp3"),
        MUSIC_INTRO_CUTSCENE("audio/Takeover_5.mp3"),
        SOUND_CREATURE_PAIN("audio/27780_SFX_CreatureGruntInPain1.wav"),
        SOUND_PLAYER_PAIN("audio/27678_SFX_ComicalSoundsTiredGrunt1.wav"),
        SOUND_PLAYER_WAND_ATTACK("audio/26230_SFX_ProductionElementReverseWhoosh19.wav"),
        SOUND_COIN_RUSTLE("audio/00954_SFX_MoneyCoinsDumpedInHand_final.wav")
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
