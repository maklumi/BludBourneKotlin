package com.packtpub.libgdx.bludbourne


object EntityFactory {

    enum class EntityType {
        PLAYER, DEMO_PLAYER, NPC
    }

    fun getEntity(entityType: EntityType): Entity? {
        when (entityType) {
            EntityType.PLAYER -> return Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            EntityType.DEMO_PLAYER -> return Entity(NPCInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            EntityType.NPC -> return Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
            else -> return null
        }
    }
}
