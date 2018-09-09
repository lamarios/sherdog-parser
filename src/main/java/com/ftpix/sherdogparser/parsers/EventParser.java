package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.models.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gz on 20-Aug-16.
 * Class to parse an event by using its sherdog url
 */
public class EventParser implements SherdogParser<Event> {
    private final int FIGHTER1_COLUMN = 1, FIGHTER2_COLUMN = 3, METHOD_COLUMN = 4, ROUND_COLUMN = 5, TIME_COLUMN = 6;
    private final Logger logger = LoggerFactory.getLogger(EventParser.class);

    private final ZoneId ZONE_ID;

    /**
     * Setting a zoneId will convert the dates to the desired zone id
     * @param zoneId  specified zone id for time conversion
     */
    public EventParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    /**
     * Creates an event parser with the default zone id
     */
    public EventParser() {
        this.ZONE_ID = ZoneId.systemDefault();
    }


    /**
     * parses an event from a jsoup document
     *
     * @param doc the jsoup document
     * @return a parsed event
     */
    @Override
    public Event parseDocument(Document doc) {
        Event event = new Event();

        event.setSherdogUrl(ParserUtils.getSherdogPageUrl(doc));

        //getting name
        Elements name = doc.select(".header .section_title h1 span[itemprop=\"name\"]");
        event.setName(name.html().replace("<br>", " - "));

        Elements date = doc.select(".authors_info .date meta[itemprop=\"startDate\"]");
        //TODO: get date to proper format
        try {
            event.setDate(ParserUtils.getDateFromStringToZoneId(date.first().attr("content"), ZONE_ID));
        } catch (DateTimeParseException e) {
            logger.error("Couldn't parse date", e);
        }


        getFights(doc, event);

        Element org = doc.select(".header .section_title h2 a").get(0);
        SherdogBaseObject organization = new SherdogBaseObject();
        organization.setSherdogUrl(org.attr("abs:href"));
        organization.setName(org.select("span[itemprop=\"name\"").get(0).html());


        event.setOrganization(organization);
        return event;
    }


    /**
     * Gets the fight of the event
     *
     * @param doc   the jsoup HTML document
     * @param event The current event
     */
    private void getFights(Document doc, Event event) {
        logger.info("Getting fights for event #{}[{}]", event.getSherdogUrl(), event.getName());

        SherdogBaseObject sEvent = new SherdogBaseObject();
        sEvent.setName(event.getName());
        sEvent.setSherdogUrl(event.getSherdogUrl());

        List<Fight> fights = new ArrayList<>();

        //Checking on main event
        Elements mainFightElement = doc.select(".content.event");

        Elements fighters = mainFightElement.select("h3 a");
        //First fighter
        SherdogBaseObject mainFighter1 = new SherdogBaseObject();
        Element mainFighter1Element = fighters.get(0);
        mainFighter1.setSherdogUrl(mainFighter1Element.attr("abs:href"));
        mainFighter1.setName(mainFighter1Element.select("span[itemprop=\"name\"]").html());

        //second fighter
        SherdogBaseObject mainFighter2 = new SherdogBaseObject();
        Element mainFighter2Element = fighters.get(1);
        mainFighter2.setSherdogUrl(mainFighter2Element.attr("abs:href"));
        mainFighter2.setName(mainFighter2Element.select("span[itemprop=\"name\"]").html());


        Fight mainFight = new Fight();
        mainFight.setEvent(sEvent);
        mainFight.setFighter1(mainFighter1);
        mainFight.setFighter2(mainFighter2);
        mainFight.setResult(ParserUtils.getFightResult(mainFightElement.first()));

        //getting method
        Elements mainTd = mainFightElement.select("td");
        if (mainTd.size() > 0) {
            mainFight.setWinMethod(mainTd.get(1).html().replaceAll("<em(.*)<br>", "").trim());
            mainFight.setWinRound(Integer.parseInt(mainTd.get(3).html().replaceAll("<em(.*)<br>", "").trim()));
            mainFight.setWinTime(mainTd.get(4).html().replaceAll("<em(.*)<br>", "").trim());
        }
        mainFight.setDate(event.getDate());


        fights.add(mainFight);
        logger.info("Fight added: {}", mainFight);
        //Checking on card results

        logger.info("Found {} fights", fights.size());

        Elements tds = doc.select(".event_match table tr");

        fights.addAll(parseEventFights(tds, event));


        event.setFights(fights);
    }


    /**
     * Parse fights of an old event
     */
    private List<Fight> parseEventFights(Elements trs, Event event) {
        SherdogBaseObject sEvent = new SherdogBaseObject();
        sEvent.setName(event.getName());
        sEvent.setSherdogUrl(event.getSherdogUrl());

        List<Fight> fights = new ArrayList<>();

        if (trs.size() > 0) {
            trs.remove(0);

            trs.forEach(tr -> {
                Fight fight = new Fight();
                fight.setEvent(sEvent);
                fight.setDate(event.getDate());
                Elements tds = tr.select("td");

                fight.setFighter1(getFighter(tds.get(FIGHTER1_COLUMN)));
                fight.setFighter2(getFighter(tds.get(FIGHTER2_COLUMN)));

                //parsing old fight, we can get the result
                if (tds.size() == 7) {
                    fight.setResult(getResult(tds.get(FIGHTER1_COLUMN)));
                    fight.setWinMethod(getMethod(tds.get(METHOD_COLUMN)));
                    fight.setWinRound(getRound(tds.get(ROUND_COLUMN)));
                    fight.setWinTime(getTime(tds.get(TIME_COLUMN)));
                }

                fights.add(fight);
                logger.info("Fight added: {}", fight);
            });
        }

        return fights;
    }

    /**
     * Get a fighter
     *
     * @param td element from sherdog's table
     * @return return a sherdogbaseobject with the fighter name and url
     */
    private SherdogBaseObject getFighter(Element td) {

        Elements name1 = td.select("span[itemprop=\"name\"]");

        if (name1.size() > 0) {

            String name = name1.get(0).html();

            Elements select = td.select("a[itemprop=\"url\"]");

            if (select.size() > 0) {
                String url = select.get(0).attr("abs:href");

                SherdogBaseObject fighter = new SherdogBaseObject();
                fighter.setSherdogUrl(url);
                fighter.setName(name);
                return fighter;
            }
        }

        return null;
    }

    /**
     * get the time at which teh fight finished
     *
     * @param td element from sherdog's table
     * @return get the time of the event
     */
    private String getTime(Element td) {
        return td.html();
    }


    /**
     * get the round at which the even finished
     *
     * @param td element from sherdog's table
     * @return the round number
     */
    private int getRound(Element td) {
        return Integer.parseInt(td.html());
    }


    /**
     * @param td element from sherdog's table
     * @return get the win method
     */
    private String getMethod(Element td) {
        return td.html().replaceAll("<br>(.*)", "");
    }

    /**
     * get the result of the fight
     *
     * @param td element from sherdog's table
     * @return a rightresult enum
     */
    private FightResult getResult(Element td) {
        return ParserUtils.getFightResult(td);
    }

}
