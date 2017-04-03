package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.packtpub.libgdx.bludbourne.*
import com.packtpub.libgdx.bludbourne.ComponentObserver.ComponentEvent.*
import com.packtpub.libgdx.bludbourne.audio.AudioManager
import com.packtpub.libgdx.bludbourne.audio.AudioObserver
import com.packtpub.libgdx.bludbourne.audio.AudioSubject
import com.packtpub.libgdx.bludbourne.battle.BattleObserver
import com.packtpub.libgdx.bludbourne.battle.BattleObserver.BattleEvent.*
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraphObserver
import com.packtpub.libgdx.bludbourne.profile.ProfileManager
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver
import com.packtpub.libgdx.bludbourne.quest.QuestGraph
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen


class PlayerHUD(camera: Camera, val player: Entity, val mapMgr: MapManager) :
        Screen, ProfileObserver, ComponentObserver, ConversationGraphObserver,
        StoreInventoryObserver, BattleObserver, InventoryObserver,
        StatusObserver,
        AudioSubject {

    private val _observers = Array<AudioObserver>()

    val stage: Stage
    private val viewport: Viewport
    private val statusUI: StatusUI
    private val inventoryUI: InventoryUI
    private val conversationUI: ConversationUI
    private val storeInventoryUI: StoreInventoryUI
    private var _questUI: QuestUI
    private val _battleUI: BattleUI
    private val json = Json()
    private val _messageBoxUI: Dialog
    private val INVENTORY_FULL = "Your inventory is full!"

    init {
        viewport = ScreenViewport(camera)
        stage = Stage(viewport)
//        stage.setDebugAll(true)

        _messageBoxUI = object : Dialog("Message", Utility.STATUSUI_SKIN, "solidbackground") {
            init {
                button("OK")
                text(INVENTORY_FULL)
            }

            override fun result(`object`: Any?) {
                cancel()
                isVisible = false
            }

        }
        _messageBoxUI.setVisible(false)
        _messageBoxUI.pack()
        _messageBoxUI.setPosition(stage.width / 2 - _messageBoxUI.getWidth() / 2, stage.height / 2 - _messageBoxUI.getHeight() / 2)

        statusUI = StatusUI()
        statusUI.isVisible = true
        statusUI.setPosition(0f, 0f)

        inventoryUI = InventoryUI()
        inventoryUI.isVisible = false
        inventoryUI.isMovable = false


        inventoryUI.setPosition(stage.width / 2f, 0f)


        conversationUI = ConversationUI()
        conversationUI.apply {
            isMovable = true
            isVisible = false
        }
        conversationUI.setPosition(stage.width / 2f, 0f)
        conversationUI.setSize(stage.width / 2f, stage.height / 2f)

        storeInventoryUI = StoreInventoryUI()
        storeInventoryUI.apply {
            isMovable = false
            isVisible = false
        }
        storeInventoryUI.setPosition(0f, 0f)

        _questUI = QuestUI()
        _questUI.isMovable = false
        _questUI.isVisible = false
        _questUI.setPosition(0f, stage.height / 2)
        _questUI.width = stage.width
        _questUI.height = stage.height / 2

        _battleUI = BattleUI()
        _battleUI.setFillParent(true)
        _battleUI.isVisible = false
        _battleUI.isMovable = false

        //removes all listeners including ones that handle focus
        _battleUI.clearListeners()

        stage.addActor(_battleUI)
        stage.addActor(_questUI)
        stage.addActor(storeInventoryUI)
        stage.addActor(conversationUI)
        stage.addActor(_messageBoxUI)
        stage.addActor(statusUI)
        stage.addActor(inventoryUI)
        statusUI.toFront()

        //add tooltips to the stage
        val actors = inventoryUI.inventoryActors
        for (actor in actors) {
            stage.addActor(actor)
        }

        val storeActors = storeInventoryUI.inventoryActors
        for (actor in storeActors) {
            stage.addActor(actor)
        }

        // Observers
        player.registerObserver(this)
        statusUI.addObserver(this)
        storeInventoryUI.addObserver(this)
        inventoryUI.addObserver(_battleUI.battleState)
        inventoryUI.addObserver(this)
        _battleUI.battleState.addObserver(this)
        this.addObserver(AudioManager)

        // Listeners
        val inventoryButton = statusUI.inventoryButton
        inventoryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                inventoryUI.isVisible = !inventoryUI.isVisible
            }
        })

        val questButton = statusUI.questButton
        questButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                _questUI.isVisible = !_questUI.isVisible
            }
        })

        conversationUI.closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                conversationUI.isVisible = false
                mapMgr.clearCurrentSelectedMapEntity()
            }
        })

        storeInventoryUI.closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                storeInventoryUI.savePlayerInventory()
                storeInventoryUI.cleanupStoreInventory()
                storeInventoryUI.isVisible = false
                mapMgr.clearCurrentSelectedMapEntity()
            }
        })

        //Music/Sound loading
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_BATTLE)
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE)
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN)
    }

    fun updateEntityObservers() {
        mapMgr.unregisterCurrentMapEntityObservers()

        _questUI.initQuests(mapMgr)

        mapMgr.registerCurrentMapEntityObservers(this)
    }

    override fun onNotify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent) {
        when (event) {
            ProfileObserver.ProfileEvent.PROFILE_LOADED -> {
                //if goldval is negative, this is our first save
                var goldVal = profileManager.getProperty("currentPlayerGP", Int::class.java) as Int
                val firstTime = goldVal < 0

                if (firstTime) {
                    //add default items if first time
                    val items: Array<InventoryItem.ItemTypeID> = player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0..items.size - 1) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY))
                    }
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                    profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.inventorySlotTable))
                }


                val inventory = profileManager.getProperty("playerInventory", Array::class.java) as Array<InventoryItemLocation>
                InventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)

                val equipInventory = profileManager.getProperty("playerEquipInventory", Array::class.java) as Array<InventoryItemLocation>
                if (equipInventory.size > 0) {
                    inventoryUI.resetEquipSlots()
                    InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                }

                val quests = profileManager.getProperty("playerQuests", Array::class.java)
                _questUI.quests = quests as Array<QuestGraph>

                var xpMaxVal = profileManager.getProperty("currentPlayerXPMax", Int::class.java) as Int
                val xpVal = profileManager.getProperty("currentPlayerXP", Int::class.java) as Int

                var hpMaxVal = profileManager.getProperty("currentPlayerHPMax", Int::class.java) as Int
                var hpVal = profileManager.getProperty("currentPlayerHP", Int::class.java) as Int

                var mpMaxVal = profileManager.getProperty("currentPlayerMPMax", Int::class.java) as Int
                var mpVal = profileManager.getProperty("currentPlayerMP", Int::class.java) as Int

                var levelVal = profileManager.getProperty("currentPlayerLevel", Int::class.java) as Int

                // check gold
                if (firstTime) {
                    // start the player with some money
                    goldVal = 20
                    levelVal = 1
                    statusUI.setStatusForLevel(levelVal)
                } else {

                    //set the current max values first
                    statusUI.setXPValueMax(xpMaxVal)
                    statusUI.setHPValueMax(hpMaxVal)
                    statusUI.setMPValueMax(mpMaxVal)

                    statusUI.setXPValue(xpVal)
                    statusUI.setHPValue(hpVal)
                    statusUI.setMPValue(mpVal)
                }

                //then add in current values
                statusUI.setGoldValue(goldVal)
                statusUI.setLevelValue(levelVal)
            }

            ProfileObserver.ProfileEvent.SAVING_PROFILE -> {
                profileManager.setProperty("playerQuests", _questUI.quests)
                profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.inventorySlotTable))
                profileManager.setProperty("playerEquipInventory", InventoryUI.getInventory(inventoryUI.equipSlots))
                profileManager.setProperty("currentPlayerGP", statusUI.getGoldValue())
                profileManager.setProperty("currentPlayerLevel", statusUI.getLevelValue())
                profileManager.setProperty("currentPlayerXP", statusUI.getXPValue())
                profileManager.setProperty("currentPlayerXPMax", statusUI.getXPValueMax())
                profileManager.setProperty("currentPlayerHP", statusUI.getHPValue())
                profileManager.setProperty("currentPlayerHPMax", statusUI.getHPValueMax())
                profileManager.setProperty("currentPlayerMP", statusUI.getMPValue())
                profileManager.setProperty("currentPlayerMPMax", statusUI.getMPValueMax())
            }
        }
    }

    override fun onNotify(value: String, event: ComponentObserver.ComponentEvent) {

        when (event) {
            LOAD_CONVERSATION -> {
                val config = json.fromJson(EntityConfig::class.java, value)
                conversationUI.loadConversation(config)
                conversationUI.getCurrentConversationGraph().addObserver(this)
            }
            SHOW_CONVERSATION -> {
                val configShow = json.fromJson(EntityConfig::class.java, value)
                if (configShow.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = true
                }
            }
            HIDE_CONVERSATION -> {
                val configHide = json.fromJson(EntityConfig::class.java, value)
                if (configHide.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = false
                }
            }
            QUEST_LOCATION_DISCOVERED -> {
                val string = value.split(Component.MESSAGE_TOKEN)
                val questID = string [0]
                val questTaskID = string [1]

                _questUI.questTaskComplete(questID, questTaskID)
                updateEntityObservers()
            }
            ENEMY_SPAWN_LOCATION_CHANGED -> {
                val enemyZoneID = value
                _battleUI.battleZoneTriggered(enemyZoneID.toInt())
            }
            PLAYER_HAS_MOVED -> {
                //System.out.println("Player has moved!!!");
                if (_battleUI.isBattleReady()) {
                    MainGameScreen.gameState = MainGameScreen.GameState.SAVING
                    mapMgr.disableCurrentmapMusic()
                    notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE)
                    _battleUI.toBack()
                    _battleUI.isVisible = true
                }
            }

        }

    }

    override fun onNotify(graph: ConversationGraph, event: ConversationGraphObserver.ConversationCommandEvent) {
        when (event) {
            ConversationGraphObserver.ConversationCommandEvent.LOAD_STORE_INVENTORY -> {
                val selectedEntity = mapMgr.currentSelectedEntity ?: return

                val inventory = InventoryUI.getInventory(inventoryUI.inventorySlotTable)
                storeInventoryUI.loadPlayerInventory(inventory)

                val items = selectedEntity.entityConfig.inventory
                val itemLocations = Array<InventoryItemLocation>()
                for (i in 0..items.size - 1) {
                    itemLocations.add(InventoryItemLocation(i, items[i].toString(), 1, InventoryUI.PLAYER_INVENTORY))
                }

                storeInventoryUI.loadStoreInventory(itemLocations)

                conversationUI.isVisible = false

                storeInventoryUI.toFront()
                storeInventoryUI.isVisible = true
            }

            ConversationGraphObserver.ConversationCommandEvent.EXIT_CONVERSATION -> {
                conversationUI.isVisible = false
                mapMgr.clearCurrentSelectedMapEntity()
            }

            ConversationGraphObserver.ConversationCommandEvent.ACCEPT_QUEST -> {
                val currentlySelectedEntity = mapMgr.currentSelectedEntity ?: return
                val config = currentlySelectedEntity.entityConfig
                val questGraph = _questUI.loadQuest(config.questConfigPath)
                if (questGraph != null) {
                    //Update conversation dialog
                    config.conversationConfigPath = QuestUI.RETURN_QUEST
                    config.currentQuestID = questGraph.questID
                    ProfileManager.instance.setProperty(config.entityID, config)
                    updateEntityObservers()
                }

                conversationUI.isVisible = false
                mapMgr.clearCurrentSelectedMapEntity()
            }

            ConversationGraphObserver.ConversationCommandEvent.RETURN_QUEST -> {
                val returnEntity = mapMgr.currentSelectedEntity ?: return
                val configReturn = returnEntity.entityConfig

                val configReturnProperty = ProfileManager.instance.getProperty(configReturn.entityID, EntityConfig::class.java) ?: return

                val questID = configReturnProperty.currentQuestID
                if (_questUI.isQuestReadyForReturn(questID)) {
                    val quest = _questUI.getQuestByID(questID)
                    statusUI.addXPValue(quest!!.xpReward)
                    statusUI.addGoldValue(quest.goldReward)
                    inventoryUI.removeQuestItemFromInventory(questID)

                    configReturnProperty.conversationConfigPath = QuestUI.FINISHED_QUEST
                    ProfileManager.instance.setProperty(configReturnProperty.entityID, configReturnProperty)
                }

                conversationUI.isVisible = false
                mapMgr.clearCurrentSelectedMapEntity()
            }

            ConversationGraphObserver.ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY -> {
                val entity = mapMgr.currentSelectedEntity ?: return

                if (inventoryUI.doesInventoryHaveSpace()) {
                    inventoryUI.addEntityToInventory(entity, entity.entityConfig.currentQuestID)
                    mapMgr.clearCurrentSelectedMapEntity()
                    conversationUI.isVisible = false
                    entity.unregisterObservers()
                    mapMgr.removeMapQuestEntity(entity)
                    _questUI.updateQuests(mapMgr)
                } else {
                    mapMgr.clearCurrentSelectedMapEntity()
                    conversationUI.isVisible = false
                    _messageBoxUI.isVisible = true
                }

            }

            ConversationGraphObserver.ConversationCommandEvent.NONE -> {
                // do nothing
            }
        }
    }

    override fun onNotify(value: String, event: StoreInventoryObserver.StoreInventoryEvent) {
        when (event) {
            StoreInventoryObserver.StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED -> {
                statusUI.setGoldValue(value.toInt())
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE)
            }

            StoreInventoryObserver.StoreInventoryEvent.PLAYER_INVENTORY_UPDATED -> {
                val items = json.fromJson(Array::class.java, value) as Array<InventoryItemLocation>
                InventoryUI.populateInventory(inventoryUI.inventorySlotTable, items, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
            }
        }
    }

    override fun onNotify(value: Int, event: StatusObserver.StatusEvent) {
        when (event) {
            StatusObserver.StatusEvent.UPDATED_GP -> {
                storeInventoryUI.setPlayerGP(value)
                ProfileManager.instance.setProperty("currentPlayerGP", statusUI.getGoldValue())
            }
            StatusObserver.StatusEvent.UPDATED_HP -> {
                ProfileManager.instance.setProperty("currentPlayerHP", statusUI.getHPValue())
            }
            StatusObserver.StatusEvent.UPDATED_LEVEL -> {
                ProfileManager.instance.setProperty("currentPlayerLevel", statusUI.getLevelValue())
            }
            StatusObserver.StatusEvent.UPDATED_MP -> {
                ProfileManager.instance.setProperty("currentPlayerMP", statusUI.getMPValue())
            }
            StatusObserver.StatusEvent.UPDATED_XP -> {
                ProfileManager.instance.setProperty("currentPlayerXP", statusUI.getXPValue())
            }
        }
    }

    override fun onNotify(enemyEntity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            OPPONENT_HIT_DAMAGE -> {
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN)
            }
            
            OPPONENT_DEFEATED -> {
                MainGameScreen.gameState = MainGameScreen.GameState.RUNNING
                val goldReward = enemyEntity.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_GP_REWARD.toString()).toInt()
                statusUI.addGoldValue(goldReward)
                val xpReward = Integer.parseInt(enemyEntity.entityConfig.getPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString()))
                statusUI.addXPValue(xpReward)
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE)
                mapMgr.enableCurrentmapMusic()
                _battleUI.isVisible = false
            }
            PLAYER_RUNNING -> {
                MainGameScreen.gameState = MainGameScreen.GameState.RUNNING
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE)
                mapMgr.enableCurrentmapMusic()
                _battleUI.isVisible = false
            }
            PLAYER_HIT_DAMAGE -> {
                val hpVal = ProfileManager.instance.getProperty("currentPlayerHP", Int::class.java) as Int
                statusUI.setHPValue(hpVal)

                if (hpVal <= 0) {
                    notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE)
                    _battleUI.isVisible = false
                    MainGameScreen.gameState = MainGameScreen.GameState.GAME_OVER
                }
            }
            PLAYER_USED_MAGIC -> {
                val mpVal = ProfileManager.instance.getProperty("currentPlayerMP", Int::class.java) as Int
                statusUI.setMPValue(mpVal)
            }
            else -> {

            }
        }
    }

    override fun onNotify(value: String, event: InventoryObserver.InventoryEvent) {
        when (event) {
            InventoryObserver.InventoryEvent.ITEM_CONSUMED -> {
                val strings = value.split(Component.MESSAGE_TOKEN.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (strings.size != 2) return

                val type = strings[0].toInt()
                val typeValue = strings[1].toInt()

                if (InventoryItem.doesRestoreHP(type)) {
                    statusUI.addHPValue(typeValue)
                } else if (InventoryItem.doesRestoreMP(type)) {
                    statusUI.addMPValue(typeValue)
                }
            }
            else -> {
            }
        }
    }

    override fun show() {}

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }

    override fun addObserver(audioObserver: AudioObserver) {
        _observers.add(audioObserver)

    }

    override fun removeObserver(audioObserver: AudioObserver) {
        _observers.removeValue(audioObserver, true)

    }

    override fun removeAllObservers() {
        _observers.removeAll(_observers, true)

    }

    override fun notify(command: AudioObserver.AudioCommand, event: AudioObserver.AudioTypeEvent) {
        _observers.forEach { it.onNotify(command, event) }
    }
}
