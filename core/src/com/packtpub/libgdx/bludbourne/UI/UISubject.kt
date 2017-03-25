package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.utils.Array

open class UISubject {
    private val _observers = Array<UIObserver>()

    fun addObserver(conversationObserver: UIObserver) {
        _observers.add(conversationObserver)
    }

    fun removeObserver(conversationObserver: UIObserver) {
        _observers.removeValue(conversationObserver, true)
    }

    fun removeAllObservers() {
        for (observer in _observers) {
            _observers.removeValue(observer, true)
        }
    }

    fun notify(value: String, event: UIObserver.UIEvent) {
        for (observer in _observers) {
            observer.onNotify(value, event)
        }
    }
}
