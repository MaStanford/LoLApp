package com.stanford.lolapp.network;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Stanford on 4/29/14.
 *
 * Use this calls to make network calls when local persistence or data structures do not contain the
 * required data.
 *
 * TODO: Decide if this is a good design or if there should be a service used for all RPCs
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

    /**
     * Fetches a champion by ID
     * Pass listeners into this.
     * @param id
     * @param responseListener
     * @param errorListener
     */
    public void fetchChampionById(int id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        WebService.LoLAppWebserviceRequest request = new WebService.GetChampionData(id);
        //TODO: Grab the bundle parameters from the shared prefs.
        Bundle params = new Bundle();
        params.putString(WebService.PARAM_REQUIRED_LOCATION,WebService.location.na.getLocation());
        params.putString(WebService.PARAM_REQUIRED_LOCALE,WebService.locale.en_US.getLocale());
        params.putString(WebService.GetChampionData.PARAM_DATA,WebService.ChampData.all.getData());
        WebService.makeRequest(mContext, requestQueue, request, params, null,responseListener,errorListener);
    }

    /**
     * Fetches a list of champions by position in the ID list, which is used as the order
     * for list views.
     *
     * @param min
     * @param max
     * @param responseListener
     * @param errorListener
     */
    public void fetchChampionRangeByPosition(int min, int max, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        for(int i = min; i < max; i++){
            WebService.LoLAppWebserviceRequest request = new WebService.GetChampionData(mDataHash.getChampionIDbyPos(i));
            //TODO: Grab the bundle parameters from the shared prefs.
            Bundle params = new Bundle();
            params.putString(WebService.PARAM_REQUIRED_LOCATION,WebService.location.na.getLocation());
            params.putString(WebService.PARAM_REQUIRED_LOCALE,WebService.locale.en_US.getLocale());
            params.putString(WebService.GetChampionData.PARAM_DATA, WebService.ChampData.all.getData());
            WebService.makeRequest(mContext, requestQueue, request, params, null, responseListener,errorListener);
        }
    }
}
