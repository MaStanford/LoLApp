package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class MetaDataDTO {

    boolean isRune;
    String tier;
    String type;

    public MetaDataDTO() {
    }

    public boolean isRune() {
        return isRune;
    }

    public void setRune(boolean isRune) {
        this.isRune = isRune;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MetaDataDTO{" +
                "isRune=" + isRune +
                ", tier='" + tier + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
