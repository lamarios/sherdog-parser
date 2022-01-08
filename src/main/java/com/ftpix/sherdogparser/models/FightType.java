package com.ftpix.sherdogparser.models;

public enum FightType {
    PRO, PRO_EXHIBITION, EXHIBITION, AMATEUR, OTHER, UPCOMING;

    public static FightType fromString(String type) {
        switch (type.toUpperCase().trim()) {
            case "PRO":
            case "FIGHT HISTORY - PRO":
                return PRO;
            case "PRO EXHIBITION":
            case "FIGHT HISTORY - PRO EXHIBITION":
                return PRO_EXHIBITION;
            case "AMATEUR":
            case "FIGHT HISTORY - AMATEUR":
                return AMATEUR;
            case "EXHIBITION":
            case "FIGHT HISTORY - EXHIBITION":
                return EXHIBITION;
            default:
                return OTHER;

        }
    }
}
