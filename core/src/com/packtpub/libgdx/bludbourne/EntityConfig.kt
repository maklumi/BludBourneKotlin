package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Entity.AnimationType
import java.util.*


class EntityConfig {
    var animationConfig: Array<AnimationConfig> = Array()
    var inventory: Array<InventoryItem.ItemTypeID> = Array()
    var state: Entity.State = Entity.State.IDLE
    var direction: Entity.Direction = Entity.Direction.DOWN
    var entityID = UUID.randomUUID().toString()
    var conversationConfigPath: String = ""
    var questConfigPath = ""
    var persistenceKey = ""


    fun addAnimationConfig(animationConfig: AnimationConfig) {
        this.animationConfig.add(animationConfig)
    }

    class AnimationConfig {
        var frameDuration = 1.0f
        var animationType: AnimationType = AnimationType.IDLE
        var texturePaths: Array<String> = Array()
        var gridPoints: Array<GridPoint2> = Array()
    }

}
