package com.packtpub.libgdx.bludbourne.UI


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class AnimatedImage : Image {
    var _animation: Animation<TextureRegion>? = null
    private var _frameTime = 0f

    constructor() : super()

    constructor(animation: Animation<TextureRegion>) : super(animation.getKeyFrame(0f)) {
        this._animation = animation
    }

    fun setAnimation(animation: Animation<TextureRegion>) {
        super.setDrawable(TextureRegionDrawable(animation.getKeyFrame(0f)))
        this._animation = animation
    }

    override fun act(delta: Float) {
        val drawable = this.drawable ?: return
        _frameTime = (_frameTime + delta) % 5
        (drawable as TextureRegionDrawable).region = _animation!!.getKeyFrame(_frameTime, true)
        super.act(delta)
    }


}