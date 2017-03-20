package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.utils.Json


abstract class InputComponent : Component {

    var currentDirection: Entity.Direction = Entity.Direction.DOWN
    var json: Json = Json()

    abstract fun update(entity: Entity, delta: Float)
}
