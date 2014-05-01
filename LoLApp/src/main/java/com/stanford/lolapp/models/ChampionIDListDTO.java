package com.stanford.lolapp.models;

import com.stanford.lolapp.ChampionNotFoundException;

import java.util.List;

/**
 * Created by Mark Stanford on 4/27/14.
 */
public class ChampionIDListDTO {

    private static final String LOG_TAG = "ChampionIDList";
    //Cannot be static.  For some reason GSON doesn't work on static fields.
    //TODO: Figure out why reflection fails on static fields
    private List<ChampionIdDTO> champions;

    /**
     * Returns the array of ChampionID objects
     * @return
     */
    public List<ChampionIdDTO> getList(){
        if(champions != null)
            return champions;
        return null;
    }

    /**
     * Returns a single element in the array of ChampionID objects
     * @param index
     * @return
     */
    public ChampionIdDTO getItem(int index){
        if(champions.size() > 0 && index < champions.size())
            return champions.get(index);
        return null;
    }

    public int getIdByPosition(int position) throws ChampionNotFoundException {
        if(champions != null && position  <= champions.size())
            return champions.get(position).getId();
        throw new ChampionNotFoundException("Cannot find Champion ID by Position");
    }

    /**
     * Returns a String of all the ChampionID objects toString
     * @return
     */
    public String toString(){
        if(champions != null && champions.size() > 0){
            StringBuilder sb = new StringBuilder();
            for(ChampionIdDTO championID : champions){
                sb.append(championID.toString());
            }
            return sb.toString();
        }
        return String.valueOf(champions.size());
    }
}
