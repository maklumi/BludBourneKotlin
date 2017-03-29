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
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraphObserver
import com.packtpub.libgdx.bludbourne.profile.ProfileManager
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver
import com.packtpub.libgdx.bludbourne.quest.QuestGraph
import com.badlogic.gdx.scenes.scene2d.Touchable


class PlayerHUD(camera: Camera, val player: Entity, val mapMgr: MapManager) :
        Screen, ProfileObserver, ComponentObserver, ConversationGraphObserver,
        StoreInventoryObserver, StatusObserver {

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
        _battleUI.touchable = Touchable.childrenOnly

        stage.addActor(_questUI)
        stage.addActor(statusUI)
        stage.addActor(inventoryUI)
        stage.addActor(conversationUI)
        stage.addActor(storeInventoryUI)
        stage.addActor(_messageBoxUI)
        stage.addActor(_battleUI)
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
        ProfileManager.instance.addObserver(this)
        player.registerObserver(this)
        statusUI.addObserver(this)
        storeInventoryUI.addObserver(this)

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
                    InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                }

                val quests = profileManager.getProperty("playerQuests", Array::class.java)
                _questUI.quests = quests as Array<QuestGraph>

                var xpMaxVal = profileManager.getProperty("currentPlayerXPMax", Int::class.java)
                var xpVal = profileManager.getProperty("currentPlayerXP", Int::class.java)

                // check gold
                if (firstTime) {
                    // start the player with some money
                    goldVal = 20
                    xpMaxVal = 200
                }

                //set the current max values first
                statusUI.setXPValueMax(xpMaxVal!!)

                //then add in current values
                statusUI.setGoldValue(goldVal)
                statusUI.setXPValue(xpVal!!)
            }

            ProfileObserver.ProfileEvent.SAVING_PROFILE -> {
                profileManager.setProperty("playerQuests", _questUI.quests)
                profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.inventorySlotTable))
                profileManager.setProperty("playerEquipInventory", InventoryUI.getInventory(inventoryUI.equipSlots))
                profileManager.setProperty("currentPlayerGP", statusUI.getGoldValue())
                profileManager.setProperty("currentPlayerXP", statusUI.getXPValue())
                profileManager.setProperty("currentPlayerXPMax", statusUI.getXPValueMax())
            }
        }
    }

    override fun onNotify(value: String, event: ComponentObserver.ComponentEvent) {

        when (event) {
            ComponentObserver.ComponentEvent.LOAD_CONVERSATION -> {
                val config = json.fromJson(EntityConfig::class.java, value)
                conversationUI.loadConversation(config)
                conversationUI.getCurrentConversationGraph().addObserver(this)
            }
            ComponentObserver.ComponentEvent.SHOW_CONVERSATION -> {
                val configShow = json.fromJson(EntityConfig::class.java, value)
                if (configShow.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = true
                }
            }
            ComponentObserver.ComponentEvent.HIDE_CONVERSATION -> {
                val configHide = json.fromJson(EntityConfig::class.java, value)
                if (configHide.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = false
                }
            }
            ComponentObserver.ComponentEvent.QUEST_LOCATION_DISCOVERED -> {
                val string = value.split(Component.MESSAGE_TOKEN)
                val questID = string [0]
                val questTaskID = string [1]

                _questUI.questTaskComplete(questID, questTaskID)
                updateEntityObservers()
            }
            ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED -> {
                val enemyZoneID = value
                _battleUI.battleZoneTriggered(Integer.valueOf(enemyZoneID))
                _battleUI.isVisible = true
                _battleUI.toBack()
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
}
