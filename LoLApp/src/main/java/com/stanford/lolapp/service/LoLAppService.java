package com.stanford.lolapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.stanford.lolapp.R;
import com.stanford.lolapp.activities.DataActivity;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.models.ItemListDTO;
import com.stanford.lolapp.models.User;
import com.stanford.lolapp.network.ChampionTask;
import com.stanford.lolapp.network.ItemTask;
import com.stanford.lolapp.network.JSONBody;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;
import com.stanford.lolapp.network.WebserviceRequest;
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
 * http://stackoverflow.com/questions/15524280/service-vs-intent-service
 * <p/>
 * The service should handle network requests and local persistence from those calls.
 * That way the user opening and closing the app doesn't affect the network calls.
 * <p/>
 * The service should be the only class to access data from the singleton, if an activity and the service access the data
 * the behavior is undefined on if they are accessing the same singleton.
 * <p/>
 * Activities should call on the service to check if there is local persistence for the data required,
 * if not then make a network call to generate that data.
 *
 * I chose to go with Service instead of IntentService because I may not want the service
 * stopped when there is no intents, when there is a bound client.  This may not be possible with
 * IntentService.
 */
public class LoLAppService extends Service {

    private LoLAppService mService;

    //Actions for network calls, put bundle in intent also
    public static final String ACTION_FETCH_CHAMPIONS               = "com.stanford.lolapp.actionFetchChampions";
    public static final String ACTION_FETCH_CHAMPIONIDS             = "com.stanford.lolapp.actionFetchIDs";
    public static final String ACTION_FETCH_ITEMS                   = "com.stanford.lolapp.actionFetchItems";
    public static final String ACTION_FETCH_GAMES                   = "com.stanford.lolapp.actionFetchGames";
    public static final String ACTION_FETCH_MASTERYRUNES            = "com.stanford.lolapp.actionFetchMasteryRunes";

    public static final String EXTRA_BUNDLE                         = "com.stanford.lolapp.extraBundle";

    //Actions that fetches are complete.  Bind to service and grab data.
    public static final String ACTION_FETCH_CHAMPIONS_DONE              = "com.stanford.lolapp.actionFetchChampionsDone";
    public static final String ACTION_FETCH_CHAMPIONIDS_DONE            = "com.stanford.lolapp.actionFetchIDsDone";
    public static final String ACTION_FETCH_ITEMS_DONE                  = "com.stanford.lolapp.actionFetchItemsDone";
    public static final String ACTION_FETCH_GAMES_DONE                  = "com.stanford.lolapp.actionFetchGamesDone";
    public static final String ACTION_FETCH_MASTERYRUNES_DONE           = "com.stanford.lolapp.actionFetchMasteryRunesDone";
    public static final String ACTION_FETCH_CHAMPIONS_DONE_FAIL         = "com.stanford.lolapp.actionFetchChampionsDone";
    public static final String ACTION_FETCH_CHAMPIONIDS_DONE_FAIL       = "com.stanford.lolapp.actionFetchIDsDone";
    public static final String ACTION_FETCH_ITEMS_DONE_FAIL             = "com.stanford.lolapp.actionFetchItemsDone";
    public static final String ACTION_FETCH_GAMES_DONE_FAIL             = "com.stanford.lolapp.actionFetchGamesDone";
    public static final String ACTION_FETCH_MASTERYRUNES_DONE_FAIL      = "com.stanford.lolapp.actionFetchMasteryRunesDone";

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
    NotificationManager mNotificationManager;

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
     * Checks if the local persistence or volatile memory contains the User Object
     * @return
     */
    public boolean isUserAvailible(){
        if(isUserSaved() && mDataHash.getUser() != null){ //User is local and volatile
            return true;
        } else if(isUserSaved()){  //User is only local, set the volatile
            mDataHash.setUser(getUserFromFile(getUserFileName()));
            return true;
        } else { //User is not active, I do not see a case where it is volatile and not local
            return false;
        }
    }

    /**
     * checks if the local persistence or volatile memory contains the the ChampionList
     * @return
     */
    public boolean isChampionListAvailible(){
        if(mDataHash.getChampionList() != null){ //Champs is local and volatile
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: Local");
            return true;
        } else if(isChampionsSaved()){  //User is only local, set the volatile
            mDataHash.setChampionList(getChampionsFromFile(getChampionsFileName()));
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: Persistent");
            return true;
        } else { //champs is not available, I do not see a case where it is volatile and not local
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: false");
            return false;
        }
    }

