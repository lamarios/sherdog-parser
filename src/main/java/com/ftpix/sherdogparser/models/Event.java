package com.ftpix.sherdogparser.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class Event extends  SherdogBaseObject{

    private SherdogBaseObject organization;
    private ZonedDateTime date;
    private List<Fight> fights = new ArrayList<>();
    private String location = "";

    public SherdogBaseObject getOrganization() {
        return organization;
    }

    public void setOrganization(SherdogBaseObject organization) {
        this.organization = organization;
    }



    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }


    public List<Fight> getFights() {
        return fights;
    }

    public void setFights(List<Fight> fights) {
        this.fights = fights;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean equals(Object arg0) {
        try {
            Event event = (Event) arg0;
            return event.getSherdogUrl().equalsIgnoreCase(sherdogUrl);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return "Event{" +
                "name=" + name +
                "organization=" + organization +
                ", date=" + date +
                ", fights=" + fights +
                ", location='" + location + '\'' +
                '}';
    }
}
