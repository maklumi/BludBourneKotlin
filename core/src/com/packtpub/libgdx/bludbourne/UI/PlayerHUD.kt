package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.packtpub.libgdx.bludbourne.Entity
import com.packtpub.libgdx.bludbourne.InventoryItem
import com.packtpub.libgdx.bludbourne.profile.ProfileManager
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver

class PlayerHUD(camera: Camera, val player: Entity) : Screen, ProfileObserver {

    val stage: Stage
    private val viewport: Viewport
    private val statusUI: StatusUI
    private val inventoryUI: InventoryUI

    companion object {
        private val STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas"
        private val STATUSUI_SKIN_PATH = "skins/statusui.json"
        private val ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas"
        private val ITEMS_SKIN_PATH = "skins/items.json"
        val statusUITextureAtlas = TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH)
        val itemsTextureAtlas = TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH)
        val statusUISkin = Skin(Gdx.files.internal(STATUSUI_SKIN_PATH), statusUITextureAtlas)
    }

    init {
        viewport = ScreenViewport(camera)
        stage = Stage(viewport)
//        stage.setDebugAll(true)

        statusUI = StatusUI(statusUISkin, statusUITextureAtlas)
        statusUI.isVisible = true
        statusUI.setPosition(0f, 0f)

        inventoryUI = InventoryUI(statusUISkin, statusUITextureAtlas)
        inventoryUI.isVisible = false
        inventoryUI.isMovable = false


        inventoryUI.setPosition(stage.width / 2f, 0f)


        stage.addActor(statusUI)
        stage.addActor(inventoryUI)

        //add tooltips to the stage
        val actors = inventoryUI.inventoryActors
        for (actor in actors) {
            stage.addActor(actor)
        }

        val inventoryButton = statusUI.inventoryButton
        inventoryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                inventoryUI.isVisible = !inventoryUI.isVisible
            }
        })
    }

    override fun onNotify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent) {
        when (event) {
            ProfileObserver.ProfileEvent.PROFILE_LOADED -> {
                val inventory = profileManager.getProperty("playerInventory", Array::class.java) as Array<InventoryItemLocation>
                if (inventory.size > 0) {
                    inventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory)
                } else {
                    //add default items if nothing is found
                    val items: Array<InventoryItem.ItemTypeID> = player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0..items.size - 1) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1))
                    }
                    inventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations)
                }

                val equipInventory = profileManager.getProperty("playerEquipInventory", Array::class.java) as Array<InventoryItemLocation>
                if (equipInventory.size > 0) {
                    inventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory)
                }
            }

            ProfileObserver.ProfileEvent.SAVING_PROFILE -> {
                profileManager.setProperty("playerInventory", inventoryUI.getInventory(inventoryUI.inventorySlotTable))
                profileManager.setProperty("playerEquipInventory", inventoryUI.getInventory(inventoryUI.equipSlots))
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
