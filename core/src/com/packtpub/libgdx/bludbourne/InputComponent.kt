package com.packtpub.libgdx.bludbourne


abstract class InputComponent : Component {

    abstract fun update(entity: Entity, delta: Float)
}
