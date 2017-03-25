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

    private val _json = Json()

    init {

        _graph = ConversationGraph()

        //create
        _dialogText = Label("No Conversation", Utility.STATUSUI_SKIN)
        _dialogText.setWrap(true)
        _dialogText.setAlignment(Align.center)
        _listItems = List<ConversationChoice>(Utility.STATUSUI_SKIN)

        val scrollPane = ScrollPane(_listItems)
        scrollPane.setOverscroll(false, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setScrollbarsOnTop(true)

        //layout
        add()
        add(_closeButton)
        row()

        this.defaults().expand().fill()
        this.add(_dialogText).pad(10f, 10f, 10f, 10f)
        this.row()
        this.add(scrollPane)

        //this.debug();
        this.pack()

        //Listeners
        _listItems.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val choice = _listItems.selected
                _graph!!.setCurrentConversation(choice.destinationId)
                _dialogText.setText(_graph!!.getConversationByID(choice.destinationId)!!.dialog)
                _listItems.setItems(*_graph!!.currentChoices.toTypedArray())
                _listItems.selectedIndex = -1
            }
        }
        )
    }

    fun loadConversation(entityConfig: EntityConfig) {
        val fullFilenamePath = entityConfig.conversationConfigPath
        if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
            Gdx.app.debug(TAG, "Conversation file does not exist!")
            _dialogText.setText("")
            _listItems.clearItems()
            return
        }

        currentEntityID = entityConfig.entityID
        val json = Json()
        val graph = json.fromJson(ConversationGraph::class.java, Gdx.files.internal(fullFilenamePath))
        setConversationGraph(graph)
    }

    fun setConversationGraph(graph: ConversationGraph) {
        this._graph = graph
        val id = _graph!!.currentConversationID
        val conversation = _graph!!.getConversationByID(id) ?: return
        this._dialogText.setText(conversation.dialog)
        this._listItems.setItems(*_graph!!.currentChoices.toTypedArray())
        _listItems.selectedIndex = -1
    }


    private val TAG = ConversationUI::class.java.simpleName


}
