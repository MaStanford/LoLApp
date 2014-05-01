package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class SkinDTO {

    int id;
    String name;
    int num;

    public SkinDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SkinDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", num=" + num +
                '}';
    }
}
