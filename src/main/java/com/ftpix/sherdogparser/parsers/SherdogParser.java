package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.exceptions.NotASherdogURLException;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by gz on 20-Aug-16.
 * teh default interface for a shergod parser
 */
public interface SherdogParser<T> {

    /**
     * Parse a sherdog page
     *
     * @param url of the sherdog page
     * @return the object parsed by the parser
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    default T parse(String url) throws IOException, ParseException, SherdogParserException {


        if(!url.startsWith("https://www.sherdog.com/")){
            throw new NotASherdogURLException();
        }

        Document doc = ParserUtils.parseDocument(url);
        return parseDocument(doc);
    }


    /**
     * Parses a document from the HTML source code directly
     *
     * @param html the HTML source code
     * @return the parsed object
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    default T parseFromHtml(String html) throws IOException, ParseException {
        return parseDocument(Jsoup.parse(html));
    }

    /**
     * Parses a jsoup document
     *
     * @param doc the document to parse
     * @return the parsed object
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    T parseDocument(Document doc) throws ParseException, IOException;
}
