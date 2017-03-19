package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_HEIGHT
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_WIDTH

class GraphicsComponent {
    private val TAG = GraphicsComponent::class.java.simpleName
    private val defaultSpritePath = "sprites/characters/Warrior.png"

    private lateinit var walkLeftFrames: Array<TextureRegion>
    private lateinit var walkRightFrames: Array<TextureRegion>
    private lateinit var walkUpFrames: Array<TextureRegion>
    private lateinit var walkDownFrames: Array<TextureRegion>

    private lateinit var walkLeftAnimation: Animation<TextureRegion>
    private lateinit var walkRightAnimation: Animation<TextureRegion>
    private lateinit var walkUpAnimation: Animation<TextureRegion>
    private lateinit var walkDownAnimation: Animation<TextureRegion>


    private var frameTime = 0f
    private var frameSprite: Sprite = Sprite()
    private lateinit var currentFrame: TextureRegion

    init {
        Utility.loadTextureAsset(defaultSpritePath)
        loadDefaultSprite()
        loadAllAnimations()
    }

    fun update(batch: Batch, entity: Entity, delta: Float) {
        frameTime = (frameTime + delta) % 5

        when (entity.direction) {
            Entity.Direction.DOWN ->
                if (entity.state === Entity.State.WALKING) {
                    currentFrame = walkDownAnimation.getKeyFrame(frameTime)
                } else {
//                currentFrame = walkDownAnimation.keyFrames[0] as TextureRegion
                    currentFrame = walkDownFrames[0]
                }
            Entity.Direction.LEFT ->
                if (entity.state === Entity.State.WALKING) {
                    currentFrame = walkLeftAnimation.getKeyFrame(frameTime)
                } else {
//                currentFrame = walkLeftAnimation.keyFrames[0] as TextureRegion
                    currentFrame = walkLeftFrames[0]
                }
            Entity.Direction.UP ->
                if (entity.state === Entity.State.WALKING) {
                    currentFrame = walkUpAnimation.getKeyFrame(frameTime)
                } else {
//                currentFrame = walkUpAnimation.keyFrames[0] as TextureRegion
                    currentFrame = walkUpFrames[0]
                }
            Entity.Direction.RIGHT ->
                if (entity.state === Entity.State.WALKING) {
                    currentFrame = walkRightAnimation.getKeyFrame(frameTime)
                } else {
//                currentFrame = walkRightAnimation.keyFrames[0] as TextureRegion
                    currentFrame = walkRightFrames[0]
                }
        }

        batch.begin()
        batch.draw(currentFrame, entity.currentPlayerPosition.x, entity.currentPlayerPosition.y, 1f, 1f)
        batch.end()
    }


    fun dispose() = Utility.unloadAsset(defaultSpritePath)

    private fun loadDefaultSprite() {
        val texture = Utility.getTextureAsset(defaultSpritePath)
        val textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT)
        frameSprite = Sprite(textureFrames[0][0], 0, 0, FRAME_WIDTH, FRAME_HEIGHT)
        currentFrame = textureFrames[0][0]
    }

    private fun loadAllAnimations() {
        val texture = Utility.getTextureAsset(defaultSpritePath)
        val textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT)

        walkDownFrames = Array<TextureRegion>(4)
        walkLeftFrames = Array<TextureRegion>(4)
        walkRightFrames = Array<TextureRegion>(4)
        walkUpFrames = Array<TextureRegion>(4)

        for (i in 0..3) {
            for (j in 0..3) {
                val region = textureFrames[i][j]
                when (i) {
                    0 -> walkDownFrames.insert(j, region)
                    1 -> walkLeftFrames.insert(j, region)
                    2 -> walkRightFrames.insert(j, region)
                    3 -> walkUpFrames.insert(j, region)
                }
            }
        }

        walkDownAnimation = Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP)
        walkLeftAnimation = Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP)
        walkRightAnimation = Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP)
        walkUpAnimation = Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP)


    }


}
