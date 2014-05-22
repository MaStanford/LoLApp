package com.stanford.lolapp.network;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;

import org.json.JSONObject;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class ItemTask {

    private static LoLApp mContext;
    private static DataHash mDataHash;
    private static RequestQueue requestQueue;

    private ItemTask(){
        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();
        requestQueue = VolleyTask.getRequestQueue(mContext);
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final ItemTask INSTANCE = new ItemTask();
    }

    public static ItemTask getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Fetches all the items
     * @param params
     * @param body
     * @param responseListener
     * @param errorListener
     */
    public void fetchAllItems(Bundle params,JSONBody body, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        LoLAppWebserviceRequest request = new Requests.GetAllItems();

        WebService.makeRequest(requestQueue, request, params, body,responseListener,errorListener);
    }

    /**
     * Fetches a item by ID
     * Pass listeners into this.
     * @param id
     * @param responseListener
     * @param errorListener
     */
    public void fetchItemById(int id,Bundle params,JSONBody body, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        LoLAppWebserviceRequest request = new Requests.GetItem(id);

        WebService.makeRequest(requestQueue, request, params, body,responseListener,errorListener);
    }
}
