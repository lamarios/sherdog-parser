package com.ftpix.sherdogparser.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Event {

    private Organization organization;
    private String name;
    private ZonedDateTime date;
    private String shergodUrl;
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

    public String getShergodUrl() {
        return shergodUrl;
    }

    public void setShergodUrl(String shergodUrl) {
        this.shergodUrl = shergodUrl;
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
            return event.getShergodUrl().equalsIgnoreCase(shergodUrl);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", shergodUrl='" + shergodUrl + '\'' +
                '}';
    }
}
