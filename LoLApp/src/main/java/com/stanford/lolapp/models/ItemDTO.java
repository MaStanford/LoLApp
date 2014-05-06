package com.stanford.lolapp.models;

import com.stanford.lolapp.network.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public class ItemDTO {

    String colloq               = "";
    boolean consumeOnFull       = false;
    boolean consumed            = false;
    int depth                   = 0;
    String description          = "";
    List<String> from           = new ArrayList<String>();
    GoldDTO gold                = new GoldDTO();
    String group                = "";
    boolean hideFromAll         = false;
    int id                      = 0;
    ImageDTO image              = new ImageDTO();
    boolean inStore             = false;
    List<String> into           = new ArrayList<String>();
    Map<String, Boolean> maps   = new HashMap<String, Boolean>();
    String name                 = "";
    String plaintext            = "";
    String requiredChampion     = "";
    MetaDataDTO rune            = new MetaDataDTO();
    String sanitizedDescription = "";
    int specialRecipe           = 0;
    int stacks                  = 0;
    BasicDataStatsDTO stats     = new BasicDataStatsDTO();
    List<String> tags           = new ArrayList<String>();

    public ItemDTO() {
    }

    public String getColloq() {
        return colloq;
    }

    public void setColloq(String colloq) {
        this.colloq = colloq;
    }

    public boolean isConsumeOnFull() {
        return consumeOnFull;
    }

    public void setConsumeOnFull(boolean consumeOnFull) {
        this.consumeOnFull = consumeOnFull;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

    public GoldDTO getGold() {
        return gold;
    }

    public void setGold(GoldDTO gold) {
        this.gold = gold;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isHideFromAll() {
        return hideFromAll;
    }

    public void setHideFromAll(boolean hideFromAll) {
        this.hideFromAll = hideFromAll;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }

    public boolean isInStore() {
        return inStore;
    }

    public void setInStore(boolean inStore) {
        this.inStore = inStore;
    }

    public List<String> getInto() {
        return into;
    }

    public void setInto(List<String> into) {
        this.into = into;
    }

    public Map<String, Boolean> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Boolean> maps) {
        this.maps = maps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }

    public String getRequiredChampion() {
        return requiredChampion;
    }

    public void setRequiredChampion(String requiredChampion) {
        this.requiredChampion = requiredChampion;
    }

    public MetaDataDTO getRune() {
        return rune;
    }

    public void setRune(MetaDataDTO rune) {
        this.rune = rune;
    }

    public String getSanitizedDescription() {
        return sanitizedDescription;
    }

    public void setSanitizedDescription(String sanitizedDescription) {
        this.sanitizedDescription = sanitizedDescription;
    }

    public int getSpecialRecipe() {
        return specialRecipe;
    }

    public void setSpecialRecipe(int specialRecipe) {
        this.specialRecipe = specialRecipe;
    }

    public int getStacks() {
        return stacks;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }

    public BasicDataStatsDTO getStats() {
        return stats;
    }

    public void setStats(BasicDataStatsDTO stats) {
        this.stats = stats;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * returns the image url as a string.  It grabs the URL from the webservice and the imageDTO
     * @return
     */
    public String getImageURL(){
        return WebService.ITEM_ICONS + image.getFull();
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "colloq='" + colloq + '\n' +
                ", consumeOnFull=" + consumeOnFull + '\n' +
                ", consumed=" + consumed + '\n' +
                ", depth=" + depth + '\n' +
                ", description='" + description +  '\n' +
                ", from=" + from + '\n' +
                ", gold=" + gold + '\n' +
                ", group='" + group +  '\n' +
                ", hideFromAll=" + hideFromAll + '\n' +
                ", id=" + id + '\n' +
                ", image=" + image + '\n' +
                ", inStore=" + inStore + '\n' +
                ", into=" + into + '\n' +
                ", maps=" + maps + '\n' +
                ", name='" + name +  '\n' +
                ", plaintext='" + plaintext +  '\n' +
                ", requiredChampion='" + requiredChampion +  '\n' +
                ", rune=" + rune + '\n' +
                ", sanitizedDescription='" + sanitizedDescription +  '\n' +
                ", specialRecipe=" + specialRecipe + '\n' +
                ", stacks=" + stacks + '\n' +
                ", stats=" + stats + '\n' +
                ", tags=" + tags + '\n' +
                '}';
    }
}
