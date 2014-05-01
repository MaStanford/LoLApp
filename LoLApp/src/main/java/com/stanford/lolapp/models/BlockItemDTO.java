package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class BlockItemDTO {
    int count;
    int id;

    public BlockItemDTO() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BlockItemDTO{" +
                "count=" + count +
                ", id=" + id +
                '}';
    }
}
