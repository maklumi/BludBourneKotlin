package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class PlayerGraphicsComponent : GraphicsComponent() {

    private val TAG = GraphicsComponent::class.java.simpleName
    private val defaultSpritePath = "sprites/characters/Warrior.png"

    private var currentPosition = Vector2(0f, 0f)
    private var currentState = Entity.State.WALKING
    private var currentDirection = Entity.Direction.DOWN

    private var walkLeftAnimation: Animation<TextureRegion>
    private var walkRightAnimation: Animation<TextureRegion>
    private var walkUpAnimation: Animation<TextureRegion>
    private var walkDownAnimation: Animation<TextureRegion>

    private val json = Json()
    private var frameTime = 0f
    private lateinit var currentFrame: TextureRegion

    init {
        Utility.loadTextureAsset(defaultSpritePath)
        val texture = Utility.getTextureAsset(defaultSpritePath)

        val downGridPoints: Array<GridPoint2> = Array()
        val leftGridPoints: Array<GridPoint2> = Array()
        val rightGridPoints: Array<GridPoint2> = Array()
        val upGridPoints: Array<GridPoint2> = Array()

        downGridPoints.add(GridPoint2(0, 0))
        downGridPoints.add(GridPoint2(0, 1))
        downGridPoints.add(GridPoint2(0, 2))
        downGridPoints.add(GridPoint2(0, 3))

        walkDownAnimation = loadAnimation(texture, downGridPoints)

        leftGridPoints.add(GridPoint2(1, 0))
        leftGridPoints.add(GridPoint2(1, 1))
        leftGridPoints.add(GridPoint2(1, 2))
        leftGridPoints.add(GridPoint2(1, 3))

        walkLeftAnimation = loadAnimation(texture, leftGridPoints)

        rightGridPoints.add(GridPoint2(2, 0))
        rightGridPoints.add(GridPoint2(2, 1))
        rightGridPoints.add(GridPoint2(2, 2))
        rightGridPoints.add(GridPoint2(2, 3))

        walkRightAnimation = loadAnimation(texture, rightGridPoints)

        upGridPoints.add(GridPoint2(3, 0))
        upGridPoints.add(GridPoint2(3, 1))
        upGridPoints.add(GridPoint2(3, 2))
        upGridPoints.add(GridPoint2(3, 3))

        walkUpAnimation = loadAnimation(texture, upGridPoints)
    }


    override fun update(entity: Entity, batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 5

        when (currentDirection) {
            Entity.Direction.DOWN ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkDownAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkDownAnimation.keyFrames[0]
                }
            Entity.Direction.LEFT ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkLeftAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkLeftAnimation.keyFrames[0]
                }
            Entity.Direction.UP ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkUpAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkUpAnimation.keyFrames[0]
                }
            Entity.Direction.RIGHT ->
                if (currentState === Entity.State.WALKING) {
                    currentFrame = walkRightAnimation.getKeyFrame(frameTime)
                } else {
                    currentFrame = walkRightAnimation.keyFrames[0]
                }
        }

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()
    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.CURRENT_POSITION.toString(), ignoreCase = true)) {
                currentPosition = json.fromJson(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.INIT_START_POSITION.toString(), ignoreCase = true)) {
                currentPosition = json.fromJson(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE.toString(), ignoreCase = true)) {
                currentState = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION.toString(), ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun dispose() = Utility.unloadAsset(defaultSpritePath)

}
