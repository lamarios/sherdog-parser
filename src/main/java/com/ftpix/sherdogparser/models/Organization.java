package com.ftpix.sherdogparser.models;

import java.util.ArrayList;
import java.util.List;


public class Organization extends SherdogBaseObject {

    private List<Event> events = new ArrayList<>();

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
