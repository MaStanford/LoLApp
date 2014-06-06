package com.stanford.lolapp.models;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.stanford.lolapp.LoLApp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class ChampionListDTO {

    public static final boolean PARAM_DATA_BY_ID = false;

    //dataById flag in query sets this as mapped to ID, otherwise it maps to name
    Map<String, ChampionDTO> data;
    String format;
    //Mapped Champion ID to Champion Name
    Map<String, String> keys;
    String type;
    String version;

    public ChampionListDTO(){
        data =  new HashMap<String, ChampionDTO>();
        keys = new HashMap<String, String>();
    }

    public Map<String, ChampionDTO> getData() {
        return data;
    }

    public void setData(Map<String, ChampionDTO> data) {
        this.data = data;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, String> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
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
     * Sets a champion into the list
     * @param champion
     * @return Champion
     */
    public ChampionDTO setChampion(ChampionDTO champion){
        //Check if data map is by ID or Name
        if(PARAM_DATA_BY_ID){
            return data.put(String.valueOf(champion.getId()),champion);
        }else{
            return data.put(champion.getKey(),champion);
        }
    }

    /**
     * Returns the champion based on the ID
     * @param id
     * @return Champion
     */
    public ChampionDTO getChampionByID(int id){
        String key = keys.get(String.valueOf(id));
        return data.get(key);
    }

    /**
     * Returns a champion based on the champion name(key)
     * @param name
     * @return
     */
    public ChampionDTO getChampionByKey(String name){
        //Check if data map is by ID or Name
        if(PARAM_DATA_BY_ID){
            //If it is by key then we need to iterate through the map and match the key(name)
            for (String key : data.keySet()) {
                if(data.get(key).getName().equals(name))
                    return data.get(key);
            }
        }else{
            return data.get(name);
        }
        return null;
    }

    /**
     * Returns the champion based on the position in the ChampionIDlist
     * @param position
     * @return
     */
    public ChampionDTO getChampionByPosition(int position){
        //Get the ID of the champion based on the position in the list of IDDTOs
        int id;
        if(LoLApp.getApp().getDataHash().getChampionIdList().getItem(position) != null) {
            id = LoLApp.getApp().getDataHash().getChampionIdList().getItem(position).getId();
        }else{
            return null;
        }
        return getChampionByID(id);
    }

    public int getSize(){
        if(data == null)
            return 0;
        return data.size();
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ChampionListDTO fromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,ChampionListDTO.class);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (String key : data.keySet()) {
            sb.append(data.get(key).toString());
        }


        for (String key : keys.keySet()) {
            sb.append(keys.get(key).toString());
        }

        return "\nChampionListDTO{" + "\n" +
                "data size=" + data.size() + "\n" +
                "data=" + sb.toString() + "\n" +
                "format='" + format + '\'' +"\n" +
                "keys=" + keys.toString() + "\n" +
                "type='" + type + '\'' + "\n" +
                "version='" + version + '\n' +
                '}';
    }
}
