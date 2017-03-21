package com.packtpub.libgdx.bludbourne


class TownMap : Map(MapFactory.MapType.TOWN, TownMap._mapPath) {
    companion object {
        private val _mapPath = "maps/town.tmx"
    }
}
