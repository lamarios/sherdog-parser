package com.ftpix.sherdogparser.models;

/**
 * Created by gz on 20-Aug-16.
 */
public enum Organizations {
    UFC("http://www.sherdog.com/organizations/Ultimate-Fighting-Championship-2"),
    INVICTA_FC("http://www.sherdog.com/organizations/Invicta-Fighting-Championships-4469"),
    BELLATOR("http://www.sherdog.com/organizations/Bellator-MMA-1960");


    public String url;

    Organizations(String url) {
        this.url = url;
    }


}

