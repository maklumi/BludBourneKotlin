package com.packtpub.libgdx.bludbourne.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class ShakeCamera(cameraViewportX: Float, cameraViewportY: Float, private var _shakeRadius: Float) {

    var isCameraShaking = false
    private val _originCameraCenter = Vector2(cameraViewportX / 2f, cameraViewportY / 2f)
    private var _origShakeRadius = _shakeRadius
    private var _randomAngle = 0.0f
    private var _offsetCameraCenter = Vector2()
    private var _currentCameraCenter = Vector2()

    fun startShaking() {
        isCameraShaking = true
    }

    private fun seedRandomAngle() {
        _randomAngle = MathUtils.random(1, 360).toFloat()
    }

    private fun computeCameraOffset() {
        _offsetCameraCenter.x = MathUtils.sinDeg(_randomAngle) * _shakeRadius
        _offsetCameraCenter.y = MathUtils.cosDeg(_randomAngle) * _shakeRadius
    }

    private fun computeCurrentCameraCenter() {
        _currentCameraCenter.x = _originCameraCenter.x + _offsetCameraCenter.x
        _currentCameraCenter.y = _originCameraCenter.y + _offsetCameraCenter.y
    }

    private fun diminishShake() {
        if (_shakeRadius < 2.0) {
            reset()
            return
        }

        isCameraShaking = true

        _shakeRadius *= .9f
        _randomAngle = ((150 + MathUtils.random(1, 60)) % 360).toFloat()
    }

    fun reset() {
        _shakeRadius = _origShakeRadius
        isCameraShaking = false
        seedRandomAngle()
        computeCameraOffset()
    }

    val shakeCameraCenter: Vector2
        get() {
            computeCameraOffset()
            computeCurrentCameraCenter()
            diminishShake()
            return _currentCameraCenter
        }

}
