package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch

abstract class GraphicsComponent : Component {

    abstract fun update(entity: Entity, batch: Batch, delta: Float)

}
