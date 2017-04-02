package com.packtpub.libgdx.bludbourne.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.audio.AudioManager
import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.audio.AudioSubject

open class GameScreen : Screen, AudioSubject {
    private val _observers = Array<AudioObserver>()

    init {
        this.addObserver(AudioManager)
    }

    override fun addObserver(audioObserver: AudioObserver) {
        _observers.add(audioObserver)
    }

    override fun removeObserver(audioObserver: AudioObserver) {
        _observers.removeValue(audioObserver, true)
    }

    override fun removeAllObservers() {
        _observers.removeAll(_observers, true)
    }

    override fun notify(command: AudioObserver.AudioCommand, event: AudioObserver.AudioTypeEvent) {
        for (observer in _observers) {
            observer.onNotify(command, event)
        }
    }

    override fun show() {

    }

    override fun render(delta: Float) {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {

    }
}
