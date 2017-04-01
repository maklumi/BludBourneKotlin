package com.packtpub.libgdx.bludbourne.UI


import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align.center
import com.badlogic.gdx.utils.Scaling.stretch
import com.packtpub.libgdx.bludbourne.Entity

class AnimatedImage : Image() {
    //    var _animation: Animation<TextureRegion>? = null
    private var _frameTime = 0f
    private var _entity: Entity? = null
    private var _currentAnimationType = Entity.AnimationType.IDLE

    fun setEntity(entity: Entity) {
        _entity = entity
        // set default
        setCurrentAnimation(Entity.AnimationType.IDLE)
    }

    fun setCurrentAnimation(animationType: Entity.AnimationType) {
        val animation = _entity!!.getAnimation(animationType)

        this.apply {
            _currentAnimationType = animationType
            drawable = TextureRegionDrawable(animation.getKeyFrame(0f))
            setScaling(stretch)
            setAlign(center)
            setSize(prefWidth, prefHeight)
        }
    }

    override fun act(delta: Float) {
        val drawable = this.drawable ?: return
        _frameTime = (_frameTime + delta) % 5
        val region = _entity!!.getAnimation(_currentAnimationType).getKeyFrame(_frameTime, true)
        (drawable as TextureRegionDrawable).region = region
        super.act(delta)
    }


}
