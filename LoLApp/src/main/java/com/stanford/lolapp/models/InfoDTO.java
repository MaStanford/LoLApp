package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class InfoDTO {

    int attack;
    int defense;
    int difficulty;
    int magic;

    public InfoDTO() {
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    @Override
    public String toString() {
        return "InfoDTO{" +
                "attack=" + attack +
                ", defense=" + defense +
                ", difficulty=" + difficulty +
                ", magic=" + magic +
                '}';
    }
}
