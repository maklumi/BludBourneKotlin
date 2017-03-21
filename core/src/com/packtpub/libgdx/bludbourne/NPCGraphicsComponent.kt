package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class NPCGraphicsComponent : GraphicsComponent() {

    private var currentPosition: Vector2 = Vector2(0f, 0f)
    private var currentState: Entity.State = Entity.State.WALKING
    private var currentDirection: Entity.Direction = Entity.Direction.DOWN

    private val walkLeftAnimation: Animation<TextureRegion>
    private val walkRightAnimation: Animation<TextureRegion>
    private val walkUpAnimation: Animation<TextureRegion>
    private val walkDownAnimation: Animation<TextureRegion>
    private val immobileAnimation: Animation<TextureRegion>

    private val json: Json

    private var frameTime = 0f
    private var currentFrame: TextureRegion? = null

    init {
        Utility.loadTextureAsset(walkingAnimationSpriteSheetPath)
        Utility.loadTextureAsset(immobileAnimation1)
        Utility.loadTextureAsset(immobileAnimation2)

        val texture = Utility.getTextureAsset(walkingAnimationSpriteSheetPath)
        val texture1 = Utility.getTextureAsset(immobileAnimation1)
        val texture2 = Utility.getTextureAsset(immobileAnimation2)

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

        val point = GridPoint2(0, 0)
        immobileAnimation = loadAnimation(texture1, texture2, point)

        json = Json()
    }

    override fun receiveMessage(message: String) {
        //Gdx.app.debug(TAG, "Got message " + message);
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0].equals(Component.MESSAGE.CURRENT_POSITION.toString(), ignoreCase = true)) {
                currentPosition = json.fromJson<Vector2>(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.INIT_START_POSITION.toString(), ignoreCase = true)) {
                currentPosition = json.fromJson<Vector2>(Vector2::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE.toString(), ignoreCase = true)) {
                currentState = json.fromJson<Entity.State>(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION.toString(), ignoreCase = true)) {
                currentDirection = json.fromJson<Entity.Direction>(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun update(entity: Entity, batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 5 //Want to avoid overflow

        //Look into the appropriate variable when changing position
        when (currentDirection) {
            Entity.Direction.DOWN -> if (currentState === Entity.State.WALKING) {
                currentFrame = walkDownAnimation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                currentFrame = walkDownAnimation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                currentFrame = immobileAnimation.getKeyFrame(frameTime)
            }
            Entity.Direction.LEFT -> if (currentState === Entity.State.WALKING) {
                currentFrame = walkLeftAnimation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                currentFrame = walkLeftAnimation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                currentFrame = immobileAnimation.getKeyFrame(frameTime)
            }
            Entity.Direction.UP -> if (currentState === Entity.State.WALKING) {
                currentFrame = walkUpAnimation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                currentFrame = walkUpAnimation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                currentFrame = immobileAnimation.getKeyFrame(frameTime)
            }
            Entity.Direction.RIGHT -> if (currentState === Entity.State.WALKING) {
                currentFrame = walkRightAnimation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                currentFrame = walkRightAnimation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                currentFrame = immobileAnimation.getKeyFrame(frameTime)
            }
            else -> {
            }
        }

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()
    }

    override fun dispose() {
        Utility.unloadAsset(walkingAnimationSpriteSheetPath)
        Utility.unloadAsset(immobileAnimation1)
        Utility.unloadAsset(immobileAnimation2)
    }

    companion object {

        private val TAG = NPCGraphicsComponent::class.java.simpleName

        private val walkingAnimationSpriteSheetPath = "sprites/characters/Engineer.png"
        private val immobileAnimation1 = "sprites/characters/Player0.png"
        private val immobileAnimation2 = "sprites/characters/Player1.png"
    }

}
