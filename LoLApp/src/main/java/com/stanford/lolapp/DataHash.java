package com.stanford.lolapp;

import com.stanford.lolapp.models.ChampionDTO;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;

import java.util.List;

/**
 * Created by Mark Stanford on 4/26/14.
 */
public class DataHash {

    private static final String LOG_TAG = "DataHash";

    //List of ChampionIDs
    private static ChampionIDListDTO mChampionIdList;
    //Hash of Champion Objects
    private static ChampionListDTO mChampionHash;

    private DataHash(){
        this.mChampionIdList = new ChampionIDListDTO();
        this.mChampionHash = new ChampionListDTO();
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
        return this.mChampionIdList;
    }

    public ChampionIDListDTO getChampionIdList(){
        //TODO: error check null, if null run task to get list?
        return this.mChampionIdList;
    }

    public ChampionListDTO setChampionList(ChampionListDTO championlist){
        this.mChampionHash = championlist;
        return this.mChampionHash;
    }

    public ChampionListDTO getChampionList(){
        //TODO: error check null, if null run task to populate first part of list?
        return this.mChampionHash;
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
}
