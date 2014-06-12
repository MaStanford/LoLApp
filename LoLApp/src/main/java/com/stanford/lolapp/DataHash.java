package com.stanford.lolapp;

import android.util.Log;

import com.stanford.lolapp.exceptions.ChampionNotFoundException;
import com.stanford.lolapp.exceptions.ItemNotFoundException;
import com.stanford.lolapp.fragments.ChampionFragment;
import com.stanford.lolapp.models.ChampionDTO;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.models.ItemDTO;
import com.stanford.lolapp.models.ItemListDTO;
import com.stanford.lolapp.models.User;

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
    private static ChampionListDTO mChampionHash;
    //Hash of Item Objects
    private static ItemListDTO mItemHash;
    //Your user brodog
    private static User mUser;

    //Bools for checking state of data
    private boolean isChampsAllLoaded   = false;
    private boolean isItemsAllLoaded    = false;
    private boolean isChampIdsAllLoaded = false;

    private DataHash(){
        this.mChampionIdList    = null;
        this.mChampionHash      = null;
        this.mItemHash          = null;
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
        this.mChampionHash = championlist;
        this.isChampsAllLoaded = true;
        return this.mChampionHash;
    }

    public ChampionListDTO getChampionList(){
        return this.mChampionHash;
    }

    public boolean isChampListEmpty(){
        return (mChampionHash.getSize() > 0);
    }

    public ItemListDTO setItemList(ItemListDTO itemList){
        this.mItemHash = itemList;
        this.isItemsAllLoaded = true;
        return this.mItemHash;
    }

    public ItemListDTO getItemList(){
        return this.mItemHash;
    }

    public boolean isItemListEmpty(){
        return (mItemHash.getSize() > 0);
    }

    /**
     * Looks up the ID by positon in the list of IDs
     * The call to get list of IDs needs to have been completed by now
     * @param position
     * @return
     */
    public int getChampionIDbyPos(int position) throws ChampionNotFoundException {
        try {
            return mChampionIdList.getIdByPosition(position);
        }catch (ChampionNotFoundException e){
            Log.d(TAG,"Position not found in ID list");
            throw new ChampionNotFoundException("Champion is not in ID list.");
        }
    }

    /**
     * Puts a champion in the hash
     * @param champ
     * @return
     */
    public ChampionDTO setChampion(ChampionDTO champ){
        if(this.mChampionHash != null) {
            return this.mChampionHash.setChampion(champ);
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
            return this.mChampionHash.getChampionByKey(key);
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
        if(this.mChampionHash != null){
            try{
                return this.mChampionHash.getChampionByID(id);
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
        if(this.mChampionHash != null){
            try {
                return this.mChampionHash.getChampionByPosition(position);
            } catch (ChampionNotFoundException e) {
                return null;
            }
        }
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
            return mItemHash.getItemByPosition(position);
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
        if(mChampionHash == null)
            return 0;
        return mChampionHash.getSize();
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
        if(mItemHash == null)
            return 0;
        return mItemHash.getSize();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        DataHash.mUser = mUser;
    }

    public void deleteChampions(){
        this.mChampionHash =  null;
    }

    public void deleteChampionIDs(){
        this.mChampionIdList =  null;
    }

    public void deleteUser(){
        this.mUser =  null;
    }

    public void deleteItems(){
        this.mItemHash = null;
    }
}
