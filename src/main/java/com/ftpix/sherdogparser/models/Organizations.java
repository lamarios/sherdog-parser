package com.ftpix.sherdogparser.models;


public enum Organizations {
    UFC("http://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2"),
    INVICTA_FC("http://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469"),
    BELLATOR("http://www.sherdog.com/organizations/Bellator-MMA-1960"),
    ONE_FC("http://www.sherdog.com/organizations/One-Championship-3877"),
    WSOF("http://www.sherdog.com/organizations/World-Series-of-Fighting-5449");


    public String url;

    Organizations(String url) {
        this.url = url;
    }


}

