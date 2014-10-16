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
import com.stanford.lolapp.network.ChampionTask;
import com.stanford.lolapp.network.ItemTask;
import com.stanford.lolapp.network.JSONBody;
import com.stanford.lolapp.persistence.JSONFileUtil;
import com.stanford.lolapp.util.Constants;

import org.json.JSONObject;

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
    private static JSONFileUtil mFileUtil;
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

    public JSONFileUtil getFileUtil(){
        return mFileUtil;
    }

    /**
     * Checks if the local persistence or volatile memory contains the User Object
     * @return
     */
    public boolean isUserAvailible(){
        if(mFileUtil.isUserSaved() && mDataHash.getUser() != null){ //User is local and volatile
            return true;
        } else if(mFileUtil.isUserSaved()){  //User is only local, set the volatile
            mDataHash.setUser(mFileUtil.getUserFromFile(mFileUtil.getUserFileName()));
            return true;
        } else { //User is not active, I do not see a case where it is volatile and not local
            return false;
        }
    }

    /**
     * checks if the local persistence or volatile memory contains the the ChampionList
     * TODO: Find a way to make sure the whole thing is downloaded or not.
     * @return
     */
    public boolean isChampionListAvailible(){
        if(mDataHash.getChampionList() != null){ //Champs is local and volatile
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: Local");
            return true;
        } else if(mFileUtil.isChampionsSaved()){  //User is only local, set the volatile
            mDataHash.setChampionList(mFileUtil.getChampionsFromFile(mFileUtil.getChampionsFileName()));
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: Persistent");
            return true;
        } else { //champs is not available, I do not see a case where it is volatile and not local
            Constants.DEBUG_LOG(TAG,"ChampionListAvailable: false");
            return false;
        }
    }

    /**
     * Check is the local persistence or volatile memory contains the championIDlist
     * TODO: Find a way to make sure the whole thing is downloaded or not.
     * @return
     */
    public boolean isChampionIdListAvailible(){
        if(mDataHash.getChampionIdList() != null){ //IDs are local and volatile
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: Local");
            return true;
        } else if(mFileUtil.isChampionIdsSaved()){  //IDs are only local
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: persistence");
            mDataHash.setChampionIdList(mFileUtil.getChampionIdsFromFile(mFileUtil.getChampionIdsFileName()));
            return true;
        } else { //IDs are not available, I do not see a case where it is volatile and not local
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: False");
            return false;
        }
    }

    /**
     * Check is the local persistence or volatile memory contains the championIDlist
     * TODO: Find a way to make sure the whole thing is downloaded or not.
     * @return
     */
    public boolean isItemsAvailible(){
        if(mFileUtil.isItemsSaved() && mDataHash.getItemList().getSize() > 0){ //IDs are local and volatile
            return true;
        } else if(mFileUtil.isChampionIdsSaved()){  //IDs are only local
            mDataHash.setChampionIdList(mFileUtil.getChampionIdsFromFile(mFileUtil.getChampionIdsFileName()));
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
        Constants.DEBUG_LOG(TAG, "fetchChamionIdsBackground : \n" + params.toString());
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
        mChampionTask.fetchAllChampions(requestParams, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Parse the response
                        Gson gson = new Gson();
                        ChampionListDTO champ = gson.fromJson(response.toString(), ChampionListDTO.class);
                        //Set response to volatile memory
                        mDataHash.setChampionList(champ);
                        //Set response to persistent memory
                        mFileUtil.saveChampionsFile(champ);
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
        Constants.DEBUG_LOG(TAG, "fetchChamionIdsBackground : \n" + params.toString());
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
                        mFileUtil.saveChampionIdsFile(champList);
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
                        mFileUtil.saveItemsFile(itemList);
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
        mFileUtil = JSONFileUtil.getInstance();

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

        //Make sure that the caller sets the proper bundle
        Bundle params = intent.getExtras();
        //TODO: make a default params here in case someone forgets to put them


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
