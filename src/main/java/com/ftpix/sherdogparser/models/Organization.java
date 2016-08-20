package com.ftpix.sherdogparser.models;

import java.util.ArrayList;
import java.util.List;


public class Organization {

    private int id;
    private String name;
    private String sherdogUrl;

    public List<Event> events = new ArrayList<Event>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSherdogUrl() {
        return sherdogUrl;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Organization org = (Organization) obj;
            return org.getSherdogUrl().equalsIgnoreCase(this.sherdogUrl);
        } catch (Exception e) {
            return false;
        }
    }


}
