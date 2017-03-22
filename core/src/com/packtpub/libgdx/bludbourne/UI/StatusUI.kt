package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

class StatusUI : Group() {
    private val textureAtlas: TextureAtlas
    private val hudBackgroundImage: Image
    private val hpBar: Image
    private val mpBar: Image
    private val xpBar: Image
    private val skin: Skin

    //Attributes
    private val level = 1
    private val gold = 0
    private val hp = 50
    private val mp = 50
    private val xp = 0

    init {
        textureAtlas = TextureAtlas(textureAtlasPath)
        hudBackgroundImage = Image(textureAtlas.findRegion("HUD_Background"))

        skin = Skin()
        skin.load(Gdx.files.internal("skins/uiskin.json"))

        val table = Table()

        val group = WidgetGroup()
        hpBar = Image(textureAtlas.findRegion("HP_Bar"))
        hpBar.setPosition(3f, 6f)

        val bar = Image(textureAtlas.findRegion("Bar"))

        group.addActor(bar)
        group.addActor(hpBar)

        val cell = table.add(group)
        cell.width(bar.width)
        cell.height(bar.height)

        val hpLabel = Label(" hp:", skin)
        table.add(hpLabel)
        val hp = Label(hp.toString(), skin)
        table.add(hp)
        table.row()

        val group2 = WidgetGroup()
        mpBar = Image(textureAtlas.findRegion("MP_Bar"))
        mpBar.setPosition(3f, 6f)

        val bar2 = Image(textureAtlas.findRegion("Bar"))

        group2.addActor(bar2)
        group2.addActor(mpBar)

        val cell2 = table.add(group2)
        cell2.width(bar2.width)
        cell2.height(bar2.height)

        val mpLabel = Label(" mp:", skin)
        table.add(mpLabel)
        val mp = Label(mp.toString(), skin)
        table.add(mp)
        table.row()

        val group3 = WidgetGroup()
        xpBar = Image(textureAtlas.findRegion("XP_Bar"))
        xpBar.setPosition(3f, 6f)

        val bar3 = Image(textureAtlas.findRegion("Bar"))

        group3.addActor(bar3)
        group3.addActor(xpBar)

        val cell3 = table.add(group3)
        cell3.width(bar3.width)
        cell3.height(bar3.height)

        val xpLabel = Label(" xp:", skin)
        table.add(xpLabel)
        val xp = Label(xp.toString(), skin)
        table.add(xp)
        table.row()

        val levelLabel = Label("lv:", skin)
        table.add(levelLabel)
        val levelVal = Label(level.toString(), skin)
        table.add(levelVal).align(Align.left)

        val goldLabel = Label("gp: ", skin)
        table.add(goldLabel)
        val goldVal = Label(gold.toString(), skin)
        table.add(goldVal)

        table.debug()
        table.setPosition(135f, 68f)
        table.setFillParent(true)

        this.addActor(hudBackgroundImage)
        this.addActor(table)
    }

    companion object {
        private val textureAtlasPath = "skins/statusui.pack"
    }

    /*
    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.draw(hudBackgroundImage, 0f, 0f, 4f, 3f)
    }
*/

}
