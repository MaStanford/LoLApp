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

import java.util.List;

/**
 * Created by Mark Stanford on 4/26/14.
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
    private boolean isChampsAllLoaded = false;
    private boolean isItemsAllLoaded = false;
    private boolean isChampIdsAllLoaded = false;

    private DataHash(){
        this.mChampionIdList = new ChampionIDListDTO();
        this.mChampionHash = new ChampionListDTO();
        this.mItemHash = new ItemListDTO();
        this.mUser =  new User();
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
    public int getChampionIDbyPos(int position){
        try {
            return mChampionIdList.getIdByPosition(position);
        }catch (ChampionNotFoundException e){
            Log.d(TAG,"Position not found in ID list");
            return -1;
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
        if(this.mChampionHash != null)
            return this.mChampionHash.getChampionByKey(key);
        return null;
    }

    /**
     * Returns the Champion based on the specified ID
     * @param id
     * @return
     */
    public ChampionDTO getChampionById(int id){
        if(this.mChampionHash != null)
            return this.mChampionHash.getChampionByID(id);
        return null;
    }

    /**
     * returns the champion by position in the list
     * @param position
     */
    public ChampionDTO getChampionByPos(int position) {
        if(this.mChampionHash != null)
            return this.mChampionHash.getChampionByPosition(position);
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
        return mChampionHash.getSize();
    }

    /**
     * Returns the size of the item data hash
     * @return
     */
    public int sizeOfItemList(){
        return mItemHash.getSize();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        DataHash.mUser = mUser;
    }

    public void deleteChampions(){
        this.mChampionHash =  new ChampionListDTO();
    }

    public void deleteChampionIDs(){
        this.mChampionIdList =  new ChampionIDListDTO();
    }

    public void deleteUser(){
        this.mUser =  null;
    }

    public void deleteItems(){
        this.mItemHash =  new ItemListDTO();
    }
}
