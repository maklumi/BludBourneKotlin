package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import java.util.*


object EntityFactory {

    enum class EntityType {
        PLAYER, PLAYER_DEMO, NPC
    }

    enum class EntityName {
        PLAYER_PUPPET,
        TOWN_GUARD_WALKING,
        TOWN_BLACKSMITH,
        TOWN_MAGE,
        TOWN_INNKEEPER,
        TOWN_FOLK1, TOWN_FOLK2, TOWN_FOLK3, TOWN_FOLK4, TOWN_FOLK5,
        TOWN_FOLK6, TOWN_FOLK7, TOWN_FOLK8, TOWN_FOLK9, TOWN_FOLK10,
        TOWN_FOLK11, TOWN_FOLK12, TOWN_FOLK13, TOWN_FOLK14, TOWN_FOLK15
    }

    private val json = Json()
    val PLAYER_CONFIG = "scripts/player.json"
    val TOWN_GUARD_WALKING_CONFIG = "scripts/town_guard_walking.json"
    val TOWN_BLACKSMITH_CONFIG = "scripts/town_blacksmith.json"
    val TOWN_MAGE_CONFIG = "scripts/town_mage.json"
    val TOWN_INNKEEPER_CONFIG = "scripts/town_innkeeper.json"
    val TOWN_FOLK_CONFIGS = "scripts/town_folk.json"

    private val _entities = Hashtable<String, EntityConfig>()

    init {
        val townFolkConfigs: Array<EntityConfig> = Entity.getEntityConfigs(TOWN_FOLK_CONFIGS)
        for (config in townFolkConfigs) {
            _entities.put(config.entityID, config)
        }

        _entities.put(EntityName.TOWN_GUARD_WALKING.toString(), Entity.loadEntityConfigByPath(TOWN_GUARD_WALKING_CONFIG))
        _entities.put(EntityName.TOWN_BLACKSMITH.toString(), Entity.loadEntityConfigByPath(TOWN_BLACKSMITH_CONFIG))
        _entities.put(EntityName.TOWN_MAGE.toString(), Entity.loadEntityConfigByPath(TOWN_MAGE_CONFIG))
        _entities.put(EntityName.TOWN_INNKEEPER.toString(), Entity.loadEntityConfigByPath(TOWN_INNKEEPER_CONFIG))
        _entities.put(EntityName.PLAYER_PUPPET.toString(), Entity.loadEntityConfigByPath(PLAYER_CONFIG))
    }

    fun getEntity(entityType: EntityType): Entity {
        when (entityType) {
            EntityType.PLAYER -> {
                val player = Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
                player.entityConfig = Entity.getEntityConfig(PLAYER_CONFIG)
                player.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(player.entityConfig))
                return player
            }
            EntityType.PLAYER_DEMO -> return Entity(NPCInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            EntityType.NPC -> return Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
        }
    }

    fun getEntityByName(entityName: EntityName): Entity {
        val config = EntityConfig(_entities[entityName.toString()]!!)
        return Entity.Companion.initEntity(config)
    }
}
