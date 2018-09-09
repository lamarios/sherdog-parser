package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.sherdogparser.models.Organizations;
import com.ftpix.sherdogparser.parsers.EventParser;
import com.ftpix.sherdogparser.parsers.FighterParser;
import com.ftpix.sherdogparser.parsers.OrganizationParser;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;

/**
 * Created by gz on 20-Aug-16.
 * The main class of sherdog-parser
 */
public class Sherdog {
    private ZoneId zoneId = ZoneId.systemDefault();
    private PictureProcessor pictureProcessor = Constants.DEFAULT_PICTURE_PROCESSOR;




    /**
     * Gets the zone id
     * @return the current zoneid
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Sets the zoneod
     * @param zoneId which zone id the event times need to be converted
     */
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }


    /**
     * Gets an organization via it's sherdog URL.
     * @param sherdogUrl Sherdog URL, can find predefined url in Organizations.* enum.
     * @return an Organization
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Organization getOrganization(String sherdogUrl) throws IOException, ParseException, SherdogParserException {
        return new OrganizationParser(zoneId).parse(sherdogUrl);
    }

    /**
     * Gets an organization via it's sherdog page HTML, in case you want to have your own way of getting teh HTML content
     * @param html The web page HTML
     * @return an Organization
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Organization getOrganizationFromHtml(String html) throws IOException, ParseException, SherdogParserException {
        return new OrganizationParser(zoneId).parseFromHtml(html);
    }


    /**
     * Gets an organization via it's sherdog URL.
     * @param organization An organization from the Organizations. enum
     * @return an Organization
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Organization getOrganization(Organizations organization) throws IOException, ParseException, SherdogParserException {
        return new OrganizationParser(zoneId).parse(organization.url);
    }

    /**
     * Gets an event via it's shergog page HTML
     * @param html The web page HTML
     * @return an Event
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Event getEventFromHtml(String html) throws IOException, ParseException, SherdogParserException {
        return new EventParser(zoneId).parseFromHtml(html);
    }

    /**
     * Gets an event via it's sherdog URL.
     * @param sherdogUrl Sherdog URL, can be found in the list of event of an organization
     * @return an Event
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Event getEvent(String sherdogUrl) throws IOException, ParseException, SherdogParserException {
        return new EventParser(zoneId).parse(sherdogUrl);
    }


    /**
     * Get a fighter via it;s sherdog page HTML
     * @param html The web page HTML
     * @return a Fighter an all his fights
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Fighter getFighterFromHtml(String html) throws IOException, ParseException, SherdogParserException {
        return new FighterParser(pictureProcessor, zoneId).parseFromHtml(html);
    }
    /**
     * Get a fighter via it;s sherdog URL.
     * @param sherdogUrl the shergod url of the fighter
     * @return a Fighter an all his fights
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     * @throws SherdogParserException if anythign related to the parser goes wrong
     */
    public Fighter getFighter(String sherdogUrl) throws IOException, ParseException, SherdogParserException {
        return new FighterParser(pictureProcessor, zoneId).parse(sherdogUrl);
    }


    /**
     * Gets a picture processor
     * @return
     */
    public PictureProcessor getPictureProcessor() {
        return pictureProcessor;
    }

    /**
     * Sets a picture processor if some processing is needed for the fighter picture
     * @param pictureProcessor the picture processor to use
     */
    public void setPictureProcessor(PictureProcessor pictureProcessor) {
        this.pictureProcessor = pictureProcessor;
    }

    /**
     * Builder
     */
    public static class Builder {
        private Sherdog parser = new Sherdog();

        /**
         * Sets a cache folder for the parser
         * @param processor the picture processor to user with the parser check {@link PictureProcessor} for more info
         * @return the sherdog current state
         */
        public Builder withPictureProcessor(PictureProcessor processor) {
            parser.setPictureProcessor(processor);
            return this;
        }


        /**
         * Sets a timezone for the parser , this will help convert the timezone to the wanted timezone
         * @param timezone timezone for the sherdog builder
         * @return the sherdog current state
         */
        public Builder withTimezone(String timezone) {
            parser.setZoneId(ZoneId.of(timezone));
            return this;
        }

        public Sherdog build() {
            return parser;
        }


    }
}
