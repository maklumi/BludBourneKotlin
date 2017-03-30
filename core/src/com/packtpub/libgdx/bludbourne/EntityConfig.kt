package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
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
    var currentQuestID = ""
    var itemTypeID = ""
    var entityProperties: ObjectMap<String, String> = ObjectMap()

    constructor()

    constructor(config: EntityConfig) {
        state = config.state
        direction = config.direction
        entityID = config.entityID
        conversationConfigPath = config.conversationConfigPath
        questConfigPath = config.questConfigPath
        currentQuestID = config.currentQuestID
        itemTypeID = config.itemTypeID

        animationConfig.addAll(config.animationConfig)
        inventory.addAll(config.inventory)
        entityProperties.putAll(config.entityProperties)
    }

    fun setPropertyValue(key: String, value: String) {
        entityProperties.put(key, value)
    }

    fun getPropertyValue(key: String): String {
        val propertyVal = entityProperties.get(key) ?: return ""
        return propertyVal
    }

    fun addAnimationConfig(animationConfig: AnimationConfig) {
        this.animationConfig.add(animationConfig)
    }

    class AnimationConfig {
        var frameDuration = 1.0f
        var animationType: AnimationType = AnimationType.IDLE
        var texturePaths: Array<String> = Array()
        var gridPoints: Array<GridPoint2> = Array()
    }

    enum class EntityProperties {
        ENTITY_HEALTH_POINTS,
        ENTITY_ATTACK_POINTS,
        ENTITY_DEFENSE_POINTS,
        ENTITY_HIT_DAMAGE_TOTAL,
        ENTITY_XP_REWARD,
        ENTITY_GP_REWARD,
        NONE
    }
}
