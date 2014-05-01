package com.stanford.lolapp.network;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.models.ChampionIDListDTO;

import org.json.JSONObject;

/**
 * Created by Mark Stanford on 4/29/14.
 */
public class ChampionTask {

    private static LoLApp mContext;
    private static DataHash mDataHash;
    private static RequestQueue requestQueue;

    private ChampionTask(){
        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();
        requestQueue = VolleyTask.getRequestQueue(mContext);
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final ChampionTask INSTANCE = new ChampionTask();
    }

    public static ChampionTask getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void fetchChampionById(int id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){

        WebService.LoLAppWebserviceRequest request = new WebService.GetStaticChampion(id);

        //TODO: Grab the bundle parameters from the shared prefs.
        Bundle params = new Bundle();
        params.putString(WebService.PARAM_REQUIRED_LOCATION,WebService.location.na.getLocation());
        params.putString(WebService.PARAM_REQUIRED_LOCALE,WebService.locale.en_US.getLocale());
        params.putString(WebService.GetStaticChampion.PARAM_DATA,"all");

        WebService.makeRequest(mContext, requestQueue, request, params,responseListener,errorListener);
    }
}
