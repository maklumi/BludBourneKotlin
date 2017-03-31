package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class PlayerGraphicsComponent : GraphicsComponent() {

    private val TAG = GraphicsComponent::class.java.simpleName

    private var _previousPosition = Vector2.Zero

    override fun update(entity: Entity, mapMgr: MapManager, batch: Batch, delta: Float) {

        updateAnimations(delta)

        //Player has moved
        if (_previousPosition.x != currentPosition.x ||
                _previousPosition.y != currentPosition.y) {
            notify("", ComponentObserver.ComponentEvent.PLAYER_HAS_MOVED)
            _previousPosition = currentPosition.cpy()
        }

        val camera = mapMgr.camera
        camera.position.set(currentPosition.x, currentPosition.y, 0f)
        camera.update()

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()

        // Used to graphically debug boundingBoxes
        /*
        val rect = entity.getCurrentBoundingBox()
        shapeRenderer.apply {
            projectionMatrix = camera.combined
            begin(ShapeRenderer.ShapeType.Filled)
            color = Color.RED
            rect(rect.x * Map.UNIT_SCALE, rect.y * Map.UNIT_SCALE, rect.width * Map.UNIT_SCALE, rect.height * Map.UNIT_SCALE)
            end()
        }*/

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
