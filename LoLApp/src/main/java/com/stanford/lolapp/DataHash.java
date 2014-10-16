package com.stanford.lolapp;

import android.util.Log;

import com.stanford.lolapp.exceptions.ChampionNotFoundException;
import com.stanford.lolapp.exceptions.ItemNotFoundException;
import com.stanford.lolapp.models.ChampionDTO;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.models.ItemDTO;
import com.stanford.lolapp.models.ItemListDTO;
import com.stanford.lolapp.models.User;
import com.stanford.lolapp.persistence.JSONFileUtil;
import com.stanford.lolapp.util.Constants;

import java.util.List;

/**
 * Created by Mark Stanford on 4/26/14.
 *
 * TODO: make getters throw exceptions if not found, none of this null checking BS
 */
public class DataHash {

    private static final String TAG = "DataHash";

    //List of ChampionIDs
    private static ChampionIDListDTO mChampionIdList;
    //Hash of Champion Objects
    private static ChampionListDTO mChampionList;
    //Hash of Item Objects
    private static ItemListDTO mItemList;
    //Your user brodog
    private static User mUser;

    //Bools for checking state of data
    private boolean isChampsAllLoaded   = false;
    private boolean isItemsAllLoaded    = false;
    private boolean isChampIdsAllLoaded = false;

    private DataHash(){
        this.mChampionIdList    = null;
        this.mChampionList      = null;
        this.mItemList          = null;
        this.mUser              = null;
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final DataHash INSTANCE = new DataHash();
    }

    public static DataHash getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ChampionIDListDTO setChampionIdList(ChampionIDListDTO champioIDlist){
        this.mChampionIdList = champioIDlist;
        this.isChampIdsAllLoaded = true;
        return this.mChampionIdList;
    }

    public ChampionIDListDTO getChampionIdList(){
        return this.mChampionIdList;
    }

    public boolean isChampIDListEmpty(){
        return (mChampionIdList.getSize() > 0);
    }

    public ChampionListDTO setChampionList(ChampionListDTO championlist){
        this.mChampionList = championlist;
        this.isChampsAllLoaded = true;
        return this.mChampionList;
    }

    public ChampionListDTO getChampionList(){
        return this.mChampionList;
    }

    public boolean isChampListEmpty(){
        return (mChampionList.getSize() > 0);
    }

    public ItemListDTO setItemList(ItemListDTO itemList){
        this.mItemList = itemList;
        this.isItemsAllLoaded = true;
        return this.mItemList;
    }

    public ItemListDTO getItemList(){
        return this.mItemList;
    }

    public boolean isItemListEmpty(){
        return (mItemList.getSize() > 0);
    }