    /**
     * Check is the local persistence or volatile memory contains the championIDlist
     * @return
     */
    public boolean isChampionIdListAvailible(){
        if(isChampionIdsSaved() && mDataHash.getChampionIdList().getSize() > 0){ //IDs are local and volatile
            return true;
        } else if(isChampionIdsSaved()){  //IDs are only local
            mDataHash.setChampionIdList(getChampionIdsFromFile(getChampionIdsFileName()));
            return true;
        } else { //IDs are not available, I do not see a case where it is volatile and not local
            return false;
        }
    }

    /**
     * Check is the local persistence or volatile memory contains the championIDlist
     * @return
     */
    public boolean isItemsAvailible(){
        if(isItemsSaved() && mDataHash.getItemList().getSize() > 0){ //IDs are local and volatile
            return true;
        } else if(isChampionIdsSaved()){  //IDs are only local
            mDataHash.setChampionIdList(getChampionIdsFromFile(getChampionIdsFileName()));
            return true;
        } else { //items are not available, I do not see a case where it is volatile and not local
            return false;
        }
    }

    /**
     * ***********************************************
     * *************NetWork Methods*******************
     * ***********************************************
     * There is support for network calls to be done with startCommand and have a broadcast when completed.
     * There is also support for network calls with a  bound service and passing listeners.
     */

