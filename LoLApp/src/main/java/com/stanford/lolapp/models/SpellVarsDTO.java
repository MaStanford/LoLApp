package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class SpellVarsDTO {

    List<Double> coeff;
    String dyn;
    String key;
    String link;
    String ranksWith;

    public SpellVarsDTO() {
    }

    public List<Double> getCoeff() {
        return coeff;
    }

    public void setCoeff(List<Double> coeff) {
        this.coeff = coeff;
    }

    public String getDyn() {
        return dyn;
    }

    public void setDyn(String dyn) {
        this.dyn = dyn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRanksWith() {
        return ranksWith;
    }

    public void setRanksWith(String ranksWith) {
        this.ranksWith = ranksWith;
    }

    @Override
    public String toString() {
        return "SpellVarsDTO{" +
                "coeff=" + coeff +
                ", dyn='" + dyn + '\'' +
                ", key='" + key + '\'' +
                ", link='" + link + '\'' +
                ", ranksWith='" + ranksWith + '\'' +
                '}';
    }
}
