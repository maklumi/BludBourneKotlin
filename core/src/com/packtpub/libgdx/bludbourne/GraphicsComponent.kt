package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.ComponentSubject
import java.util.*

abstract class GraphicsComponent : ComponentSubject(), Component {

    abstract fun update(entity: Entity, mapManager: MapManager, batch: Batch, delta: Float)

    protected var currentPosition = Vector2(0f, 0f)
    protected var currentState = Entity.State.WALKING
    protected var currentDirection = Entity.Direction.DOWN
    protected val shapeRenderer = ShapeRenderer()

    protected val json = Json()
    protected var frameTime = 0f
    protected lateinit var currentFrame: TextureRegion

    var animations: Hashtable<Entity.AnimationType, Animation<TextureRegion>> = Hashtable()

    protected fun updateAnimations(delta: Float) {
        frameTime = (frameTime + delta) % 5

        when (currentDirection) {
            Entity.Direction.DOWN -> if (currentState === Entity.State.WALKING) {
                val animation = animations[Entity.AnimationType.WALK_DOWN] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                val animation = animations[Entity.AnimationType.WALK_DOWN] ?: return
                currentFrame = animation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                val animation = animations[Entity.AnimationType.IMMOBILE] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            }
            Entity.Direction.LEFT -> if (currentState === Entity.State.WALKING) {
                val animation = animations[Entity.AnimationType.WALK_LEFT] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                val animation = animations[Entity.AnimationType.WALK_LEFT] ?: return
                currentFrame = animation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                val animation = animations[Entity.AnimationType.IMMOBILE] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            }
            Entity.Direction.UP -> if (currentState === Entity.State.WALKING) {
                val animation = animations[Entity.AnimationType.WALK_UP] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                val animation = animations[Entity.AnimationType.WALK_UP] ?: return
                currentFrame = animation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                val animation = animations[Entity.AnimationType.IMMOBILE] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            }
            Entity.Direction.RIGHT -> if (currentState === Entity.State.WALKING) {
                val animation = animations[Entity.AnimationType.WALK_RIGHT] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            } else if (currentState === Entity.State.IDLE) {
                val animation = animations[Entity.AnimationType.WALK_RIGHT] ?: return
                currentFrame = animation.getKeyFrame(0f)
            } else if (currentState === Entity.State.IMMOBILE) {
                val animation = animations[Entity.AnimationType.IMMOBILE] ?: return
                currentFrame = animation.getKeyFrame(frameTime)
            }
            else -> {
            }
        }
    }

    internal fun loadAnimation(firstTexture: String, secondTexture: String, points: Array<GridPoint2>, frameDuration: Float): Animation<TextureRegion> {
        Utility.loadTextureAsset(firstTexture)
        val texture1 = Utility.getTextureAsset(firstTexture)

        Utility.loadTextureAsset(secondTexture)
        val texture2 = Utility.getTextureAsset(secondTexture)

        val texture1Frames = TextureRegion.split(texture1, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)
        val texture2Frames = TextureRegion.split(texture2, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(2)

        animationKeyFrames.add(texture1Frames[points.first().x][points.first().y])
        animationKeyFrames.add(texture2Frames[points.first().x][points.first().y])

        return Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP)
    }

    internal fun loadAnimation(textureName: String, points: Array<GridPoint2>, frameDuration: Float): Animation<TextureRegion> {
        Utility.loadTextureAsset(textureName)
        val texture = Utility.getTextureAsset(textureName)
        val textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(points.size)

        points.forEach { point -> animationKeyFrames.add(textureFrames[point.x][point.y]) }

        return Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP)
    }

}
