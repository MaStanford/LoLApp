package com.stanford.lolapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.models.User;
import com.stanford.lolapp.network.ChampionTask;
import com.stanford.lolapp.network.JSONBody;
import com.stanford.lolapp.network.WebService;
import com.stanford.lolapp.util.Constants;
import com.stanford.lolapp.util.JSONSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * http://developer.android.com/guide/components/services.html
 * http://developer.android.com/training/run-background-service/create-service.html
 * <p/>
 * The service should handle network requests and local persistence from those calls.
 * That way the user opening and closing the app doesn't affect the network calls.
 * <p/>
 * The service should be the only class to access data, if an activity and the service access the data
 * the behavior is undefined on if they are accessing the same singleton.
 * <p/>
 * Activities should call on the service to check if there is local persistence for the data required,
 * if not then make a network call to generate that data.
 */
public class LoLAppService extends IntentService {

    //Intents
    public static final String INTENT_FETCH_DATA        = "com.stanford.lolapp.fetchData";
    public static final String INTENT_LOADING_DATA      = "com.stanford.lolapp.loadingData";
    public static final String INTENT_DATA_LOADED       = "com.stanford.lolapp.dataLoaded";
    public static final String INTENT_DATA_EXPIRED      = "com.stanford.lolapp.dataExpired";

    //Constants
    public static final long TIME_TO_LIVE_USER = 259200000; //3 days
    public static final long TIME_TO_LIVE_STATIC = 604800000;//7 days
    private final String TAG = "LoLAppService";

    //Fields
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder mBinder = new LocalBinder();

    //Bools for state of service
    private boolean mIsLoadingNetwork = false;
    private boolean mIsLoadingFileIO = false;
    private boolean mBound = false;

    //Singleton references
    private static LoLApp mContext;
    private static DataHash mDataHash;

    public LoLAppService() {
        super("LoLAppService");
    }

    /**
     * ***********************************************
     * *************Singleton Refs********************
     * ***********************************************
     */

    /**
     * Be careful, there is no guarantee that this ref will be the same for activities
     * @return
     */
    public LoLApp getContext(){
        return mContext;
    }

    /**
     * Be careful, there is no guarantee that this ref will be the same for activities
     * @return
     */
    public DataHash getDataHash(){
        return mDataHash;
    }


    /**
     * ***********************************************
     * *************NetWork Methods*******************
     * ***********************************************
     */

