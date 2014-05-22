package com.stanford.lolapp.network;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;

import org.json.JSONObject;

/**
 * Created by Mark Stanford on 4/29/14.
 * <p/>
 * Use this calls to make network calls when local persistence or data structures do not contain the
 * required data.
 * <p/>
 */
public class ChampionTask {

    private static LoLApp mContext;
    private static DataHash mDataHash;
    private static RequestQueue requestQueue;

    private ChampionTask() {
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
     * Fetches champion by ID.  Response is in listener
     * @param id
     * @param params
     * @param body
     * @param responseListener
     * @param errorListener
     */
    public void fetchChampionById(int id,
                                  Bundle params,
                                  JSONBody body,
                                  Response.Listener<JSONObject> responseListener,
                                  Response.ErrorListener errorListener) {
        LoLAppWebserviceRequest request = new Requests.GetChampionData(id);
        WebService.makeRequest(requestQueue, request, params, body, responseListener, errorListener);
    }


    /**
     * Creates a Champion request for each position in the range
     * @param min
     * @param max
     * @param params
     * @param body
     * @param responseListener
     * @param errorListener
     */
    public void fetchChampionRangeByPosition(int min, int max,
                                             Bundle params,
                                             JSONBody body,
                                             Response.Listener<JSONObject> responseListener,
                                             Response.ErrorListener errorListener) {
        for (int i = min; i < max; i++) {
            LoLAppWebserviceRequest request = new Requests.GetChampionData(mDataHash.getChampionIDbyPos(i));
            WebService.makeRequest(requestQueue, request, params, body, responseListener, errorListener);
        }
    }

    /**
     * Fetches all the Champions.  Response is in listener
     * @param requestParams
     * @param body
     * @param successListener
     * @param errorListener
     */
    public void fetchAllChampion(final Bundle requestParams,
                                 final JSONBody body,
                                 final Response.Listener<JSONObject> successListener,
                                 final Response.ErrorListener errorListener) {
        LoLAppWebserviceRequest request = new Requests.GetAllChampionData();
        WebService.makeRequest(requestQueue, request, requestParams, body, successListener, errorListener);
    }

    /**
     * Fetches all the champion IDs.  Passes response to listener
     * @param requestParams
     * @param body
     * @param successListener
     * @param errorListener
     */
    public void fetchAllChampionIDs(final Bundle requestParams,
                                    final JSONBody body,
                                    final Response.Listener<JSONObject> successListener,
                                    final Response.ErrorListener errorListener) {
        LoLAppWebserviceRequest request = new Requests.GetAllChampionIds();
        WebService.makeRequest(requestQueue, request, requestParams, body, successListener, errorListener);
    }
}
