package com.ftpix.sherdogparser.models;

import com.ftpix.sherdogparser.Sherdog;

import java.io.IOException;

public class Search {

    private String term;
    private SearchWeightClass weightClass = null;

    private Sherdog sherdog;


    public Search(String term, Sherdog sherdog) {
        this.term = term;
        this.sherdog = sherdog;
    }


    /**
     * Filter the search results by weight class
     * @param weightClass the chosen weight class
     * @return
     */
    public Search withWeightClass(SearchWeightClass weightClass){
        this.weightClass = weightClass;

        return this;
    }


    /**
     * Performs the search
     * @return the search results
     * @throws IOException when the query fails
     */
    public SearchResults query() throws IOException {
        return new SearchResults(this.term, this.weightClass, sherdog);
    }

}
