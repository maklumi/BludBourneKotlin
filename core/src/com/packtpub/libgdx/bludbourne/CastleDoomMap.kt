package com.packtpub.libgdx.bludbourne

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, CastleDoomMap._mapPath) {
    companion object {
        private val _mapPath = "maps/castle_of_doom.tmx"
    }

}
