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

    private fun playMusic(isLooping: Boolean, fullFilePath: String) {
        val music = _queuedMusic.get(fullFilePath)
        if (music != null) {
            music.isLooping = isLooping
            music.play()
        } else if (Utility.isAssetLoaded(fullFilePath)) {
            val asset = Utility.getMusicAsset(fullFilePath)
            asset?.isLooping = true
            asset?.play()
            _queuedMusic.put(fullFilePath, asset)
        } else {
            Gdx.app.debug(TAG, "Music not loaded")
            return
        }
    }

    private fun playSound(isLooping: Boolean, fullFilePath: String) {
        val sound = _queuedSounds.get(fullFilePath)
        if (sound != null) {
            val soundId = sound.play()
            sound.setLooping(soundId, isLooping)
        } else if (Utility.isAssetLoaded(fullFilePath)) {
            val asset = Utility.getSoundAsset(fullFilePath)
            val soundId = asset!!.play()
            asset.setLooping(soundId, isLooping)
            _queuedSounds.put(fullFilePath, asset)
        } else {
            Gdx.app.debug(TAG, "Sound not loaded")
            return
        }
    }

}
