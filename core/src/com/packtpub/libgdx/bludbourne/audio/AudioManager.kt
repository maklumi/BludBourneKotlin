package com.packtpub.libgdx.bludbourne.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.audio.AudioObserver.AudioCommand.*
import java.util.*

object AudioManager : AudioObserver {
    private val TAG = AudioManager::class.java.simpleName

    private val _queuedMusic: Hashtable<String, Music> = Hashtable()
    private val _queuedSounds: Hashtable<String, Sound> = Hashtable()


    override fun onNotify(command: AudioObserver.AudioCommand, event: AudioObserver.AudioTypeEvent) {
        when (command) {
            MUSIC_LOAD -> Utility.loadMusicAsset(event.value)
            MUSIC_PLAY_ONCE -> playMusic(false, event.value)
            MUSIC_PLAY_LOOP -> playMusic(true, event.value)
            MUSIC_STOP -> {
                val music = _queuedMusic[event.value]
                music?.stop()
            }
            SOUND_LOAD -> Utility.loadSoundAsset(event.value)
            SOUND_PLAY_LOOP -> playSound(true, event.value)
            SOUND_PLAY_ONCE -> playSound(false, event.value)
            SOUND_STOP -> {
                val sound = _queuedSounds[event.value]
                sound?.stop()
            }
            else -> {
            }
        }
    }

    private fun playMusic(isLooping: Boolean, fullFilePath: String): Music? {
        var music = _queuedMusic.get(fullFilePath)
        if (music != null) {
            music.isLooping = isLooping
            music.play()
        } else if (Utility.isAssetLoaded(fullFilePath)) {
            music = Utility.getMusicAsset(fullFilePath)
            music?.isLooping = isLooping
            music?.play()
            _queuedMusic.put(fullFilePath, music)
        } else {
            Gdx.app.debug(TAG, "Music not loaded")
            return null
        }
        return music
    }

    private fun playSound(isLooping: Boolean, fullFilePath: String): Sound? {
        var sound = _queuedSounds.get(fullFilePath)
        if (sound != null) {
            val soundId = sound.play()
            sound.setLooping(soundId, isLooping)
        } else if (Utility.isAssetLoaded(fullFilePath)) {
            sound = Utility.getSoundAsset(fullFilePath)
            val soundId = sound!!.play()
            sound.setLooping(soundId, isLooping)
            _queuedSounds.put(fullFilePath, sound)
        } else {
            Gdx.app.debug(TAG, "Sound not loaded")
            return null
        }
        return sound
    }

    fun dispose() {
        _queuedMusic.values.forEach { it.dispose() }

        _queuedSounds.values.forEach { it.dispose() }
    }

}
