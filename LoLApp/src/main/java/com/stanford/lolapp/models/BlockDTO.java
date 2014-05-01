package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class BlockDTO {

    List<BlockItemDTO> items;
    boolean recMath;
    String type;

    public BlockDTO() {
    }

    public List<BlockItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BlockItemDTO> items) {
        this.items = items;
    }

    public boolean isRecMath() {
        return recMath;
    }

    public void setRecMath(boolean recMath) {
        this.recMath = recMath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BlockDTO{" +
                "items=" + items +
                ", recMath=" + recMath +
                ", type='" + type + '\'' +
                '}';
    }
}
