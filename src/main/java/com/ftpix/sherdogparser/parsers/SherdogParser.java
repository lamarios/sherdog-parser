package com.ftpix.sherdogparser.parsers;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by gz on 20-Aug-16.
 */
public interface SherdogParser<T> {

    /**
     * Parse a sherdog page
     *
     * @param url of the sherdog page
     * @return the object parsed by the parser
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    T parse(String url) throws IOException, ParseException;
}