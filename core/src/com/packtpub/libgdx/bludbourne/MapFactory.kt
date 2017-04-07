package com.packtpub.libgdx.bludbourne

import java.util.Hashtable

object MapFactory {
    //All maps for the game
    private val mapTable = Hashtable<MapType, Map>()

    enum class MapType {
        TOP_WORLD,
        TOWN,
        CASTLE_OF_DOOM
    }

    fun getMap(mapType: MapType): Map {
        val map : Map
        when (mapType) {
            MapFactory.MapType.TOP_WORLD -> {
                map = TopWorldMap()
                mapTable.put(MapType.TOP_WORLD, map)
            }
            MapFactory.MapType.TOWN -> {
                map = TownMap()
                mapTable.put(MapType.TOWN, map)
            }
            MapFactory.MapType.CASTLE_OF_DOOM -> {
                map = CastleDoomMap()
                mapTable.put(MapType.CASTLE_OF_DOOM, map)
            }

        }
        return map
    }

    fun clearCache() {
        for (map in mapTable.values) {
            map.dispose()
        }
        mapTable.clear()
    }
}