    /**
     * Fetches and stores a list of all the champions
     * Saves the list to local persistence.
     * Stores list to volatile memory in the dataHash Singleton
     * The app should call these with intents and startService.
     */
    private void getAllChampions(
            final RequestQueue queue,
            final WebService.WebserviceRequest serviceRequest,
            final Bundle requestParams,
            final JSONBody body,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        mIsLoadingNetwork = true;
        ChampionTask mChampionTask = ChampionTask.getInstance();

        //Use champion task, this is my lame and bad attempt at encapsulation
        mChampionTask.fetchAllChampion(requestParams,body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Parse the response
                        Gson gson = new Gson();
                        ChampionListDTO champ = gson.fromJson(response.toString(), ChampionListDTO.class);
                        //Set response to volatile memory
                        mDataHash.setChampionList(champ);
                        //Set response to persistent memory
                        saveChampionsFile(champ);
                        //Callback to activity
                        successListener.onResponse(response);
                        //Set loading to false
                        mIsLoadingNetwork = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        errorListener.onErrorResponse(error);
                        mIsLoadingNetwork = false;
                    }
                }
        );
    }

    /**
     * Fetches and stores all the championIDs
     * The app should call these with intents and startService.
     */
    private void getAllChampionIds(final Bundle requestParams,
                                  final JSONBody body,
                                  final Response.Listener<JSONObject> successListener,
                                  final Response.ErrorListener errorListener) {

        mIsLoadingNetwork = true;
        ChampionTask mChampionTask = ChampionTask.getInstance();

        mChampionTask.fetchAllChampionIDs(requestParams, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Parse the response
                        Gson gson = new Gson();
                        ChampionIDListDTO champList = gson.fromJson(response.toString(), ChampionIDListDTO.class);
                        //Set response to volatile memory
                        mDataHash.setChampionIdList(champList);
                        //Set response to persistent memory
                        saveChampionIdsFile(champList);
                        //Callback to activity
                        successListener.onResponse(response);
                        //set loading to false
                        mIsLoadingNetwork = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        errorListener.onErrorResponse(error);
                        mIsLoadingNetwork = false;
                    }
                }
        );
    }

    /**
     ************************************************
     **************File IO Methods*******************
     ************************************************
     */

    /**
     * Returns the name of the User File
     *
     * @return
     */
    public File getUserFileName() {
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_USER);
    }

    /**
     * Returns the name of the Champions file
     * @return
     */
    public File getChampionsFileName() {
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONS);
    }

    public File getChampionIdsFileName(){
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONID);
    }

    /**
     * Returns the date of the last time the User file was modified
     *
     * @return
     */
    public Long getUserFileDate() {
        if (isActiveUser()) {
            File mFile = getUserFileName();
            return mFile.lastModified();
        }
        return null;
    }

    /**
     * returns the date of last mod of champions file
     * @return
     */
    public Long getChampionsFileDate() {
        if (isChampionsSaved()) {
            File mFile = getChampionsFileName();
            return mFile.lastModified();
        }
        return null;
    }

    public Long getChampionsIdsFileDate(){
        if (isChampionIdsSaved()) {
            File mFile = getChampionIdsFileName();
            return mFile.lastModified();
        }
        return null;
    }

    /**
     * Returns a User object of the current active User
     * @param fileName
     * @return
     */
    public User getUserFile(File fileName) {
        if (isActiveUser()) {
            try {
                JSONObject jsonObject = JSONSerializer.loadJSONFile(fileName);
                User mUser = new User(jsonObject);
                return mUser;
            } catch (IOException e) {
                //Probably means that the User file doesn't exist
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                //Probably means we can't parse the file
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the championslist from file
     * @param fileName
     * @return
     */
    public ChampionListDTO getChampionsFile(File fileName) {
        if (isChampionsSaved()) {
            try {
                String json = JSONSerializer.loadStringFile(fileName);
                Gson gson = new Gson();
                ChampionListDTO champList = gson.fromJson(json,ChampionListDTO.class);
                mDataHash.setChampionList(champList);
                return champList;
            } catch (IOException e) {
                //Probably means that the Champ file doesn't exist
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public ChampionIDListDTO getChampionIdListFile(File fileName){
        if (isChampionIdsSaved()) {
            try {
                String json = JSONSerializer.loadStringFile(fileName);
                Gson gson = new Gson();
                ChampionIDListDTO champList = gson.fromJson(json,ChampionIDListDTO.class);
                mDataHash.setChampionIdList(champList);
                return champList;
            } catch (IOException e) {
                //Probably means that the Champ file doesn't exist
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Saves the user file.
     * @param user
     * @return
     */
    public boolean saveUserFile(User user) {
        try {
            JSONSerializer.saveJSON(user.toJSON(), getUserFileName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves Champions List to file
     * @param champs
     * @return
     */
    public boolean saveChampionsFile(ChampionListDTO champs){
        try {
            JSONSerializer.saveJSON(champs.toJSON(), getChampionsFileName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveChampionIdsFile(ChampionIDListDTO champs){
        try {
            JSONSerializer.saveJSON(champs.toJSON(), getChampionIdsFileName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks to see if there is an active user file.  When the user logs out, the file should be deleted.
     * @return
     */
    public boolean isActiveUser() {
        File mFile = getUserFileName();
        if (mFile.exists()) {
            if (isUserExpired()) {
                logOutUser();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks to see if there is a champions file
     * @return
     */
    public boolean isChampionsSaved(){
        File mFile = getChampionsFileName();
        if (mFile.exists()) {
            if (isChampionsExpired()) {
                deleteChampions();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isChampionIdsSaved(){
        File mFile = getChampionIdsFileName();
        if (mFile.exists()) {
            if (isChampionIdsExpired()) {
                deleteChampionIds();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Check if user file is expired
     *
     * @return
     */
    public boolean isUserExpired() {
        long userTime = getUserFileDate();
        Date now = new Date();
        if ((now.getTime() - userTime) > TIME_TO_LIVE_USER)
            return true; //User file is expired
        return false;
    }

    /**
     * Checks to see if the champions static data is expired
     * @return
     */
    public boolean isChampionsExpired() {
        long champTime = getChampionsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > TIME_TO_LIVE_STATIC)
            return true; //champ file is expired
        return false;
    }

    public boolean isChampionIdsExpired(){
        long champTime = getChampionsIdsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > TIME_TO_LIVE_STATIC)
            return true; //champ file is expired
        return false;
    }

    /**
     * Checks to see if there is an active user, and if there is, delete the file.
     */
    public void logOutUser() {
        if (isActiveUser()) {
            try {
                File file = getUserFileName();
                if (file.delete()) {
                    mDataHash.deleteUser();
                } else {
                    mDataHash.deleteUser();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes the championslist file
     */
    public void deleteChampions(){
        if (isChampionsSaved()) {
            try {
                File file = getChampionsFileName();
                if (file.delete()) {
                    mDataHash.deleteChampions();
                } else {
                    mDataHash.deleteChampions();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteChampionIds(){
        if (isChampionIdsSaved()) {
            try {
                File file = getChampionIdsFileName();
                if (file.delete()) {
                    mDataHash.deleteChampionIDs();
                } else {
                    mDataHash.deleteChampionIDs();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ***********************************************
     * *************Service Methods*******************
     * ***********************************************
     */
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_DEFAULT);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();
    }

    /**
     * Intent passing is good for notifications that don't have to be fast.
     * It can take an intent a couple hundred milliseconds to get from the service to the app.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent
        String dataString = intent.getDataString();
        // Do work here, based on the contents of dataString
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks to see if it can be destroyed
     *
     * @return
     */
    public boolean canDestroy() {
        if (!mIsLoadingFileIO && !mIsLoadingNetwork && !mBound)
            return true;
        return false;
    }

    /**
     ************************************************
     *****************Bind Methods*******************
     ************************************************
     */

    /**
     * When bound to activity
     * Binding is best when there is a constant data flow to an activity, otherwise use intents.
     *
     * @param arg0
     * @return
     */
    @Override
    public IBinder onBind(Intent arg0) {
        mBound = true;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mBound = true;
    }

    /**
     * Only called when all clients have unbound
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        mBound = false;
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public LoLAppService getService() {
            return LoLAppService.this;
        }
    }
}
