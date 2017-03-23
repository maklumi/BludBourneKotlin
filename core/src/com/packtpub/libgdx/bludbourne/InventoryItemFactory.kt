package com.packtpub.libgdx.bludbourne


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Scaling
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemTypeID
import com.packtpub.libgdx.bludbourne.UI.PlayerHUD
import java.util.*

class InventoryItemFactory private constructor() {

    private val _json = Json()
    private val INVENTORY_ITEM = "scripts/inventory_items.json"
    private val _inventoryItemList: Hashtable<ItemTypeID, InventoryItem>

    init {
        val list = _json.fromJson(ArrayList::class.java, Gdx.files.internal(INVENTORY_ITEM)) as ArrayList<JsonValue>
        _inventoryItemList = Hashtable<ItemTypeID, InventoryItem>()

        for (jsonVal in list) {
            val inventoryItem = _json.readValue(InventoryItem::class.java, jsonVal)
            _inventoryItemList.put(inventoryItem.itemTypeID!!, inventoryItem)
        }
    }

    fun getInventoryItem(inventoryItemType: ItemTypeID): InventoryItem {
        val item = InventoryItem(_inventoryItemList[inventoryItemType]!!)
        item.drawable = TextureRegionDrawable(PlayerHUD.itemsTextureAtlas.findRegion(item.itemTypeID!!.toString()))
        item.setScaling(Scaling.none)
        return item
    }

    companion object {
        private var _instance: InventoryItemFactory? = null

        val instance: InventoryItemFactory
            get() {
                if (_instance == null) {
                    _instance = InventoryItemFactory()
                }

                return _instance!!
            }
    }

    /*
    public void testAllItemLoad(){
        for(ItemTypeID itemTypeID : ItemTypeID.values()) {
            InventoryItem item = new InventoryItem(_inventoryItemList.get(itemTypeID));
            item.setDrawable(new TextureRegionDrawable(PlayerHUD.itemsTextureAtlas.findRegion(item.getItemTypeID().toString())));
            item.setScaling(Scaling.none);
        }
    }*/

}
