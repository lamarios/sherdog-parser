package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Organization;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by gz on 20-Aug-16.
 */
public class OrganizationParser implements SherdogParser<Organization> {
    private final Logger logger = LoggerFactory.getLogger(OrganizationParser.class);

    private final int DATE_COLUMN = 0, NAME_COLUMN = 1, LOCATION_COLUMN = 2;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
    //private final SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ZoneId ZONE_ID;


    /**
     * Creates an organization parser with the default zone id
     */
    public OrganizationParser() {
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Create a parser with a specified zoneid
     */
    public OrganizationParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    /**
     * Parse a sherdog page
     *
     * @param url of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    public Organization parse(String url) throws IOException, ParseException {
        Organization organization = new Organization();
        organization.setSherdogUrl(url);

        url += "/recent-events/";
        int page = 1;


        Document doc = Jsoup.connect(url + page).timeout(Constants.PARSING_TIMEOUT).get();

        logger.info("Getting name");
        Elements name = doc.select(".bio_organization .module_header h2[itemprop=\"name\"");
        organization.setName(name.html());


        logger.info("Getting upcoming event");
        Elements upcomingEventsElement = doc.select("#upcoming_tab .event tr");
        organization.getEvents().addAll(parseEvent(upcomingEventsElement));

        logger.info("Getting past events");
        List<Event> toAdd;
        do {
            logger.info("Parsing page [{}]", page);

            toAdd = new ArrayList<>();
            doc = Jsoup.connect(url + page).timeout(Constants.PARSING_TIMEOUT).get();
            Elements events = doc.select("#recent_tab .event tr");

            toAdd = parseEvent(events);

            organization.getEvents().addAll(toAdd);
            page++;


        } while (toAdd.size() > 0);


        organization.getEvents().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return organization;
    }


    /**
     * Get all the events of an organization
     *
     * @param trs the JSOUP TR elements from the event table
     * @return a list of events
     * @throws ParseException if something is wrong with sherdog layout
     */
    private List<Event> parseEvent(Elements trs) throws ParseException {
        List<Event> events = new ArrayList<Event>();

        trs.remove(0);

        trs.forEach(tr -> {

            Event event = new Event();
            boolean addEvent = true;
            Elements tds = tr.select("td");


            event.setName(getEventName(tds.get(NAME_COLUMN)));
            event.setSherdogUrl(getEventUrl(tds.get(NAME_COLUMN)));
            event.setLocation(getElementLocation(tds.get(LOCATION_COLUMN)));

            try {
                event.setDate(getEventDate(tds.get(DATE_COLUMN)));
            } catch (DateTimeParseException e) {
                logger.error("Couldn't fornat date, we shouldn't add the event", e);
                addEvent = false;
            }

            if (addEvent) {
                events.add(event);
            }
        });


        return events;
    }

    private String getElementLocation(Element td) {
        String[] split = td.html().split(">");
        return split[1].trim();
    }

    private String getEventName(Element td) {
        Elements nameElement = td.select("span[itemprop=\"name\"");
        String name = nameElement.get(0).html();
        name = name.replaceAll("( )+", " ").trim();

        return name;
    }

    private String getEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"");
        return url.get(0).attr("abs:href");
    }

    private ZonedDateTime getEventDate(Element element) {
        Elements metaDate = element.select("meta[itemprop=\"startDate\"");
        String date = metaDate.get(0).attr("content");

        return ParserUtils.getDateFromStringToZoneId(date, ZONE_ID);
    }
}
