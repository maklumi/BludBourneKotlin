package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Scaling

class InventoryUI(skin: Skin, textureAtlas: TextureAtlas) : Window("Inventory", skin, "solidbackground") {

    private val _numSlots = 50
    private val _lengthSlotRow = 10
    private val _dragAndDrop = DragAndDrop()

    init {
        this.setFillParent(true)

        //create
        val inventorySlotTable = Table()
        val playerSlotsTable = Table()

        //layout
        for (i in 1.._numSlots) {
            val inventorySlot = InventorySlot()
            _dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            if (i == 5 || i == 10 || i == 15 || i == 20) {
                //TEMP TODO
                val image = Image(PlayerHUD.itemsTextureAtlas.findRegion("armor01"))
                image.setScaling(Scaling.none)
                inventorySlot.add(image)

                _dragAndDrop.addSource(InventorySlotSource(inventorySlot, _dragAndDrop))
            }

            inventorySlotTable.add(inventorySlot).size(52f, 52f)

//            inventorySlotTable.add(image)
            if (i % _lengthSlotRow == 0) inventorySlotTable.row()
        }

        playerSlotsTable.add(Image(NinePatch(textureAtlas.createPatch("dialog")))).size(200f, 250f)

        this.add(playerSlotsTable).padBottom(20f).row()
        this.add(inventorySlotTable).row()
        this.pack()
    }
}
