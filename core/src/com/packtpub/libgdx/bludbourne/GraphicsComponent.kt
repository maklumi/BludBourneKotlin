package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array

abstract class GraphicsComponent : Component {

    abstract fun update(entity: Entity, batch: Batch, delta: Float)

    protected fun loadAnimation(texture1: Texture, texture2: Texture, frameIndex: GridPoint2): Animation<TextureRegion> {
        val texture1Frames = TextureRegion.split(texture1, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)
        val texture2Frames = TextureRegion.split(texture2, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(2)

        animationKeyFrames.add(texture1Frames[frameIndex.x][frameIndex.y])
        animationKeyFrames.add(texture2Frames[frameIndex.x][frameIndex.y])

        return Animation(0.25f, animationKeyFrames, Animation.PlayMode.LOOP)
    }

    protected fun loadAnimation(texture: Texture, points: Array<GridPoint2>): Animation<TextureRegion> {
        val textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT)

        val animationKeyFrames = Array<TextureRegion>(points.size)

        for (point in points) {
            animationKeyFrames.add(textureFrames[point.x][point.y])
        }

        return Animation(0.25f, animationKeyFrames, Animation.PlayMode.LOOP)
    }

}
