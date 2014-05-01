package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class RecommendedDTO {

    List<BlockDTO> blocks;
    String champion;
    String map;
    String mode;
    boolean priority;
    String title;
    String type;

    public RecommendedDTO() {
    }

    public List<BlockDTO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockDTO> blocks) {
        this.blocks = blocks;
    }

    public String getChampion() {
        return champion;
    }

    public void setChampion(String champion) {
        this.champion = champion;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RecommendedDTO{" +
                "blocks=" + blocks +
                ", champion='" + champion + '\'' +
                ", map='" + map + '\'' +
                ", mode='" + mode + '\'' +
                ", priority=" + priority +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}


