package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/26/14.
 */
public class ChampionIdDTO {

    private boolean botMmEnabled;
    private int     id;
    private boolean botEnabled;
    private boolean active;
    private boolean freeToPlay;
    private boolean rankedPlayEnabled;

    public ChampionIdDTO() {
    }

    public boolean isBotMmEnabled() {
        return botMmEnabled;
    }

    public void setBotMmEnabled(boolean botMmEnabled) {
        this.botMmEnabled = botMmEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBotEnabled() {
        return botEnabled;
    }

    public void setBotEnabled(boolean botEnabled) {
        this.botEnabled = botEnabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFreeToPlay() {
        return freeToPlay;
    }

    public void setFreeToPlay(boolean freeToPlay) {
        this.freeToPlay = freeToPlay;
    }

    public boolean isRankedPlayEnabled() {
        return rankedPlayEnabled;
    }

    public void setRankedPlayEnabled(boolean rankedPlayEnabled) {
        this.rankedPlayEnabled = rankedPlayEnabled;
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("NEW CHAMPION ID OBJECT \n");
        sb.append("botMmEnabled: " + botMmEnabled + "\n");
        sb.append("id: " + id + "\n");
        sb.append("botEnabled: " + botEnabled + "\n");
        sb.append("active: " + active + "\n");
        sb.append("freeToPlay: " + freeToPlay + "\n");
        sb.append("rankedPlayEnabled: " + rankedPlayEnabled + "\n");

        return sb.toString();
    }
}
