package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class PlayerGraphicsComponent : GraphicsComponent() {

    private val TAG = GraphicsComponent::class.java.simpleName

    private var currentPosition = Vector2(0f, 0f)
    private var currentState = Entity.State.WALKING
    private var currentDirection = Entity.Direction.DOWN
    private val shapeRenderer = ShapeRenderer()

    private val json = Json()
    private var frameTime = 0f
    private lateinit var currentFrame: TextureRegion

    override fun update(entity: Entity, mapMgr: MapManager, batch: Batch, delta: Float) {
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

        val camera = mapMgr.camera
        camera.position.set(currentPosition.x, currentPosition.y, 0f)
        camera.update()

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()

        val rect = entity.getCurrentBoundingBox()
        shapeRenderer.apply {
            projectionMatrix = camera.combined
            begin(ShapeRenderer.ShapeType.Filled)
            color = Color.RED
            rect(rect.x * Map.UNIT_SCALE, rect.y * Map.UNIT_SCALE, rect.width * Map.UNIT_SCALE, rect.height * Map.UNIT_SCALE)
            end()
        }

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
            } else if (string[0].equals(Component.MESSAGE.LOAD_ANIMATIONS.toString(), true)) {
                val entityConfig = json.fromJson(EntityConfig::class.java, string[1])
                val animationConfigs = entityConfig.animationConfig
                animationConfigs.forEach {
                    val textureNames: Array<String> = it.texturePaths
                    val points: Array<GridPoint2> = it.gridPoints
                    val animationType: Entity.AnimationType = it.animationType
                    val frameDuration = it.frameDuration

                    var animation: Animation<TextureRegion>? = null
                    if (textureNames.size == 1) animation = loadAnimation(textureNames[0], points, frameDuration)
                    if (textureNames.size == 2) animation = loadAnimation(textureNames[0], textureNames[1], points, frameDuration)
                    animations.put(animationType, animation)
                }
            }
        }
    }

    override fun dispose() {
    }
}