    /**
     * Called from intent handler.
     *
     * Pass the bundle along with the intent
     */
    private void fetchChampionsBackground(Bundle params, final int startID){
        fetchAllChampions(params,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mNotificationManager.cancel(R.string.service_data_fetch_champ);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_CHAMPIONS_DONE);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mNotificationManager.cancel(R.string.service_data_fetch_champ);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_CHAMPIONS_DONE_FAIL);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                }
        );
    }

    /**
     * Fetches and stores a list of all the champions
     * Saves the list to local persistence.
     * Stores list to volatile memory in the dataHash Singleton
     * The app should call these with intents and startService.
     */
    public void fetchAllChampions(
            final Bundle requestParams,
            final JSONBody body,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        mIsLoadingNetwork = true;
        ChampionTask mChampionTask = ChampionTask.getInstance();

        //Use champion task, this is my lame and bad attempt at encapsulation
        mChampionTask.fetchAllChampion(requestParams, body,
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
     * Called from intent handler.
     * Pass the params as a bundle in the intent
     * @param params
     */
    private void fetchChamionIdsBackground(Bundle params, final int startID){
        fetchAllChampionIds(params,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        stopNotification(R.string.service_data_fetch_id);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_CHAMPIONIDS_DONE);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopNotification(R.string.service_data_fetch_id);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_CHAMPIONS_DONE_FAIL);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                }
        );
    }

    /**
     * Fetches and stores all the championIDs
     * The app should call these with intents and startService.
     */
    public void fetchAllChampionIds(final Bundle requestParams,
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
                        if(successListener != null)
                            successListener.onResponse(response);
                        //set loading to false
                        mIsLoadingNetwork = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        if(errorListener != null)
                            errorListener.onErrorResponse(error);
                        mIsLoadingNetwork = false;
                    }
                }
        );
    }

    /**
     * Called from intent handler.
     *
     * Pass the bundle along with the intent
     */
    private void fetchItemsBackground(Bundle params, final int startID){
        fetchAllItems(params,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mNotificationManager.cancel(R.string.service_data_fetch_champ);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_ITEMS_DONE);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mNotificationManager.cancel(R.string.service_data_fetch_champ);
                        Intent mIntent = new Intent();
                        mIntent.setAction(ACTION_FETCH_ITEMS_DONE_FAIL);
                        sendBroadcast(mIntent);
                        //Check if we stop service
                        if(canDestroy()){
                            stopSelf(startID);
                        }
                    }
                }
        );
    }
    public void fetchAllItems(final Bundle requestParams,
                              final JSONBody body,
                              final Response.Listener<JSONObject> successListener,
                              final Response.ErrorListener errorListener) {

        mIsLoadingNetwork = true;
        ItemTask mItemTask = ItemTask.getInstance();

        mItemTask.fetchAllItems(requestParams,body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Parse the response
                        Gson gson = new Gson();
                        ItemListDTO itemList = gson.fromJson(response.toString(), ItemListDTO.class);
                        //Set response to volatile memory
                        mDataHash.setItemList(itemList);
                        //Set response to persistent memory
                        saveItemsFile(itemList);
                        //Callback to activity
                        if(successListener != null)
                            successListener.onResponse(response);
                        //set loading to false
                        mIsLoadingNetwork = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        if(errorListener != null)
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
    private File getUserFileName() {
        Constants.DEBUG_LOG(TAG,"User file: " + LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_USER);
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_USER);
    }

    /**
     * Returns the name of the Champions file
     * @return
     */
    private File getChampionsFileName() {
        Constants.DEBUG_LOG(TAG,"Champions file: " + LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONS);
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONS);
    }

    /**
     * Gets the filename of championIDs file
     * @return
     */
    private File getChampionIdsFileName(){
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONID);
    }

    /**
     * Returns the filename of the Items file
     * @return
     */
    private File getItemFileName(){
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_ITEMS);
    }

    /**
     * Returns the date of the last time the User file was modified
     *
     * @return
     */
    private Long getUserFileDate() {
        File mFile = getUserFileName();
        return mFile.lastModified();
    }

    /**
     * returns the date of last mod of champions file
     * @return
     */
    private Long getChampionsFileDate() {
        File mFile = getChampionsFileName();
        return mFile.lastModified();
    }

    /**
     * Return the last modified date of the champion Id file
     * @return
     */
    private Long getChampionsIdsFileDate(){
        File mFile = getChampionIdsFileName();
        return mFile.lastModified();
    }

    /**
     * Returns the last modified date of the items file
     * @return
     */
    private Long getItemsFileDate(){
        File mFile = getItemFileName();
        return mFile.lastModified();
    }

    /**
     * Returns a User object of the current active User
     * @param fileName
     * @return
     */
    private User getUserFromFile(File fileName) {
        try {
            JSONObject jsonObject = JSONSerializer.loadJSONFile(fileName);
            User mUser = new User(jsonObject);
            mDataHash.setUser(mUser);
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

    /**
     * Returns the championslist from file
     * @param fileName
     * @return
     */
    private ChampionListDTO getChampionsFromFile(File fileName) {
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

    /**
     * returns the championIDlist from file and sets it to volatile
     * @param fileName
     * @return
     */
    private ChampionIDListDTO getChampionIdsFromFile(File fileName){
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

    /**
     * Returns the items from saved file and sets to volatile
     * @param filename
     * @return
     */
    private ItemListDTO getItemsFromFile(File filename){
        try {
            String json = JSONSerializer.loadStringFile(filename);
            Gson gson = new Gson();
            ItemListDTO itemList = gson.fromJson(json,ItemListDTO.class);
            mDataHash.setItemList(itemList);
            return itemList;
        } catch (IOException e) {
            //Probably means that the Champ file doesn't exist
            e.printStackTrace();
            return null;
        }
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
     * Saves the parameter in the Items File
     * @param items
     * @return
     */
    public boolean saveItemsFile(ItemListDTO items){
        try {
            JSONSerializer.saveJSON(items.toJSON(), getItemFileName());
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
    private boolean isUserSaved() {
        File mFile = getUserFileName();
        Constants.DEBUG_LOG(TAG,"isUserSaved: " + mFile.exists());
        if (mFile.exists()) {
            if (isUserFileExpired()) {
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
    private boolean isChampionsSaved(){
        File mFile = getChampionsFileName();
        Constants.DEBUG_LOG(TAG,"isChampionsSaved: " + mFile.exists());
        if (mFile.exists()) {
            if (isChampionsFileExpired()) {
                deleteChampions();
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isChampionIdsSaved(){
        File mFile = getChampionIdsFileName();
        if (mFile.exists()) {
            if (isChampionIdsFileExpired()) {
                deleteChampionIds();
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isItemsSaved(){
        File mFile = getItemFileName();
        if (mFile.exists()) {
            if (isItemsFileExpired()) {
                deletItemsFile();
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
    private boolean isUserFileExpired() {
        long userTime = getUserFileDate();
        Date now = new Date();
        if ((now.getTime() - userTime) > TIME_TO_LIVE_USER) {
            Constants.DEBUG_LOG(TAG, "isUserFileExpired: " + true);
            return true; //User file is expired
        }
        Constants.DEBUG_LOG(TAG,"isUserFileExpired: " + false);
        return false;
    }

    /**
     * Checks to see if the champions static data is expired
     * @return
     */
    private boolean isChampionsFileExpired() {
        long champTime = getChampionsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > TIME_TO_LIVE_STATIC) {
            Constants.DEBUG_LOG(TAG, "isChampionFileExpired: " + true);
            return true; //champ file is expired
        }
        Constants.DEBUG_LOG(TAG,"isChampionFileExpired: " + false);
        return false;
    }

    /**
     * Checks if the championIDs file is expired.
     * @return
     */
    private boolean isChampionIdsFileExpired(){
        long champTime = getChampionsIdsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > TIME_TO_LIVE_STATIC)
            return true; //champ file is expired
        return false;
    }

    /**
     * Checks if the items file is expired
     * @return
     */
    private boolean isItemsFileExpired(){
        long itemTime = getItemsFileDate();
        Date now = new Date();
        if ((now.getTime() - itemTime) > TIME_TO_LIVE_STATIC)
            return true; //champ file is expired
        return false;
    }

    /**
     * Checks to see if there is an active user, and if there is, delete the file.
     */
    public void logOutUser() {
        try {
            File file = getUserFileName();
            if (file.delete()) {//I don't see why this would ever be false
                mDataHash.deleteUser();
            } else {
                mDataHash.deleteUser();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the championslist file
     */
    private void deleteChampions(){
        try {
            File file = getChampionsFileName();
            if (file.delete()) {//I don't see why this would ever be false
                mDataHash.deleteChampions();
            } else {
                mDataHash.deleteChampions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the IDs list
     */
    private void deleteChampionIds(){
        if (isChampionIdsSaved()) {
            try {
                File file = getChampionIdsFileName();
                if (file.delete()) {//I don't see why this would ever be false
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
     * Deletes the items file
     */
    private void deletItemsFile(){
        if (isItemsSaved()) {
            try {
                File file = getItemFileName();
                if (file.delete()) { //I don't see why this would ever be false
                    mDataHash.deleteItems();
                } else {
                    mDataHash.deleteItems();
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
            /*
             *  I actually don't need to have a pipeline thread here since volley handles all
             *  the concurrency stuff.  This actually serves no purpose except to get me practiced in
             *  writing the boiler plate for concurrent operations in Android.  Fun fact:
             *  Android Activities have a Looper/pipeline thread handle all the UI interactions and
             *  when you grab a handler in an activity it implicitly binds to the looper :D
             */
            Log.d(TAG,"HandleMessage called. ID: " + msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_DEFAULT);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * onStartCommand overrides the bindservice lifecycle
     * http://developer.android.com/reference/android/content/Context.html#startService(android.content.Intent)
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand called.  ID: " + startId);

        //Set the first arg as the startID
        //http://developer.android.com/guide/components/services.html#Stopping
        //This doesn't actually do anything, I just wanted to practice the boiler plate.
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        Bundle params = intent.getBundleExtra(EXTRA_BUNDLE);
        //Check action to see what to do now
        switch(intent.getAction()){
            case(ACTION_FETCH_CHAMPIONIDS):
                showNotification(R.string.service_data_fetch_id);
                fetchChamionIdsBackground(params, startId);
                break;
            case(ACTION_FETCH_CHAMPIONS):
                showNotification(R.string.service_data_fetch_champ);
                fetchChampionsBackground(params, startId);
                break;
            case(ACTION_FETCH_GAMES):
                showNotification(R.string.service_data_fetch_games);
                break;
            case(ACTION_FETCH_ITEMS):
                showNotification(R.string.service_data_fetch_item);
                fetchItemsBackground(params, startId);
                break;
            case(ACTION_FETCH_MASTERYRUNES):
                showNotification(R.string.service_data_fetch_runes);
                break;
        }

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
     *****************Note Methods*******************
     ************************************************
     */

    public void showNotification(int msg) {

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(msg);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DataActivity.class), PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(getText(R.string.service_data_fetch_label))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setProgress(100,0,true)
                .build();

        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNotificationManager.notify(msg, notification);
    }

    /**
     * Cancels the notification with this specific ID
     * We are using the Views ID for notification ID
     *
     * @param msg
     */
    public void stopNotification(int msg){
        mNotificationManager.cancel(msg);
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
        if(canDestroy()){
            this.stopSelf();
        }
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public LoLAppService getService() {
            return LoLAppService.this;
        }
    }
}
