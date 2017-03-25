package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
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

class PlayerHUD(camera: Camera, val player: Entity, val mapMgr: MapManager) :
        Screen, ProfileObserver, ComponentObserver, ConversationGraphObserver {

    val stage: Stage
    private val viewport: Viewport
    private val statusUI: StatusUI
    private val inventoryUI: InventoryUI
    private val conversationUI: ConversationUI
    private val storeInventoryUI: StoreInventoryUI

    private val json = Json()


    init {
        viewport = ScreenViewport(camera)
        stage = Stage(viewport)
//        stage.setDebugAll(true)

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

        stage.addActor(statusUI)
        stage.addActor(inventoryUI)
        stage.addActor(conversationUI)
        stage.addActor(storeInventoryUI)

        //add tooltips to the stage
        val actors = inventoryUI.inventoryActors
        for (actor in actors) {
            stage.addActor(actor)
        }

        val storeActors = storeInventoryUI.inventoryActors
        for (actor in storeActors) {
            stage.addActor(actor)
        }

        val inventoryButton = statusUI.inventoryButton
        inventoryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                inventoryUI.isVisible = !inventoryUI.isVisible
            }
        })

        conversationUI._closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                conversationUI.isVisible = false
            }
        })
    }

    override fun onNotify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent) {
        when (event) {
            ProfileObserver.ProfileEvent.PROFILE_LOADED -> {
                val inventory = profileManager.getProperty("playerInventory", Array::class.java) as Array<InventoryItemLocation>
                if (inventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory, inventoryUI.dragAndDrop)
                } else {
                    //add default items if nothing is found
                    val items: Array<InventoryItem.ItemTypeID> = player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0..items.size - 1) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1))
                    }
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations, inventoryUI.dragAndDrop)
                }

                val equipInventory = profileManager.getProperty("playerEquipInventory", Array::class.java) as Array<InventoryItemLocation>
                if (equipInventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop)
                }
            }

            ProfileObserver.ProfileEvent.SAVING_PROFILE -> {
                profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.inventorySlotTable))
                profileManager.setProperty("playerEquipInventory", InventoryUI.getInventory(inventoryUI.equipSlots))
            }
        }
    }

    override fun onNotify(value: String, event: ComponentObserver.UIEvent) {

        when (event) {
            ComponentObserver.UIEvent.LOAD_CONVERSATION -> {
                val config = json.fromJson(EntityConfig::class.java, value)
                conversationUI.loadConversation(config)
                conversationUI.getCurrentConversationGraph().addObserver(this)
            }
            ComponentObserver.UIEvent.SHOW_CONVERSATION -> {
                val configShow = json.fromJson(EntityConfig::class.java, value)
                if (configShow.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = true
                }
            }
            ComponentObserver.UIEvent.HIDE_CONVERSATION -> {
                val configHide = json.fromJson(EntityConfig::class.java, value)
                if (configHide.entityID.equals(conversationUI.currentEntityID, true)) {
                    conversationUI.isVisible = false
                }
            }
        }

    }

    override fun onNotify(graph: ConversationGraph, event: ConversationGraphObserver.ConversationCommandEvent) {
        when (event) {
            ConversationGraphObserver.ConversationCommandEvent.LOAD_STORE_INVENTORY -> {
                val inventory = InventoryUI.getInventory(inventoryUI.inventorySlotTable)
                storeInventoryUI.loadPlayerInventory(inventory)

                val entities = mapMgr.getCurrentMapEntities()
                for (entity in entities) {
                    if (entity.entityConfig.entityID.equals("TOWN_BLACKSMITH", true)) {
                        val items = entity.entityConfig.inventory
                        val itemLocations = Array<InventoryItemLocation>()
                        for (i in 0..items.size - 1) {
                            itemLocations.add(InventoryItemLocation(i, items[i].toString(), 1))
                        }
                        storeInventoryUI.loadStoreInventory(itemLocations)
                        break
                    }
                }

                conversationUI.isVisible = false

                storeInventoryUI.toFront()
                storeInventoryUI.isVisible = true
            }

            ConversationGraphObserver.ConversationCommandEvent.EXIT_CONVERSATION -> {
                conversationUI.isVisible = false
            }

            ConversationGraphObserver.ConversationCommandEvent.NONE -> {
                // do nothing
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
