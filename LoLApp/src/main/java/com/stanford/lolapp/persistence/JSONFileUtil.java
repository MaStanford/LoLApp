package com.stanford.lolapp.persistence;

import com.google.gson.Gson;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.models.ItemListDTO;
import com.stanford.lolapp.models.User;
import com.stanford.lolapp.util.Constants;
import com.stanford.lolapp.util.JSONSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Mark Stanford on 6/9/14.
 */
public class JSONFileUtil {

    public final String TAG = "JSONFileUtil";
    private DataHash mDataHash;
    private LoLApp mContext;

    private JSONFileUtil(){
        mDataHash = DataHash.getInstance();
        mContext = LoLApp.getApp();
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final JSONFileUtil INSTANCE = new JSONFileUtil();
    }

    /**
     * Use this to get a reference to the singleton
     * @return
     */
    public static JSONFileUtil getInstance() {
        return SingletonHolder.INSTANCE;
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
     * TODO: Find a way to make sure the whole thing is downloaded or not.
     * @return
     */
    public boolean isChampionListAvailible(){
        if(mDataHash.getChampionList() != null){ //Champs is local and volatile
            Constants.DEBUG_LOG(TAG, "ChampionListAvailable: Local");
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
     * TODO: Find a way to make sure the whole thing is downloaded or not.
     * @return
     */
    public boolean isChampionIdListAvailible(){
        if(mDataHash.getChampionIdList() != null){ //IDs are local and volatile
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: Local");
            return true;
        } else if(isChampionIdsSaved()){  //IDs are only local
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: persistence");
            mDataHash.setChampionIdList(getChampionIdsFromFile(getChampionIdsFileName()));
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
     * Returns the name of the User File
     *
     * @return
     */
    public File getUserFileName() {
        Constants.DEBUG_LOG(TAG,"User file: " + LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_USER);
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_USER);
    }

    /**
     * Returns the name of the Champions file
     * @return
     */
    public File getChampionsFileName() {
        Constants.DEBUG_LOG(TAG,"Champions file: " + LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONS);
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONS);
    }

    /**
     * Gets the filename of championIDs file
     * @return
     */
    public File getChampionIdsFileName(){
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_CHAMPIONID);
    }

    /**
     * Returns the filename of the Items file
     * @return
     */
    public File getItemFileName(){
        return new File(LoLApp.getApp().getInternalOutputDirectory()
                + Constants.FILE_NAME_ITEMS);
    }

    /**
     * Returns the date of the last time the User file was modified
     *
     * @return
     */
    public Long getUserFileDate() {
        File mFile = getUserFileName();
        return mFile.lastModified();
    }

    /**
     * returns the date of last mod of champions file
     * @return
     */
    public Long getChampionsFileDate() {
        File mFile = getChampionsFileName();
        return mFile.lastModified();
    }

    /**
     * Return the last modified date of the champion Id file
     * @return
     */
    public Long getChampionsIdsFileDate(){
        File mFile = getChampionIdsFileName();
        return mFile.lastModified();
    }

    /**
     * Returns the last modified date of the items file
     * @return
     */
    public Long getItemsFileDate(){
        File mFile = getItemFileName();
        return mFile.lastModified();
    }

    /**
     * Returns a User object of the current active User
     * @param fileName
     * @return
     */
    public User getUserFromFile(File fileName) {
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
    public ChampionListDTO getChampionsFromFile(File fileName) {
        try {
            String json = JSONSerializer.loadStringFile(fileName);
            Gson gson = new Gson();
            ChampionListDTO champList = ChampionListDTO.fromJSON(json);
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
    public ChampionIDListDTO getChampionIdsFromFile(File fileName){
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
    public ItemListDTO getItemsFromFile(File filename){
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
    public boolean isUserSaved() {
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
    public boolean isChampionsSaved(){
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

    public boolean isChampionIdsSaved(){
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

    public boolean isItemsSaved(){
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
    public boolean isUserFileExpired() {
        long userTime = getUserFileDate();
        Date now = new Date();
        if ((now.getTime() - userTime) > Constants.TIME_TO_LIVE_USER) {
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
    public boolean isChampionsFileExpired() {
        long champTime = getChampionsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > Constants.TIME_TO_LIVE_STATIC) {
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
    public boolean isChampionIdsFileExpired(){
        long champTime = getChampionsIdsFileDate();
        Date now = new Date();
        if ((now.getTime() - champTime) > Constants.TIME_TO_LIVE_STATIC)
            return true; //champ file is expired
        return false;
    }

    /**
     * Checks if the items file is expired
     * @return
     */
    public boolean isItemsFileExpired(){
        long itemTime = getItemsFileDate();
        Date now = new Date();
        if ((now.getTime() - itemTime) > Constants.TIME_TO_LIVE_STATIC)
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
     * When data is bad, and need a restart
     */
    public void deleteData(){
        deleteChampions();
        deleteChampionIds();
        deletItemsFile();
    }

    /**
     * Deletes the championslist file
     */
    public void deleteChampions(){
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
    public void deleteChampionIds(){
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

    /**
     * Deletes the items file
     */
    public void deletItemsFile(){
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
