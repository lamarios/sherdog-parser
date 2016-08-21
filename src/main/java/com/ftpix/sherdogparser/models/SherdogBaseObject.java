package com.ftpix.sherdogparser.models;

/**
 * Created by gz on 21-Aug-16.
 */
public class SherdogBaseObject {
    protected String name, sherdogUrl;

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

    @Override
    public String toString() {
        return "SherdogBaseObject{" +
                "name='" + name + '\'' +
                ", sherdogUrl='" + sherdogUrl + '\'' +
                '}';
    }
}
