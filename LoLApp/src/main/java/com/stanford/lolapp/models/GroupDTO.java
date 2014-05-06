package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class GroupDTO {

    	String MaxGroupOwnable;
    	String key;

    public GroupDTO() {
    }

    public String getMaxGroupOwnable() {
        return MaxGroupOwnable;
    }

    public void setMaxGroupOwnable(String maxGroupOwnable) {
        MaxGroupOwnable = maxGroupOwnable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "MaxGroupOwnable='" + MaxGroupOwnable + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
