package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class GoldDTO {

    int base;
    boolean purchasable;
    int sell;
    int total;

    public GoldDTO() {
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public int getSell() {
        return sell;
    }

    public void setSell(int sell) {
        this.sell = sell;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "GoldDTO{" +
                "base=" + base +
                ", purchasable=" + purchasable +
                ", sell=" + sell +
                ", total=" + total +
                '}';
    }
}
