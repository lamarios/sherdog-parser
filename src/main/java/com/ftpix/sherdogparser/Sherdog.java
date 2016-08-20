package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.sherdogparser.parsers.EventParser;
import com.ftpix.sherdogparser.parsers.FighterParser;
import com.ftpix.sherdogparser.parsers.OrganizationParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;

/**
 * Created by gz on 20-Aug-16.
 */
public class Sherdog {
    private String cacheFolder = Constants.FIGHTER_PICTURE_CACHE_FOLDER;
    private ZoneId zoneId = ZoneId.systemDefault();


    /**
     * Get the cache folder
     * @return
     */
    public String getCacheFolder() {
        return cacheFolder;
    }


    /**
     * sets the cache folder, will create it if it doesn't exist
     * @param cacheFolder
     */
    public void setCacheFolder(String cacheFolder) {
        if (!cacheFolder.endsWith("/")) {
            cacheFolder += "/";
        }

        File f = new File(cacheFolder);

        if (!f.exists()) {
            f.mkdirs();
        }

        this.cacheFolder = cacheFolder;
    }

    /**
     * Gets the zone id
     * @return
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Sets the zoneod
     * @param zoneId
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
     */
    public Organization getOrganization(String sherdogUrl) throws IOException, ParseException {
        return new OrganizationParser().parse(sherdogUrl);
    }

    /**
     * Gets an event via it's sherdog URL.
     * @param sherdogUrl Sherdog URL, can be found in the list of event of an organization
     * @return an Event
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    public Event getEvent(String sherdogUrl) throws IOException, ParseException {
        return new EventParser(zoneId).parse(sherdogUrl);
    }

    /**
     * Get a fighter via it;s sherdog URL.
     * @param sherdogUrl
     * @return a Fighter an all his fights
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    public Fighter getFighter(String sherdogUrl) throws IOException, ParseException {
        return new FighterParser(cacheFolder).parse(sherdogUrl);
    }


    /**
     * Builder
     */
    public static class Builder {
        private Sherdog parser = new Sherdog();

        /**
         * Sets a cache folder for the parser
         * @param folder
         * @return
         */
        public Builder withCacheFolder(String folder) {
            parser.setCacheFolder(folder);
            return this;
        }


        /**
         * Sets a timezone for the parser , this will help convert the timezone to the wanted timezone
         * @param timezone
         * @return
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
