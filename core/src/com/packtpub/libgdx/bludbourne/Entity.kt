package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN
import com.packtpub.libgdx.bludbourne.profile.ProfileManager
import java.util.Hashtable
import kotlin.collections.ArrayList


class Entity(var inputComponent: InputComponent,
             var physicsComponent: PhysicsComponent,
             var graphicsComponent: GraphicsComponent) {

    private val TAG = Entity::class.java.simpleName

    lateinit var entityConfig: EntityConfig
    var _json = Json()

    enum class State {
        IDLE, WALKING,
        IMMOBILE;

        companion object {
            fun getRandomNext(): State {
                return State.values()[MathUtils.random(State.values().size - 2)]
            }
        }
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        fun getOpposite(): Direction {
            if (this == LEFT) return RIGHT
            else if (this == RIGHT) return LEFT
            else if (this == UP) return DOWN
            else return UP
        }

        companion object {

            fun getRandomNext(): Direction {
                return Direction.values()[MathUtils.random(Direction.values().size - 1)]
            }
        }
    }

    enum class AnimationType {
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE
    }

    private var components = Array<Component>(MAX_COMPONENTS)

    init {
        components.add(inputComponent)
        components.add(graphicsComponent)
        components.add(physicsComponent)
    }

    constructor(entity: Entity) : this(entity.inputComponent, entity.physicsComponent, entity.graphicsComponent) {
        set(entity)
    }

    private fun set(entity: Entity): Entity {
        inputComponent = entity.inputComponent
        graphicsComponent = entity.graphicsComponent
        physicsComponent = entity.physicsComponent
        components.clear()
        components.add(inputComponent)
        components.add(physicsComponent)
        components.add(graphicsComponent)
        entityConfig = EntityConfig(entity.entityConfig)

        return this
    }

    fun update(mapMgr: MapManager, batch: Batch, delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update(this, mapMgr, delta)
        graphicsComponent.update(this, mapMgr, batch, delta)
    }

    fun updateInput(delta: Float) {
        inputComponent.update(this, delta)
    }

    fun sendMessage(message: Component.MESSAGE, vararg args: String) {
        var fullMessage = message.toString()

        args.forEach { fullMessage += MESSAGE_TOKEN + it }

        components.forEach { it.receiveMessage(fullMessage) }
    }

    fun registerObserver(observer: ComponentObserver) {
        inputComponent.addObserver(observer)
        physicsComponent.addObserver(observer)
        graphicsComponent.addObserver(observer)
    }

    fun unregisterObservers() {
        inputComponent.removeAllObservers()
        physicsComponent.removeAllObservers()
        graphicsComponent.removeAllObservers()
    }

    fun getCurrentBoundingBox(): Rectangle = physicsComponent.boundingBox

    fun getCurrentPosition(): Vector2 = graphicsComponent.currentPosition

    fun dispose() = components.forEach { it.dispose() }

    fun getAnimation(type: Entity.AnimationType): Animation<TextureRegion> {
        return graphicsComponent.animations[type]!!
    }

    companion object {
        var FRAME_WIDTH = 16
        var FRAME_HEIGHT = 16
        val MAX_COMPONENTS = 5

        fun getEntityConfig(configFilePath: String): EntityConfig {
            val json = Json()
            return json.fromJson(EntityConfig::class.java, Gdx.files.internal(configFilePath))
        }

        fun getEntityConfigs(configFilePath: String): Array<EntityConfig> {
            val json = Json()
            val configs = Array<EntityConfig>()

            val jsonValues = json.fromJson(ArrayList::class.java, Gdx.files.internal(configFilePath)) as ArrayList<JsonValue>

            jsonValues.forEach { configs.add(json.readValue(EntityConfig::class.java, it)) }

            return configs
        }

        fun loadEntityConfigByPath(entityConfigPath: String): EntityConfig {
            val entityConfig = Entity.getEntityConfig(entityConfigPath)
            val serializedConfig = ProfileManager.instance.getProperty(entityConfig.entityID, EntityConfig::class.java)

            if (serializedConfig == null) return entityConfig else return serializedConfig
        }

        fun loadEntityConfig(entityConfig: EntityConfig): EntityConfig {
            val serializedConfig = ProfileManager.instance.getProperty(entityConfig.entityID, EntityConfig::class.java)

            if (serializedConfig == null) {
                return entityConfig
            } else {
                return serializedConfig
            }
        }

        fun initEntity(entityConfig: EntityConfig, position: Vector2): Entity {
            val json = Json()
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.entityConfig = entityConfig

            entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
            entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))
            entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.entityConfig.state))
            entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.entityConfig.direction))

            return entity
        }

        fun initEntities(configs: Array<EntityConfig>): Hashtable<String, Entity> {
            val json = Json()
            val entities = Hashtable<String, Entity>()
            for (config in configs) {
                val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)

                entity.entityConfig = config
                entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
                entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(Vector2(0f, 0f)))
                entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.entityConfig.state))
                entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.entityConfig.direction))

                entities.put(entity.entityConfig.entityID, entity)
            }

            return entities
        }

        fun initEntity(entityConfig: EntityConfig): Entity {
            val json = Json()
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.entityConfig = entityConfig

            entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
            entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(Vector2.Zero))
            entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.entityConfig.state))
            entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.entityConfig.direction))

            return entity
        }
    }


}
