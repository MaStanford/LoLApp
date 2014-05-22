package com.stanford.lolapp.network;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.models.User;

import org.json.JSONObject;

/**
 * Created by Mark Stanford on 5/5/14.
 */
public class UserTask {

    private static LoLApp mContext;
    private static RequestQueue requestQueue;

    private UserTask(){
        mContext = LoLApp.getApp();
        requestQueue = VolleyTask.getRequestQueue(mContext);
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final UserTask INSTANCE = new UserTask();
    }

    public static UserTask getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Logs in and passes the user object to the listener
     * Stores the Auth Token in the AccountManager
     *
     * @param params username:"" password:""
     * @param responseListener
     * @param errorListener
     */
    public void loginWithUserPassword(Bundle params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        LoLAppWebserviceRequest request = new Requests.Login();
        WebService.makeRequest(requestQueue, request, params, null, responseListener,errorListener);
    }

    /**
     * Creates a new user.
     * Stores the account in the AccountManager
     *
     * @param params
     * @param responseListener
     * @param errorListener
     */
    public void createUser(Bundle params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        LoLAppWebserviceRequest request = new Requests.CreateUser();
        WebService.makeRequest(requestQueue, request, params, null, responseListener,errorListener);
    }

    public boolean isUserValid(){
        return true;
    }

    public User getCurrentUser(){
        return new User();
    }
}
