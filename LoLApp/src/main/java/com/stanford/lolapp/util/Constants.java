package com.stanford.lolapp.util;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.network.WebService;

/**
 * Created by Mark Stanford on 4/26/14.
 */
public class Constants {

    static final boolean isDebuggable = (0 !=
                (LoLApp.getApp().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    public static final boolean DEBUG = isDebuggable;

    //Default File location
    public static final String EXTERNAL_FILE_DIR        = "LoLApp";
    public static final String INTERNAL_FILE_DIR        = "LoLApp";
    public static final String FILE_NAME_USER           = "User.LoL";
    public static final String FILE_NAME_CHAMPIONS      = "Champions.LoL";
    public static final String FILE_NAME_CHAMPIONID     = "ChampionIDs.LoL";
    public static final String FILE_NAME_ITEMS          = "Items.LoL";

    //Shared Pref Keys
    public static final String KEY_SHARED_PREFS         = "com.stanford.lolapp.keySharedPrefs";
    public static final String KEY_LOCALE               = "com.stanford.lolapp.keyLocale";
    public static final String KEY_LOCATION             = "com.stanford.lolapp.keyLocation";

    //Default prefs
    public static final String DEFAULT_LOCALE           = WebService.locale.en_US.getLocale();
    public static final String DEFAULT_LOCATION         = WebService.location.na.getLocation();

    //Account Manager keys
    public static final String KEY_ACCOUNT_MANAGER      = "com.stanford.lolapp.keyAcccountManager";

    //File System Keys
    public static final String EXTERNAL_FILE_DIR_KEY    = "com.stanford.lolapp.externalFileDir";

    // Errors
    public static final String CREATE_OUT_DIR_ERROR     = "CREATE_OUT_DIR_ERROR";
    public static final String EXTERNAL_DIR_NOT_AVAIL   = "EXTERNAL_DIR_NOT_AVAIL";

    //Time to live for local persistence
    public static final long TIME_TO_LIVE_USER = 259200000; //3 days
    public static final long TIME_TO_LIVE_STATIC = 604800000;//7 days



    /**
     * Make Life a little easier for everyone
     */
    public static void DEBUG_LOG(String TAG, String message){
        if(DEBUG)
            Log.d(TAG,message);
    }
}
