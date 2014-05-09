package com.stanford.lolapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

import com.stanford.lolapp.util.Constants;
import com.stanford.lolapp.util.FileSystemUtil;

import java.io.File;
import java.io.IOException;

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

        //Create the dir and check for errors

        FileSystemUtil.createInternalOutputDirectory();
        Constants.DEBUG_LOG(LOG_TAG, "Internal Created");
        try {
            FileSystemUtil.createExternalOutputDirectory();
            Constants.DEBUG_LOG(LOG_TAG, "External Created");
            Constants.DEBUG_LOG(LOG_TAG, "IsExternal? " + FileSystemUtil.isExternalStorageAvailable());
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        SharedPreferences prefs = getSharedPreferences(Constants.KEY_SHARED_PREFS, 0);
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
        SharedPreferences settings = getSharedPreferences(Constants.KEY_SHARED_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        // Commit the edits!
        editor.commit();
    }

    /**
     * Returns the external directory name from preferences.
     *
     * @return String of the output directory.
     * @author mstanford
     */
    public String getExternalFileDir(){
        SharedPreferences sharedprefs = getSharedPreferences(Constants.KEY_SHARED_PREFS,MODE_PRIVATE);
        return sharedprefs.getString(Constants.EXTERNAL_FILE_DIR_KEY, Constants.EXTERNAL_FILE_DIR);
    }


    /**
     * Returns the external output directory. It calls the getExternalFileDir and isExternalStorageAvailiable
     * If we can read and write to external then it returns the external file directory set in shared preferences
     * which getExternalFileDir retrieves.
     *
     * @return the output directory to the application that is used on the
     *         SDCARD
     * @throws IOException if the directory cannot be obtained or is not
     *             available.
     * @author mstanford
     */
    public File getExternalOutputDirectory() throws IOException {
        try{
            if (FileSystemUtil.isExternalStorageAvailable())
                return new File(Environment.getExternalStorageDirectory(),  getExternalFileDir());
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        throw new IOException(Constants.EXTERNAL_DIR_NOT_AVAIL);
    }

    /**
     * Returns the internal output directory
     *
     * @return the output directory to the application that is used internally
     *         by the app.
     * @author mstanford
     */
    public File getInternalOutputDirectory() {
        return new File(getFilesDir(), Constants.INTERNAL_FILE_DIR);
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