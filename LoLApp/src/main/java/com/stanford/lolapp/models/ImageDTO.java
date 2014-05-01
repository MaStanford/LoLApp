package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class ImageDTO {
    String full;
    String group;
    int h;
    String sprite;
    int w;
    int x;
    int y;

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "full='" + full + '\'' +
                ", group='" + group + '\'' +
                ", h=" + h +
                ", sprite='" + sprite + '\'' +
                ", w=" + w +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public String toString2(){
        StringBuilder sb = new StringBuilder();
        sb.append(full);
        sb.append(group);
        sb.append(h);
        sb.append(sprite);
        sb.append(w);
        sb.append(x);
        sb.append(y);
        return sb.toString();
    }
}
