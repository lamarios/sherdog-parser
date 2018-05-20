package com.ftpix.sherdogparser.models;

import java.time.ZonedDateTime;

public class Fight {


    private SherdogBaseObject event;
    private SherdogBaseObject fighter1;
    private SherdogBaseObject fighter2;
    private ZonedDateTime date;
    private FightResult result = FightResult.NOT_HAPPENED;
    private String winMethod, winTime;
    private int winRound;
    private FightType type;

    public SherdogBaseObject getEvent() {
        return event;
    }

    public void setEvent(SherdogBaseObject event) {
        this.event = event;
    }

    public SherdogBaseObject getFighter1() {
        return fighter1;
    }

    public void setFighter1(SherdogBaseObject fighter1) {
        this.fighter1 = fighter1;
    }

    public SherdogBaseObject getFighter2() {
        return fighter2;
    }

    public void setFighter2(SherdogBaseObject fighter2) {
        this.fighter2 = fighter2;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public FightResult getResult() {
        return result;
    }

    public void setResult(FightResult result) {
        this.result = result;
    }

    public String getWinMethod() {
        return winMethod;
    }

    public void setWinMethod(String winMethod) {
        this.winMethod = winMethod;
    }

    public String getWinTime() {
        return winTime;
    }

    public void setWinTime(String winTime) {
        this.winTime = winTime;
    }

    public int getWinRound() {
        return winRound;
    }

    public void setWinRound(int winRound) {
        this.winRound = winRound;
    }

    public FightType getType() {
        return type;
    }

    public void setType(FightType type) {
        this.type = type;
    }

    public boolean equals(Object obj) {
        try {
            Fight fight = (Fight) obj;

            return fight.getFighter1().getSherdogUrl().equalsIgnoreCase(fighter1.getSherdogUrl())
                    && fight.getFighter2().getSherdogUrl().equalsIgnoreCase(fighter2.getSherdogUrl()) && event.getSherdogUrl().equals(fight.getEvent().getSherdogUrl());
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public String toString() {
        return "Fight{" +
                "event=" + event +
                ", fighter1=" + fighter1 +
                ", fighter2=" + fighter2 +
                ", date=" + date +
                ", result=" + result +
                ", winMethod='" + winMethod + '\'' +
                ", winTime='" + winTime + '\'' +
                ", winRound=" + winRound +
                '}';
    }
}
