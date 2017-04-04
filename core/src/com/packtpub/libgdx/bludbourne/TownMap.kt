package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.profile.ProfileManager


class TownMap : Map(MapFactory.MapType.TOWN, TownMap.mapPath) {

    init {
        npcStartPositions.forEach { position ->
            val entity = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_GUARD_WALKING)
            entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))
            mapEntities.add(entity)
        }

        // Special cases
        val blackSmith = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH)
        initSpecialEntityPosition(blackSmith)
        mapEntities.add(blackSmith)

        val mage = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_MAGE)
        initSpecialEntityPosition(mage)
        mapEntities.add(mage)


        val innKeeper = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_INNKEEPER)
        initSpecialEntityPosition(innKeeper)
        mapEntities.add(innKeeper)

        val townFolk1 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK1)
        initSpecialEntityPosition(townFolk1)
        mapEntities.add(townFolk1)

        val townFolk2 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK2)
        initSpecialEntityPosition(townFolk2)
        mapEntities.add(townFolk2)

        val townFolk3 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK3)
        initSpecialEntityPosition(townFolk3)
        mapEntities.add(townFolk3)

        val townFolk4 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK4)
        initSpecialEntityPosition(townFolk4)
        mapEntities.add(townFolk4)

        val townFolk5 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK5)
        initSpecialEntityPosition(townFolk5)
        mapEntities.add(townFolk5)

        val townFolk6 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK6)
        initSpecialEntityPosition(townFolk6)
        mapEntities.add(townFolk6)

        val townFolk7 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK7)
        initSpecialEntityPosition(townFolk7)
        mapEntities.add(townFolk7)

        val townFolk8 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK8)
        initSpecialEntityPosition(townFolk8)
        mapEntities.add(townFolk8)

        val townFolk9 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK9)
        initSpecialEntityPosition(townFolk9)
        mapEntities.add(townFolk9)

        val townFolk10 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK10)
        initSpecialEntityPosition(townFolk10)
        mapEntities.add(townFolk10)

        val townFolk11 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK11)
        initSpecialEntityPosition(townFolk11)
        mapEntities.add(townFolk11)

        val townFolk12 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK12)
        initSpecialEntityPosition(townFolk12)
        mapEntities.add(townFolk12)

        val townFolk13 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK13)
        initSpecialEntityPosition(townFolk13)
        mapEntities.add(townFolk13)

        val townFolk14 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK14)
        initSpecialEntityPosition(townFolk14)
        mapEntities.add(townFolk14)

        val townFolk15 = EntityFactory.getEntityByName(EntityFactory.EntityName.TOWN_FOLK15)
        initSpecialEntityPosition(townFolk15)
        mapEntities.add(townFolk15)

    }

    override fun updateMapEntities(mapMgr: MapManager, batch: Batch, delta: Float) {
        for (i in 0..mapEntities.size - 1) {
            mapEntities[i].update(mapMgr, batch, delta)
        }
        for (i in 0..mapQuestEntities.size - 1) {
            mapQuestEntities[i].update(mapMgr, batch, delta)
        }
    }


    private fun initSpecialEntityPosition(entity: Entity) {
        var position = Vector2(0f, 0f)

        if (specialNPCStartPositions.containsKey(entity.entityConfig.entityID)) {
            position = specialNPCStartPositions[entity.entityConfig.entityID]!!
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position))

        //Overwrite default if special config is found
        val entityConfig = ProfileManager.instance.getProperty(entity.entityConfig.entityID, EntityConfig::class.java)
        if (entityConfig != null) {
            entity.entityConfig = entityConfig
        }

    }

    override fun unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TOWN)
    }

    override fun loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TOWN)
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TOWN)
    }

    companion object {
        private val mapPath = "maps/town.tmx"
    }
}
