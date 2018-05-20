package com.ftpix.sherdogparser.models;

public enum FightType {
    PRO, PRO_EXHIBITION, EXHIBITION, AMATEUR, OTHER, UPCOMING;

    public static FightType fromString(String type) {
        switch (type.toUpperCase().trim()) {
            case "PRO":
                return PRO;
            case "PRO EXHIBITION":
                return PRO_EXHIBITION;
            case "AMATEUR":
                return AMATEUR;
            case "EXHIBITION":
                return EXHIBITION;
            default:
                return OTHER;

        }
    }
}