    /**
     * Checks if the local persistence or volatile memory contains the User Object
     * @return
     */
    public boolean isUserAvailible(){
        JSONFileUtil mFileUtil = JSONFileUtil.getInstance();
        if(mFileUtil.isUserSaved() && getUser() != null){ //User is local and volatile
            return true;
        } else if(mFileUtil.isUserSaved()){  //User is only local, set the volatile
            setUser(mFileUtil.getUserFromFile(mFileUtil.getUserFileName()));
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
        JSONFileUtil mFileUtil = JSONFileUtil.getInstance();

        if(getChampionList() != null){ //Champs is local and volatile
            Constants.DEBUG_LOG(TAG, "ChampionListAvailable: Local");
            return true;
        } else if(mFileUtil.isChampionsSaved()){  //User is only local, set the volatile
            setChampionList(mFileUtil.getChampionsFromFile(mFileUtil.getChampionsFileName()));
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
        JSONFileUtil mFileUtil = JSONFileUtil.getInstance();

        if(getChampionIdList() != null){ //IDs are local and volatile
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: Local");
            return true;
        } else if(mFileUtil.isChampionIdsSaved()){  //IDs are only local
            Constants.DEBUG_LOG(TAG,"ChampionIdListAvailable: persistence");
            setChampionIdList(mFileUtil.getChampionIdsFromFile(mFileUtil.getChampionIdsFileName()));
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
        JSONFileUtil mFileUtil = JSONFileUtil.getInstance();

        if(mFileUtil.isItemsSaved() && getItemList().getSize() > 0){ //IDs are local and volatile
            return true;
        } else if(mFileUtil.isChampionIdsSaved()){  //IDs are only local
            setChampionIdList(mFileUtil.getChampionIdsFromFile(mFileUtil.getChampionIdsFileName()));
            return true;
        } else { //items are not available, I do not see a case where it is volatile and not local
            return false;
        }
    }

    /**
     * Looks up the ID by positon in the list of IDs
     * The call to get list of IDs needs to have been completed by now
     * @param position
     * @return
     */
    public int getChampionIDbyPos(int position){
        try {
            return mChampionIdList.getIdByPosition(position);
        }catch (ChampionNotFoundException e){
            Log.d(TAG,"Position not found in ID list");
            return 0;
        }
    }

    /**
     * Puts a champion in the hash
     * @param champ
     * @return
     */
    public ChampionDTO setChampion(ChampionDTO champ){
        if(this.mChampionList != null) {
            return this.mChampionList.setChampion(champ);
        }
        return null;
    }

    /**
     * Returns the champion from the specified key
     * Returns null if it does not exist
     * @param key
     * @return
     */
    public ChampionDTO getChampionByKey(String key){
        try{
            return this.mChampionList.getChampionByKey(key);
        }catch (ChampionNotFoundException e){
            return null;
        }
    }

    /**
     * Returns the Champion based on the specified ID
     * @param id
     * @return
     */
    public ChampionDTO getChampionById(int id){
        if(this.mChampionList != null){
            try{
                return this.mChampionList.getChampionByID(id);
            }catch (ChampionNotFoundException e){
                return null;
            }
        }
        return null;
    }

    /**
     * returns the champion by position in the list
     * @param position
     */
    public ChampionDTO getChampionByPos(int position) {
        if(this.mChampionList != null){
            try {
                return this.mChampionList.getChampionByPosition(position);
            } catch (ChampionNotFoundException e) {
                Constants.DEBUG_LOG(TAG, "getChampionByPos Cannot find champion!");
                return null;
            }
        }
        Constants.DEBUG_LOG(TAG, "getChampionByPos List is null!");
        return null;
    }
    /**
     * Gets Item by position
     * This needs to be called
     * @param position
     * @return
     */
    public ItemDTO getItemByPos(int position){
        try {
            return mItemList.getItemByPosition(position);
        }catch (ItemNotFoundException e){
            Log.d(TAG,"Position not found in ID list");
            return null;
        }
    }


    /**
     * Appends a list of champs to the HashMap
     * @param champs
     */
    public void appendChampions(List<ChampionDTO> champs){
        for(ChampionDTO champ : champs){
            setChampion(champ);
        }
    }

    /**
     * returns the size of the champion data hash
     * @return
     */
    public int sizeOfChampionList(){
        if(mChampionList == null)
            return 0;
        return mChampionList.getSize();
    }

    /**
     * returns the size of the champion data hash
     * @return
     */
    public int sizeOfChampionIDList(){
        if(mChampionIdList == null)
            return 0;
        return mChampionIdList.getSize();
    }

    /**
     * Returns the size of the item data hash
     * @return
     */
    public int sizeOfItemList(){
        if(mItemList == null)
            return 0;
        return mItemList.getSize();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        DataHash.mUser = mUser;
    }

    public void deleteChampions(){
        this.mChampionList =  null;
    }

    public void deleteChampionIDs(){
        this.mChampionIdList =  null;
    }

    public void deleteUser(){
        this.mUser =  null;
    }

    public void deleteItems(){
        this.mItemList = null;
    }
}
