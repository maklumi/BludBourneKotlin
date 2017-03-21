package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array
import java.util.*

abstract class GraphicsComponent : Component {

    abstract fun update(entity: Entity, batch: Batch, delta: Float)

    var animations: Hashtable<Entity.AnimationType, Animation<TextureRegion>> = Hashtable()

    internal fun loadAnimation(firstTexture: String, secondTexture: String, points: Array<GridPoint2>): Animation<TextureRegion> {
        Utility.loadTextureAsset(firstTexture)
        val texture1 = Utility.getTextureAsset(firstTexture)

        Utility.loadTextureAsset(secondTexture)
        val texture2 = Utility.getTextureAsset(secondTexture)

        val texture1Frames = TextureRegion.split(texture1, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)
        val texture2Frames = TextureRegion.split(texture2, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(2)

        animationKeyFrames.add(texture1Frames[points.first().x][points.first().y])
        animationKeyFrames.add(texture2Frames[points.first().x][points.first().y])

        return Animation(0.25f, animationKeyFrames, Animation.PlayMode.LOOP)
    }

    internal fun loadAnimation(textureName: String, points: Array<GridPoint2>): Animation<TextureRegion> {
        Utility.loadTextureAsset(textureName)
        val texture = Utility.getTextureAsset(textureName)
        val textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(points.size)

        points.forEach { point -> animationKeyFrames.add(textureFrames[point.x][point.y]) }

        return Animation(0.25f, animationKeyFrames, Animation.PlayMode.LOOP)
    }

}
