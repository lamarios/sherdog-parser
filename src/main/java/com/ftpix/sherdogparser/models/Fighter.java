package com.ftpix.sherdogparser.models;

import java.util.*;


public class Fighter extends SherdogBaseObject {

    private String nickname = "";
    private String height = "";
    private String weight = "";
    private Date birthday;
    private int wins = 0;
    private int losses = 0;
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
}
