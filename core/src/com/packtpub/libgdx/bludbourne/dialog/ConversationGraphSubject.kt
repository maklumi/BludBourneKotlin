package com.packtpub.libgdx.bludbourne.dialog

import com.badlogic.gdx.utils.Array

open class ConversationGraphSubject {
    private val _observers = Array<ConversationGraphObserver>()

    fun addObserver(graphObserver: ConversationGraphObserver) {
        _observers.add(graphObserver)
    }

    fun removeObserver(graphObserver: ConversationGraphObserver) {
        _observers.removeValue(graphObserver, true)
    }

    fun removeAllObservers() {
        for (observer in _observers) {
            _observers.removeValue(observer, true)
        }
    }

    fun notify(graph: ConversationGraph, event: ConversationGraphObserver.ConversationCommandEvent) {
        for (observer in _observers) {
            observer.onNotify(graph, event)
        }
    }
}
