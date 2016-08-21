package com.ftpix.sherdogparser.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Event implements SherdogModel{

    private Organization organization;
    private String name;
    private ZonedDateTime date;
    private String sherdogUrl;
    private List<Fight> fights;
    private String location;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public String getSherdogUrl() {
        return sherdogUrl;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
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
                "name='" + name + '\'' +
                ", date=" + date +
                ", sherdogUrl='" + sherdogUrl + '\'' +
                '}';
    }


}
