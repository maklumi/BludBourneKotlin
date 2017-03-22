package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class NPCGraphicsComponent : GraphicsComponent() {

    private var isSelected = false

    override fun receiveMessage(message: String) {
        //Gdx.app.debug(TAG, "Got message " + message);
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        if (string.size == 1) {
            if (string[0].equals(Component.MESSAGE.ENTITY_SELECTED.toString(), true)) {
                isSelected = true
            } else if (string[0].equals(Component.MESSAGE.ENTITY_DESELECTED.toString(), true)) {
                isSelected = false
            }
        }

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

    override fun update(entity: Entity, mapMgr: MapManager, batch: Batch, delta: Float) {
        updateAnimations(delta)

        if (isSelected) drawSelected(entity, mapMgr)

        batch.begin()
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1f, 1f)
        batch.end()

// Used to graphically debug boundingBoxes
/*
val rect = entity.getCurrentBoundingBox()
val camera = mapMgr.camera
shapeRenderer.apply {
    projectionMatrix = camera.combined
    begin(ShapeRenderer.ShapeType.Filled)
    color = Color.BLACK
    rect(rect.x * Map.UNIT_SCALE, rect.y * Map.UNIT_SCALE, rect.width * Map.UNIT_SCALE, rect.height * Map.UNIT_SCALE)
    end()
}*/

    }

    private fun drawSelected(entity: Entity, mapMgr: MapManager) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        val camera = mapMgr.camera
        val rect = entity.getCurrentBoundingBox()
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.0f, 1.0f, 1.0f, 0.5f)

        val width = rect.getWidth() * Map.UNIT_SCALE * 2f
        val height = rect.getHeight() * Map.UNIT_SCALE / 2f
        val x = rect.x * Map.UNIT_SCALE - width / 4
        val y = rect.y * Map.UNIT_SCALE - height / 2

        shapeRenderer.ellipse(x, y, width, height)
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    override fun dispose() {
    }


    private val TAG = NPCGraphicsComponent::class.java.simpleName


}
