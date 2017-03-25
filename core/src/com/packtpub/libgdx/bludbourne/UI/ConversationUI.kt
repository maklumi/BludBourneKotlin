package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.EntityConfig
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.dialog.ConversationChoice
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph

class ConversationUI : Window("dialog", Utility.STATUSUI_SKIN, "solidbackground") {

    private val _dialogText: Label
    private val _listItems: List<ConversationChoice>
    private var _graph: ConversationGraph? = null
    var currentEntityID: String? = null
        private set

    val _closeButton = TextButton("X", Utility.STATUSUI_SKIN)

    init {

        _graph = ConversationGraph()

        //create
        _dialogText = Label("No Conversation", Utility.STATUSUI_SKIN)
        _dialogText.setWrap(true)
        _dialogText.setAlignment(Align.center)
        _listItems = List<ConversationChoice>(Utility.STATUSUI_SKIN)

        val scrollPane = ScrollPane(_listItems, Utility.STATUSUI_SKIN, "inventoryPane")
        scrollPane.setOverscroll(false, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setForceScroll(true, false)
        scrollPane.setScrollBarPositions(false, true)

        //layout
        add()
        add(_closeButton)
        row()

        this.defaults().expand().fill()
        this.add(_dialogText).pad(10f, 10f, 10f, 10f)
        this.row()
        this.add(scrollPane).pad(10f, 10f, 10f, 10f)

        //this.debug();
        this.pack()

        //Listeners
        _listItems.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val choice = _listItems.selected ?: return
                populateConversationDialog(choice.destinationId)
            }
        }
        )
    }

    fun loadConversation(entityConfig: EntityConfig) {
        val fullFilenamePath = entityConfig.conversationConfigPath
        _dialogText.setText("")
        _listItems.clearItems()

        if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
            Gdx.app.debug(TAG, "Conversation file does not exist!")
            return
        }

        currentEntityID = entityConfig.entityID
        this.titleLabel.setText(entityConfig.entityID)
        val json = Json()
        val graph = json.fromJson(ConversationGraph::class.java, Gdx.files.internal(fullFilenamePath))
        setConversationGraph(graph)
    }

    fun setConversationGraph(graph: ConversationGraph) {
        this._graph = graph
        populateConversationDialog(_graph!!.currentConversationID)
    }

    fun populateConversationDialog(conversationID: String) {
        val conversation = _graph!!.getConversationByID(conversationID) ?: return
        _graph!!.setCurrentConversation(conversationID)
        _dialogText.setText(conversation.dialog)
        val choices = _graph!!.currentChoices
        _listItems.setItems(*choices.toTypedArray())
        _listItems.selectedIndex = -1

    }

    private val TAG = ConversationUI::class.java.simpleName
    
}
