package com.ftpix.sherdogparser.models;


public enum Organizations {
    UFC("https://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2"),
    INVICTA_FC("https://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469"),
    BELLATOR("https://www.sherdog.com/organizations/Bellator-MMA-1960"),
    ONE_FC("https://www.sherdog.com/organizations/One-Championship-3877"),
    WSOF("https://www.sherdog.com/organizations/World-Series-of-Fighting-5449");


    public String url;

    Organizations(String url) {
        this.url = url;
    }


}

