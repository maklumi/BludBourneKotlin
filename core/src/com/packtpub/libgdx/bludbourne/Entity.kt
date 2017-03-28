package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class Entity(val inputComponent: InputComponent,
             val physicsComponent: PhysicsComponent,
             val graphicsComponent: GraphicsComponent) {

    private val TAG = Entity::class.java.simpleName

    var entityConfig = EntityConfig()

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

    private val components = Array<Component>(MAX_COMPONENTS)

    init {
        components.add(inputComponent)
        components.add(graphicsComponent)
        components.add(physicsComponent)
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
    }


}
