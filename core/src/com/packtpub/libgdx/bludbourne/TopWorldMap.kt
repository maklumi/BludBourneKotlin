package com.packtpub.libgdx.bludbourne


class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, TopWorldMap._mapPath) {
    companion object {
        private val _mapPath = "maps/topworld.tmx"
    }
}
