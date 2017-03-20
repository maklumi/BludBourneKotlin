package com.packtpub.libgdx.bludbourne


object EntityFactory {

    enum class EntityType {
        PLAYER, DEMO_PLAYER
    }

    fun getEntity(entityType: EntityType): Entity? {
        when (entityType) {
            EntityFactory.EntityType.PLAYER -> return Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            EntityType.DEMO_PLAYER -> return Entity(NPCInputComponent(),
                    NPCPhysicsComponent(), NPCGraphicsComponent())
            else -> return null
        }
    }
}
