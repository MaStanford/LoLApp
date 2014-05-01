package com.stanford.lolapp.models;

import com.stanford.lolapp.network.WebService;

import java.util.Map;
import java.util.List;

/**
 * Created by Mark Stanford on 4/21/14.
 */
public class ChampionDTO {

    List<String> allytips                       = null;
    private String blurb                        = null;
    private List<String> enemytips              = null;
    private int id                              = 0;
    private ImageDTO image                      = null;
    private InfoDTO info                        = null;
    private String key                          = null;
    private String lore                         = null;
    private String name                         = null;
    private String partype                      = null;
    private PassiveDTO passive                  = null;
    private List<RecommendedDTO> recommended    = null;
    private List<SkinDTO>skins                  = null;
    private List<ChampionSpellDTO> spells       = null;
    private StatsDTO stats                      = null;
    private List<String> tags                   = null;
    private String title                        = null;

    public ChampionDTO(){

    }

    public List<String> getAllytips() {
        return allytips;
    }

    public void setAllytips(List<String> allytips) {
        this.allytips = allytips;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public List<String> getEnemytips() {
        return enemytips;
    }

    public void setEnemytips(List<String> enemytips) {
        this.enemytips = enemytips;
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

    public InfoDTO getInfo() {
        return info;
    }

    public void setInfo(InfoDTO info) {
        this.info = info;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartype() {
        return partype;
    }

    public void setPartype(String partype) {
        this.partype = partype;
    }

    public PassiveDTO getPassive() {
        return passive;
    }

    public void setPassive(PassiveDTO passive) {
        this.passive = passive;
    }

    public List<RecommendedDTO> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<RecommendedDTO> recommended) {
        this.recommended = recommended;
    }

    public List<SkinDTO> getSkins() {
        return skins;
    }

    public void setSkins(List<SkinDTO> skins) {
        this.skins = skins;
    }

    public List<ChampionSpellDTO> getSpells() {
        return spells;
    }

    public void setSpells(List<ChampionSpellDTO> spells) {
        this.spells = spells;
    }

    public StatsDTO getStats() {
        return stats;
    }

    public void setStats(StatsDTO stats) {
        this.stats = stats;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTag(){
        StringBuilder sb = new StringBuilder();
        for(String tag : tags){
            sb.append(tag + '\n');
        }
        return sb.toString();
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL(){
        return WebService.CHAMPIONS_ICONS + image.getFull();
    }
    /**
     * toString for the whole champion
     * @return
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(allytips.toString() + "\n");
        sb.append(blurb + "\n");
        sb.append(enemytips.toString() + "\n");
        sb.append(id + "\n");
        sb.append(image.toString() + "\n");
        sb.append(info.toString() + "\n");
        sb.append(key + "\n");
        sb.append(lore + "\n");
        sb.append(name + "\n");
        sb.append(partype + "\n");
        sb.append(passive.toString() + "\n");
        sb.append(recommended.toString() + "\n");
        sb.append(skins.toString() + "\n");
        sb.append(spells.toString() + "\n");
        sb.append(stats.toString() + "\n");
        sb.append(tags.toString() + "\n");
        sb.append(title.toString() + "\n");
        return sb.toString();
    }

    private String convertArrayToString(String... string){
        StringBuilder sb = new StringBuilder();
        for(String mString: string){
            sb.append(mString + "\n" );
        }
        return sb.toString();
    }
}
