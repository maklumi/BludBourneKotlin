package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.bludbourne.Utility

class StatusUI : Window("stats", Utility.STATUSUI_SKIN), StatusSubject {

    private val hpBar: Image
    private val mpBar: Image
    private val xpBar: Image
    val inventoryButton: ImageButton
    val observers = Array<StatusObserver>()


    //Attributes
    private val levelVal = 1
    private var _goldVal = -1
    private val hpVal = 50
    private val mpVal = 50
    private val xpVal = 0

    val goldLabel: Label

    init {

        //groups
        val group = WidgetGroup()
        val group2 = WidgetGroup()
        val group3 = WidgetGroup()

        //images
        hpBar = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"))
        val bar = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"))
        mpBar = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"))
        val bar2 = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"))
        xpBar = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"))
        val bar3 = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"))

        //labels
        val hpLabel = Label(" hp:", Utility.STATUSUI_SKIN)
        val hp = Label(hpVal.toString(), Utility.STATUSUI_SKIN)
        val mpLabel = Label(" mp:", Utility.STATUSUI_SKIN)
        val mp = Label(mpVal.toString(), Utility.STATUSUI_SKIN)
        val xpLabel = Label(" xp:", Utility.STATUSUI_SKIN)
        val xp = Label(xpVal.toString(), Utility.STATUSUI_SKIN)
        val levelLabel = Label(" lv:", Utility.STATUSUI_SKIN)
        val levelVal = Label(levelVal.toString(), Utility.STATUSUI_SKIN)
        goldLabel = Label(" gp:", Utility.STATUSUI_SKIN)
        val goldVal = Label(_goldVal.toString(), Utility.STATUSUI_SKIN)

        //buttons
        inventoryButton = ImageButton(Utility.STATUSUI_SKIN, "inventory-button")
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

    fun getGoldValue() : Int = _goldVal

    fun setGoldValue(value: Int) {
        _goldVal = value
        goldLabel.setText(getGoldValue().toString())
        notify(value, StatusObserver.StatusEvent.UPDATED_GP)
    }

    override fun addObserver(statusObserver: StatusObserver) {
        observers.add(statusObserver)
    }

    override fun removeObserver(statusObserver: StatusObserver) {
        observers.removeValue(statusObserver, true)
    }

    override fun removeAllObservers() {
        observers.forEach { observer -> observers.removeValue(observer, true) }
    }

    override fun notify(value: Int, event: StatusObserver.StatusEvent) {
        observers.forEach { observer -> observer.onNotify(value, event) }
    }
}
