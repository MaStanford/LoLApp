package com.stanford.lolapp.models;

import com.google.gson.Gson;
import com.stanford.lolapp.exceptions.ItemNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class ItemListDTO {

    BasicDataDTO basic;
    Map<String, ItemDTO> data;
    List<GroupDTO> groups;
    List<ItemTreeDTO> tree; //Seems to be the list we can use for listViews
    String type;
    String version;

    List<String> itemNames;

    public ItemListDTO() {
        itemNames =  new ArrayList<String>();
    }

    public BasicDataDTO getBasic() {
        return basic;
    }

    public void setBasic(BasicDataDTO basic) {
        this.basic = basic;
    }

    public Map<String, ItemDTO> getData() {
        return data;
    }

    public void setData(Map<String, ItemDTO> data) {
        this.data = data;
    }

    public List<GroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDTO> groups) {
        this.groups = groups;
    }

    public List<ItemTreeDTO> getTree() {
        return tree;
    }

    public void setTree(List<ItemTreeDTO> tree) {
        this.tree = tree;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns a single Item based on key
     * @param key
     * @return
     */
    public ItemDTO getItem(String key){
        return data.get(key);
    }

    public int getSize(){
        if(data != null)
            return data.size();
        return 0;
    }

    /**
     * returns the Item based on its location in the arraylist of names
     * @param position
     * @return
     * @throws ItemNotFoundException
     */
    public ItemDTO getItemByPosition(int position) throws ItemNotFoundException{
        if(data != null && position < data.size()){
            generateNameList();
            return data.get(itemNames.get(position));
        }
        throw new ItemNotFoundException("Item is not in list");
    }

    /**
     * Generates an arrayList of keys.
     */
    private void generateNameList(){
        itemNames.clear();
        for(String key: data.keySet()){
            itemNames.add(key);
        }
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String key: data.keySet()){
            sb.append(data.get(key).toString() + '\n');
        }
        return "ItemListDTO{" +
                "Size of ItemDTO=" + data.size() + '\n'+
                "basic=" + basic.toString() + '\n'+
                ", data=" + sb.toString() + '\n'+
                ", groups=" + groups.toString() + '\n'+
                ", tree=" + tree.toString() + '\n'+
                ", type='" + type + '\'' + '\n' +
                ", version='" + version + '\'' + '\n' +
                '}';
    }
}
