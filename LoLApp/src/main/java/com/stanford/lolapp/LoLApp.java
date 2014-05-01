package com.stanford.lolapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.util.Consts;

/**
 * Created by Mark Stanford on 4/26/14.
 */
public class LoLApp extends Application {
    private final String LOG_TAG = "LoLApp";

    private static LoLApp mInstance;
    private static DataHash mDataHash;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mDataHash = DataHash.getInstance();
    }

    public static LoLApp getApp() {
        try {
            if (mInstance == null)
                throw new IllegalStateException("Application Context Not Created!");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return mInstance;
    }

    public DataHash getDataHash(){
        if(mDataHash == null)
            mDataHash = DataHash.getInstance();
        return mDataHash;
    }

    /**
     * Returns the shared preference.
     * Useful to keep with the app context since this is the only context
     * that is guaranteed to be a singleton
     *
     * @param key
     * @param defValue
     * @return
     * @author Mark Stanford
     * @since Build 1
     */
    public String getPreference(String key, String defValue) {
        SharedPreferences prefs = getSharedPreferences(Consts.KEY_SHARED_PREFS, 0);
        return prefs.getString(key, (String) defValue);
    }

    /**
     * Sets a shared preference.
     * Useful to keep with the app context since this is the only context
     * that is guaranteed to be a singleton.
     *
     * @param key
     * @param value
     * @author mStanford
     * @since Build 1
     */
    public void setPreference(String key, String value) {
        SharedPreferences settings = getSharedPreferences(Consts.KEY_SHARED_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        // Commit the edits!
        editor.commit();
    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}