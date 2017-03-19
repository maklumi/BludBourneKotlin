package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_HEIGHT
import com.packtpub.libgdx.bludbourne.Entity.Companion.FRAME_WIDTH

class GraphicsComponent : Component {
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
    //   private var frameSprite: Sprite = Sprite()
    private lateinit var currentFrame: TextureRegion
    private val json = Json()
    private var currentPosition = Vector2()
    private var currentState = Entity.State.IDLE
    private var currentDirection = Entity.Direction.DOWN

    init {
        Utility.loadTextureAsset(defaultSpritePath)
        loadAllAnimations()
    }

    fun update(entity: Entity, batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 5

        when (currentDirection) {
            Entity.Direction.DOWN ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkDownAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkDownFrames[0]
                }
            Entity.Direction.LEFT ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkLeftAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkLeftFrames[0]
                }
            Entity.Direction.UP ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkUpAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkUpFrames[0]
                }
            Entity.Direction.RIGHT ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkRightAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkRightFrames[0]
                }
        }

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()
    }

    override fun receiveMessage(message: String) {
        val string = message.split(Component.MESSAGE.MESSAGE_TOKEN.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.CURRENT_POSITION, ignoreCase = true)) {
                currentPosition = json.fromJson(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.INIT_START_POSITION, ignoreCase = true)) {
                currentPosition = json.fromJson(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE, ignoreCase = true)) {
                currentState = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION, ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun dispose() = Utility.unloadAsset(defaultSpritePath)

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
