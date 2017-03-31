package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.utils.Json


object EntityFactory {

    enum class EntityType {
        PLAYER, DEMO_PLAYER, NPC
    }

    private val json = Json()
    val PLAYER_CONFIG = "scripts/player.json"

    fun getEntity(entityType: EntityType): Entity {
        when (entityType) {
            EntityType.PLAYER -> {
                val player = Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
                player.entityConfig = Entity.getEntityConfig(PLAYER_CONFIG)
                player.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(player.entityConfig))
                return player
            }
            EntityType.DEMO_PLAYER -> return Entity(NPCInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            EntityType.NPC -> return Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
        }
    }
}
