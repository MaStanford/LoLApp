package com.stanford.lolapp.models;

import java.util.List;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class ChampionSpellDTO {

    List<ImageDTO> altimages;
    List<Double> cooldown;
    String cooldownBurn;
    List<Integer> cost;
    String costBurn;
    String costType;
    String description;
    List<List<Double>> effect;	//This field is a List of List of Integer.
    List<String> effectBurn;
    ImageDTO image;
    String key;
    LevelTipDTO leveltip;
    int maxrank;
    String name;
    Object	range; //This field is either a List of Integer or the String 'self' for spells that target one's own champion.
    String rangeBurn;
    String resource;
    String sanitizedDescription;
    String sanitizedTooltip;
    String tooltip;
    List<SpellVarsDTO> vars;

    public List<ImageDTO> getAltimages() {
        return altimages;
    }

    public void setAltimages(List<ImageDTO> altimages) {
        this.altimages = altimages;
    }

    public List<Double> getCooldown() {
        return cooldown;
    }

    public void setCooldown(List<Double> cooldown) {
        this.cooldown = cooldown;
    }

    public String getCooldownBurn() {
        return cooldownBurn;
    }

    public void setCooldownBurn(String cooldownBurn) {
        this.cooldownBurn = cooldownBurn;
    }

    public List<Integer> getCost() {
        return cost;
    }

    public void setCost(List<Integer> cost) {
        this.cost = cost;
    }

    public String getCostBurn() {
        return costBurn;
    }

    public void setCostBurn(String costBurn) {
        this.costBurn = costBurn;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<List<Double>> getEffect() {
        return effect;
    }

    public void setEffect(List<List<Double>> effect) {
        this.effect = effect;
    }

    public List<String> getEffectBurn() {
        return effectBurn;
    }

    public void setEffectBurn(List<String> effectBurn) {
        this.effectBurn = effectBurn;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LevelTipDTO getLeveltip() {
        return leveltip;
    }

    public void setLeveltip(LevelTipDTO leveltip) {
        this.leveltip = leveltip;
    }

    public int getMaxrank() {
        return maxrank;
    }

    public void setMaxrank(int maxrank) {
        this.maxrank = maxrank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getRange() {
        return range;
    }

    public void setRange(Object range) {
        this.range = range;
    }

    public String getRangeBurn() {
        return rangeBurn;
    }

    public void setRangeBurn(String rangeBurn) {
        this.rangeBurn = rangeBurn;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSanitizedDescription() {
        return sanitizedDescription;
    }

    public void setSanitizedDescription(String sanitizedDescription) {
        this.sanitizedDescription = sanitizedDescription;
    }

    public String getSanitizedTooltip() {
        return sanitizedTooltip;
    }

    public void setSanitizedTooltip(String sanitizedTooltip) {
        this.sanitizedTooltip = sanitizedTooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public List<SpellVarsDTO> getVars() {
        return vars;
    }

    public void setVars(List<SpellVarsDTO> vars) {
        this.vars = vars;
    }

    @Override
    public String toString() {
        return "ChampionSpellDTO{" +
                "altimages=" + altimages +
                ", cooldown=" + cooldown +
                ", cooldownBurn='" + cooldownBurn + '\'' +
                ", cost=" + cost +
                ", costBurn='" + costBurn + '\'' +
                ", costType='" + costType + '\'' +
                ", description='" + description + '\'' +
                ", effect=" + effect +
                ", effectBurn=" + effectBurn +
                ", image=" + image +
                ", key='" + key + '\'' +
                ", leveltip=" + leveltip +
                ", maxrank=" + maxrank +
                ", name='" + name + '\'' +
                ", range=" + range +
                ", rangeBurn='" + rangeBurn + '\'' +
                ", resource='" + resource + '\'' +
                ", sanitizedDescription='" + sanitizedDescription + '\'' +
                ", sanitizedTooltip='" + sanitizedTooltip + '\'' +
                ", tooltip='" + tooltip + '\'' +
                ", vars=" + vars +
                '}';
    }
}
