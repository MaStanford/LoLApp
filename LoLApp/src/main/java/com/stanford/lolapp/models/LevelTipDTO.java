package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class LevelTipDTO {

    List<String> effect;
    List<String> label;

    public LevelTipDTO() {
    }

    public List<String> getEffect() {
        return effect;
    }

    public void setEffect(List<String> effect) {
        this.effect = effect;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "LevelTipDTO{" +
                "effect=" + effect +
                ", label=" + label +
                '}';
    }
}
