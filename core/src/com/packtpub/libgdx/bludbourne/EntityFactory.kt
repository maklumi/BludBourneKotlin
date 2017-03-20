package com.packtpub.libgdx.bludbourne


object EntityFactory {

    enum class EntityType {
        PLAYER
    }

    fun getEntity(entityType: EntityType): Entity? {
        when (entityType) {
            EntityFactory.EntityType.PLAYER -> return Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            else -> return null
        }
    }
}
