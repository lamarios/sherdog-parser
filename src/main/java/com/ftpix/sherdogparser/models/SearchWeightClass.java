package com.ftpix.sherdogparser.models;

public enum SearchWeightClass {
    CATCHWEIGHT(11, "Catchweight"),
    MINIMUMWEIGHT(17, "Minimumweight"),
    POUND_FOR_POUND(8, "Pound for Pound"),
    ATOMWEIGHT(15, "Atomweight"),
    STRAWWEIGHT(13, "Strawweight"),
    FLYWEIGHT(10, "Flyweight"),
    BANTAMWEIGHT(9, "Bantamweight"),
    FEATHERWEIGHT(7, "Featherweight"),
    LIGHTWEIGHT(6, "Lightweight"),
    WELTERWEIGHT(5, "Welterweight"),
    MIDDLEWEIGHT(4, "Middleweight"),
    LIGHT_HEAVYWEIGHT(3, "Light Heavyweight"),
    HEAVYWEIGHT(2, "Heavyweight"),
    SUPER_HEAVYWEIGHT(1, "Super Heavyweight");


    SearchWeightClass(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private Integer value;
    private String label;


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
