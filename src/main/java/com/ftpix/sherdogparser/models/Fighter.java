package com.ftpix.sherdogparser.models;

import java.util.*;


public class Fighter extends SherdogBaseObject {

    private String nickname = "";
    private String height = "";
    private String weight = "";
    private Date birthday;
    private int wins = 0;
    private int winsKO = 0;
    private int winsSUB = 0;
    private int winsDEC = 0;
    private int winsOTHER = 0;
    private int losses = 0;
    private int loseKO = 0;
    private int loseSUB = 0;
    private int loseDEC = 0;
    private int loseOTHER = 0;
    private int draws = 0;
    private int nc = 0;
    private String picture = "";
    private List<Fight> fights = new ArrayList<>();


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getNc() {
        return nc;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Fight> getFights() {
        return fights;
    }

    public void setFights(List<Fight> fights) {
        this.fights = fights;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Fighter fighter = (Fighter) obj;
            return fighter.getSherdogUrl().equalsIgnoreCase(this.sherdogUrl);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Fighter{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", birthday=" + birthday +
                ", wins=" + wins +
                ", losses=" + losses +
                ", draws=" + draws +
                ", nc=" + nc +
                ", picture='" + picture + '\'' +
                ", sherdogUrl='" + sherdogUrl + '\'' +
                '}';
    }

    public int getWinsKO() {
        return winsKO;
    }

    public void setWinsKO(int winsKO) {
        this.winsKO = winsKO;
    }

    public int getWinsDEC() {
        return winsDEC;
    }

    public void setWinsDEC(int winsDEC) {
        this.winsDEC = winsDEC;
    }

    public int getWinsSUB() {
        return winsSUB;
    }

    public void setWinsSUB(int winsSUB) {
        this.winsSUB = winsSUB;
    }

    public int getLoseKO() {
        return loseKO;
    }

    public void setLoseKO(int loseKO) {
        this.loseKO = loseKO;
    }

    public int getLoseDEC() {
        return loseDEC;
    }

    public void setLoseDEC(int loseDEC) {
        this.loseDEC = loseDEC;
    }

    public int getLoseSUB() {
        return loseSUB;
    }

    public void setLoseSUB(int loseSUB) {
        this.loseSUB = loseSUB;
    }

    public int getWinsOTHER() {
        return winsOTHER;
    }

    public void setWinsOTHER(int winsOTHER) {
        this.winsOTHER = winsOTHER;
    }

    public int getLoseOTHER() {
        return loseOTHER;
    }

    public void setLoseOTHER(int loseOTHER) {
        this.loseOTHER = loseOTHER;
    }
}
