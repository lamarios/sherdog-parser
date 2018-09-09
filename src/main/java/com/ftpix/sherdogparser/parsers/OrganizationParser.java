package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.sherdogparser.models.SherdogBaseObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gz on 20-Aug-16.
 * PArser to parse an organization
 */
public class OrganizationParser implements SherdogParser<Organization> {
    private final Logger logger = LoggerFactory.getLogger(OrganizationParser.class);

    private final int DATE_COLUMN = 0, NAME_COLUMN = 1, LOCATION_COLUMN = 2;

    private final ZoneId ZONE_ID;


    /**
     * Creates an organization parser with the default zone id
     */
    public OrganizationParser() {
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Create a parser with a specified zoneid
     * @param zoneId  specified zone id for time conversion
     */
    public OrganizationParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    /**
     * Parse a sherdog page
     *
     * @param doc Jsoup document of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    public Organization parseDocument(Document doc) throws IOException, ParseException {
        Organization organization = new Organization();
        organization.setSherdogUrl(ParserUtils.getSherdogPageUrl(doc));

        String url= organization.getSherdogUrl();
        url += "/recent-events/%d";
        int page = 1;


        doc = ParserUtils.parseDocument(String.format(url, page));

        logger.info("Getting name");
        Elements name = doc.select(".bio_organization .module_header h2[itemprop=\"name\"");
        organization.setName(name.html());


        logger.info("Getting upcoming event");
        Elements upcomingEventsElement = doc.select("#upcoming_tab .event tr");
        organization.getEvents().addAll(parseEvent(upcomingEventsElement, organization));

        logger.info("Getting past events");
        List<Event> toAdd;
        do {
            logger.info("Parsing page [{}]", page);

            doc = ParserUtils.parseDocument(String.format(url, page));
            Elements events = doc.select("#recent_tab .event tr");

            toAdd = parseEvent(events, organization);

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
    private List<Event> parseEvent(Elements trs, Organization organization) throws ParseException {
        List<Event> events = new ArrayList<>();

        if (trs.size() > 0) {
            trs.remove(0);


            SherdogBaseObject sOrg = new SherdogBaseObject();
            sOrg.setName(organization.getName());
            sOrg.setSherdogUrl(organization.getSherdogUrl());


            trs.forEach(tr -> {

                Event event = new Event();
                boolean addEvent = true;
                Elements tds = tr.select("td");

                event.setOrganization(sOrg);

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
        }

        return events;
    }

    private String getElementLocation(Element td) {
        String[] split = td.html().split(">");
        if (split.length > 1) {
            return split[1].trim();
        } else {
            return "";
        }
    }

    private String getEventName(Element td) {
        Elements nameElement = td.select("span[itemprop=\"name\"");

        if (nameElement.size() > 0) {
            String name = nameElement.get(0).html();
            name = name.replaceAll("( )+", " ").trim();
            return name;
        } else {
            return "";
        }
    }

    private String getEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"");
        if (url.size() > 0) {
            String attr = url.get(0).attr("abs:href");
            return attr;
        } else {
            return "";
        }
    }

    private ZonedDateTime getEventDate(Element element) {
        Elements metaDate = element.select("meta[itemprop=\"startDate\"");
        if (metaDate.size() > 0) {
            String date = metaDate.get(0).attr("content");

            return ParserUtils.getDateFromStringToZoneId(date, ZONE_ID);
        } else {
            return null;
        }
    }
}
