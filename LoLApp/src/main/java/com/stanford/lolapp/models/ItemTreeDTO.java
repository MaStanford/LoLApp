package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class ItemTreeDTO {

    	String header;
    	List<String> tags;

    public ItemTreeDTO() {
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ItemTreeDTO{" +
                "header='" + header + '\'' +
                ", tags=" + tags.toString() +
                '}';
    }
}
