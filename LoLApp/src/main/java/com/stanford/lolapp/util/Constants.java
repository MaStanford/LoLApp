package com.stanford.lolapp.util;

import android.util.Log;

/**
 * Created by Mark Stanford on 4/26/14.
 */
public class Constants {

    public static final boolean DEBUG = true;

    //Default File location
    public static final String EXTERNAL_FILE_DIR        = "LoLApp";
    public static final String INTERNAL_FILE_DIR        = "LoLApp";
    public static final String FILE_NAME_USER           = "User.LoL";
    public static final String FILE_NAME_CHAMPIONS      = "Champions.LoL";
    public static final String FILE_NAME_CHAMPIONID     = "ChampionIDs.LoL";
    public static final String FILE_NAME_ITEMS          = "Items.LoL";

    //Shared Pref Keys
    public static final String KEY_SHARED_PREFS         = "com.stanford.lolapp.keySharedPrefs";

    //Account Manager keys
    public static final String KEY_ACCOUNT_MANAGER      = "com.stanford.lolapp.keyAcccountManager";

    //File System Keys
    public static final String EXTERNAL_FILE_DIR_KEY    = "com.stanford.lolapp.externalFileDir";

    // Errors
    public static final String CREATE_OUT_DIR_ERROR     = "CREATE_OUT_DIR_ERROR";
    public static final String EXTERNAL_DIR_NOT_AVAIL   = "EXTERNAL_DIR_NOT_AVAIL";


    /**
     * Make Life a little easier for everyone
     */
    public static void DEBUG_LOG(String TAG, String message){
        if(DEBUG)
            Log.d(TAG,message);
    }
}
