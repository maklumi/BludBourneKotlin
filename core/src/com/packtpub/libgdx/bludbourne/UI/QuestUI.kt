package com.packtpub.libgdx.bludbourne.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.packtpub.libgdx.bludbourne.MapManager
import com.packtpub.libgdx.bludbourne.Utility
import com.packtpub.libgdx.bludbourne.quest.QuestGraph
import com.packtpub.libgdx.bludbourne.quest.QuestTask


class QuestUI : Window("Quest Log", Utility.STATUSUI_SKIN, "solidbackground") {

    private val _listQuests = List<QuestGraph>(Utility.STATUSUI_SKIN)
    private val _listTasks = List<QuestTask>(Utility.STATUSUI_SKIN)
    private val _json: Json = Json()
    private var _quests: Array<QuestGraph> = Array()
    private val _questLabel = Label("Quests:", Utility.STATUSUI_SKIN)
    private val _tasksLabel = Label("Tasks:", Utility.STATUSUI_SKIN)

    init {

        //create
        val scrollPane = ScrollPane(_listQuests, Utility.STATUSUI_SKIN, "inventoryPane")
        scrollPane.setOverscroll(false, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setForceScroll(true, false)


        val scrollPaneTasks = ScrollPane(_listTasks, Utility.STATUSUI_SKIN, "inventoryPane")
        scrollPaneTasks.setOverscroll(false, false)
        scrollPaneTasks.setFadeScrollBars(false)
        scrollPaneTasks.setForceScroll(true, false)

        //layout
        this.add(_questLabel).align(Align.left)
        this.add(_tasksLabel).align(Align.left)
        this.row()
        this.defaults().expand().fill()
        this.add(scrollPane).padRight(5f)
        this.add(scrollPaneTasks).padLeft(5f)

        //this.debug();
        this.pack()

        //Listeners
        _listQuests.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val quest = _listQuests.selected
                populateQuestDialog(quest)
            }
        }
        )
    }

    fun loadQuest(questConfigPath: String): QuestGraph? {
        if (questConfigPath.isEmpty() || !Gdx.files.internal(questConfigPath).exists()) {
            Gdx.app.debug(TAG, "Quest file does not exist!")
            return null
        }

        val graph = _json.fromJson(QuestGraph::class.java, Gdx.files.internal(questConfigPath))
        if (doesQuestExist(graph.questID)) {
            return null
        }

        clearDialog()
        _quests.add(graph)
        updateQuestsItemList()
        return graph
    }

    fun isQuestReadyForReturn(questID: String): Boolean {
        if (questID.isEmpty()) {
            Gdx.app.debug(TAG, "Quest ID not valid")
            return false
        }

        if (!doesQuestExist(questID)) return false

        val graph = getQuestByID(questID) ?: return false

        if (graph.updateQuestForReturn()) {
            graph.isQuestComplete = true
        } else {
            return false
        }
        return true
    }

    fun getQuestByID(questGraphID: String): QuestGraph? {
        for (questGraph in _quests) {
            if (questGraph.questID.equals(questGraphID, ignoreCase = true)) {
                return questGraph
            }
        }
        return null
    }

    fun doesQuestExist(questGraphID: String): Boolean {
        for (questGraph in _quests) {
            if (questGraph.questID.equals(questGraphID, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    var quests: Array<QuestGraph>
        get() = _quests
        set(quests) {
            this._quests = quests
            updateQuestsItemList()
        }

    fun updateQuestsItemList() {
        clearDialog()

        _listQuests.setItems(_quests)
        _listQuests.selectedIndex = -1
    }

    private fun clearDialog() {
        _listQuests.clearItems()
    }

    private fun populateQuestDialog(graph: QuestGraph) {
        _listTasks.clearItems()

        val tasks: ArrayList<QuestTask> = graph.getAllQuestTasks()

        _listTasks.setItems(*tasks.toTypedArray())
        _listTasks.selectedIndex = -1
    }

    fun initQuests(mapMgr: MapManager) {
        mapMgr.clearAllMapQuestEntities()

        //populate items if quests have them
        for (quest in _quests) {
            if (!quest.isQuestComplete) {
                quest.init(mapMgr)
            }
        }
    }

    fun updateQuests(mapMgr: MapManager) {
        for (quest in _quests) {
            if (!quest.isQuestComplete) {
                quest.update(mapMgr)
            }
        }
    }

    companion object {
        private val TAG = QuestUI::class.java.simpleName
        val RETURN_QUEST = "conversations/return_quest.json"
        val FINISHED_QUEST = "conversations/quest_finished.json"

    }

}
