package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.bludbourne.Component.Companion.MESSAGE_TOKEN

class NPCPhysicsComponent : PhysicsComponent() {

    private var state: Entity.State = Entity.State.IDLE

    override fun dispose() {}

    override fun receiveMessage(message: String) {
        //Gdx.app.debug(TAG, "Got message " + message);
        val string = message.split(MESSAGE_TOKEN)

        if (string.isEmpty()) return

        //Specifically for messages with 1 object payload
        if (string.size == 2) {
            if (string[0] == Component.MESSAGE.INIT_START_POSITION.toString()) {
                currentEntityPosition = json.fromJson(Vector2::class.java, string[1])
                nextEntityPosition.set(currentEntityPosition.x, currentEntityPosition.y)
            } else if (string[0].equals(Component.MESSAGE.CURRENT_STATE.toString(), ignoreCase = true)) {
                state = json.fromJson(Entity.State::class.java, string[1])
            } else if (string[0].equals(Component.MESSAGE.CURRENT_DIRECTION.toString(), ignoreCase = true)) {
                currentDirection = json.fromJson(Entity.Direction::class.java, string[1])
            }
        }
    }

    override fun update(entity: Entity, mapMgr: MapManager, delta: Float) {
        //We want the hit box to be at the feet for a better feel
        setBoundingBoxSize(entity, 0f, 0f)

        if (state === Entity.State.IMMOBILE) return

        if (!isCollisionWithMapLayer(entity, mapMgr, boundingBox) && state === Entity.State.WALKING) {
            setNextPositionToCurrent(entity)
        }

        calculateNextPosition(delta)
    }


    private val TAG = NPCPhysicsComponent::class.java.simpleName

}