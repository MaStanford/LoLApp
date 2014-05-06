package com.stanford.lolapp.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark Stanford on 5/5/14.
 */
public class JSONBody {

    private Map<String, Object> requestBodyMap;

    public JSONBody() {
        this.requestBodyMap = new HashMap<String, Object>();
    }

    /**
     * Sets the request body
     * @param map
     * @return
     */
    public Map<String,Object> setMap(Map<String,Object> map){
        this.requestBodyMap =  map;
        return this.requestBodyMap;
    }

    /**
     * Returns the request body hash map
     * @return
     */
    public Map<String,Object> getMap(){
        return this.requestBodyMap;
    }

    /**
     * Adds a key value pair
     * @param key
     * @param value
     * @return
     */
    public JSONBody addTuple(String key, Object value){
        this.requestBodyMap.put(key, value);
        return this;
    }

    /**
     * Adds an object to the request body.
     * @param object
     * @return
     */
    public Map<String,Object> addObject(Object object){
        /*
         * http://stackoverflow.com/questions/2779251/convert-json-to-hashmap-using-gson-in-java
         * Type type = new TypeToken<Map<String, String>>(){}.getType();
         * Map<String, String> myMap = gson.fromJson("{'k1':'apple','k2':'orange'}", type);
         */
        Gson gson=new Gson();
        Type type = new TypeToken<Map<String,Object>>(){}.getType();
        String JSONString = gson.toJson(object);
        Map<String,Object> map = gson.fromJson(JSONString, type);
        this.requestBodyMap.putAll(map);

        Log.d("addObject",map.toString());
        return this.requestBodyMap;
    }

    /**
     * Returns the request value
     * @param key
     * @return
     */
    public Object getValue(String key){
        return this.requestBodyMap.get(key);
    }

    public String toString(){
        Gson gson = new Gson();
        String JSONString = gson.toJson(requestBodyMap);
        return JSONString;
    }

    public static byte[] toByteArray(String body){
        Log.d("toByteArray",body);
        return body.getBytes();
    }
}
