package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class ShakeCamera(cameraViewportX: Float, cameraViewportY: Float, private var _shakeRadius: Float) {

    var isCameraShaking = false
    var originPosition = Vector2(cameraViewportX / 2f, cameraViewportY / 2f)
    private var _origShakeRadius = _shakeRadius
    private var _randomAngle = MathUtils.random(1, 360).toFloat()
    private var _offset = Vector2()
    private var _currentPosition = Vector2()

    fun startShaking() {
        isCameraShaking = true
    }

    private fun seedRandomAngle() {
        _randomAngle = MathUtils.random(1, 360).toFloat()
    }

    private fun computeCameraOffset() {
        val sine = MathUtils.sinDeg(_randomAngle)
        val cosine = MathUtils.cosDeg(_randomAngle)
        _offset.x = cosine * _shakeRadius
        _offset.y = sine * _shakeRadius
    }

    private fun computeCurrentCameraCenter() {
        _currentPosition.x = originPosition.x + _offset.x
        _currentPosition.y = originPosition.y + _offset.y
    }

    private fun diminishShake() {
        if (_shakeRadius < 2.0) {
            reset()
            return
        }

        isCameraShaking = true
        _shakeRadius *= .9f
        _randomAngle = MathUtils.random(1, 360).toFloat()
    }

    fun reset() {
        _shakeRadius = _origShakeRadius
        isCameraShaking = false
        seedRandomAngle()
        _currentPosition.x = originPosition.x
        _currentPosition.y = originPosition.y
    }

    val newShakePosition: Vector2
        get() {
            computeCameraOffset()
            computeCurrentCameraCenter()
            diminishShake()
            return _currentPosition
        }

}
