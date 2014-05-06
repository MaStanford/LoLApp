package com.stanford.lolapp.models;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Mark Stanford on 5/5/14.
 */
public class ItemBuild {

    private Map<Integer,String> items;

    public ItemBuild() {
    }

    public Map<Integer, String> getItems() {
        return items;
    }

    public void setItems(Map<Integer, String> items) {
        this.items = items;
    }

    public String getJSON(){
        Gson gson = new Gson();
        return gson.toJson(this.items);
    }

    @Override
    public String toString() {
        return "ItemBuild{" +
                "items=" + items +
                '}';
    }
}
