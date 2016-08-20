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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by gz on 20-Aug-16.
 */
public class OrganizationParser implements SherdogParser<Organization> {
    private final Logger logger = LoggerFactory.getLogger(OrganizationParser.class);

    private final int DATE_COLUMN = 1, NAME_COLUMN = 2, LOCATION_COLUMN = 3;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HHmmssZ");
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
        Elements upcomingEventsElement = doc.select("#upcoming_tab .event td");
        organization.getEvents().addAll(parseEvent(upcomingEventsElement));

        logger.info("Getting past events");
        List<Event> toAdd;
        do {
            logger.info("Parsing page [{}]", page);

            toAdd = new ArrayList<>();
            doc = Jsoup.connect(url + page).timeout(Constants.PARSING_TIMEOUT).get();
            Elements events = doc.select("#recent_tab .event td");

            toAdd = parseEvent(events);

            organization.getEvents().addAll(toAdd);
            page++;


        } while (toAdd.size() > 0);


        return organization;
    }


    /**
     * Get all the events of an organization
     * @param td the JSOUP TD elements from the event table
     * @return a list of events
     * @throws ParseException if something is wrong with sherdog layout
     */
    private List<Event> parseEvent(Elements td) throws ParseException {
        List<Event> events = new ArrayList<Event>();

        int i = 1;
        td.remove(0);
        td.remove(0);
        td.remove(0);
        Event event = new Event();
        for (Element element : td) {
            switch (i) {
                case DATE_COLUMN:
                    Elements metaDate = element.select("meta[itemprop=\"startDate\"");
                    String date = metaDate.get(0).attr("content");
                    // Date eventDate = df.parse(date);

                    //OffsetDateTime odt = OffsetDateTime.parse( date );
                    //ZonedDateTime usDate = odt.atZoneSameInstant(ZoneId.of(Constants.SHERDOG_TIME_ZONE));

                    // ZonedDateTime usDate = ZonedDateTime.ofInstant(eventDate.toInstant(), );
                    //System.out.println(usDate);
                    // event.setDate(usDate);
                    break;
                case NAME_COLUMN:
                    Elements name = element.select("span[itemprop=\"name\"");
                    event.setName(name.get(0).html());


                    event.setName(event.getName().replaceAll("( )+", " "));
                    event.setName(event.getName().trim());
                    Elements url = element.select("a[itemprop=\"url\"");
                    event.setShergodUrl(url.get(0).attr("abs:href"));
                    break;
                case LOCATION_COLUMN:
                    String[] split = element.html().split(">");
                    event.setLocation(split[1].trim());
                    break;
                default:
                    break;
            }
            i++;
            if (i == 4) {
                // event.setOrganization(organization);

                // Date local = DateUtils.convertTimeZone(event.getDate(),
                // TimeZone.getTimeZone(Constants.SHERDOG_TIME_ZONE),
                // TimeZone.getDefault());
                // logger.info("Event date: Sherdog:{}, local:{}",
                // event.getDate(), local);

                logger.info("Adding event {}", event.getName());
                events.add(event);

                event = new Event();
                i = 1;
            }
            // logger.info(element.html());
        }

        return events;
    }
}
