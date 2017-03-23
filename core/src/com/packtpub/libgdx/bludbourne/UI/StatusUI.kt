package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

class StatusUI(skin: Skin, textureAtlas: TextureAtlas) : Window("stats", skin) {

    private val hpBar: Image
    private val mpBar: Image
    private val xpBar: Image
    val inventoryButton: ImageButton


    //Attributes
    private val levelVal = 1
    private val goldVal = 0
    private val hpVal = 50
    private val mpVal = 50
    private val xpVal = 0

    init {

        //groups
        val group = WidgetGroup()
        val group2 = WidgetGroup()
        val group3 = WidgetGroup()

        //images
        hpBar = Image(textureAtlas.findRegion("HP_Bar"))
        val bar = Image(textureAtlas.findRegion("Bar"))
        mpBar = Image(textureAtlas.findRegion("MP_Bar"))
        val bar2 = Image(textureAtlas.findRegion("Bar"))
        xpBar = Image(textureAtlas.findRegion("XP_Bar"))
        val bar3 = Image(textureAtlas.findRegion("Bar"))

        //labels
        val hpLabel = Label(" hp:", skin)
        val hp = Label(hpVal.toString(), skin)
        val mpLabel = Label(" mp:", skin)
        val mp = Label(mpVal.toString(), skin)
        val xpLabel = Label(" xp:", skin)
        val xp = Label(xpVal.toString(), skin)
        val levelLabel = Label(" lv:", skin)
        val levelVal = Label(levelVal.toString(), skin)
        val goldLabel = Label(" gp:", skin)
        val goldVal = Label(goldVal.toString(), skin)

        //buttons
        inventoryButton = ImageButton(skin, "inventory-button")
        inventoryButton.imageCell.size(32f, 32f)

        //Align images
        hpBar.setPosition(3f, 6f)
        mpBar.setPosition(3f, 6f)
        xpBar.setPosition(3f, 6f)

        //add to widget groups
        group.addActor(bar)
        group.addActor(hpBar)
        group2.addActor(bar2)
        group2.addActor(mpBar)
        group3.addActor(bar3)
        group3.addActor(xpBar)

        //Add to layout
        defaults().expand().fill()

        //account for the title padding
        this.pad(this.padTop + 10, 10f, 10f, 10f)

        this.add()
        this.add()
        this.add(inventoryButton).align(Align.right)
        this.row()

        this.add(group).size(bar.width, bar.height)
        this.add(hpLabel)
        this.add(hp).align(Align.left)
        this.row()

        this.add(group2).size(bar2.width, bar2.height)
        this.add(mpLabel)
        this.add(mp).align(Align.left)
        this.row()

        this.add(group3).size(bar3.width, bar3.height)
        this.add(xpLabel)
        this.add(xp).align(Align.left)
        this.row()

        this.add(levelLabel).align(Align.left)
        this.add(levelVal).align(Align.left)
        this.row()
        this.add(goldLabel)
        this.add(goldVal).align(Align.left)

        //this.debug();
        this.pack()
    }


}
