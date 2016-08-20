package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.Organization;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gz on 20-Aug-16.
 */
public class EventParser implements SherdogParser<Event> {
    private final int FIGHTER1_COLUMN = 2, FIGHTER2_COLUMN = 4, METHOD_COLUMN = 5, ROUND_COLUMN = 6, TIME_COLUMN = 0;
    private final Logger logger = LoggerFactory.getLogger(EventParser.class);

    private final ZoneId ZONE_ID;

    /**
     * Setting a zoneId will convert the dates to the desired zone id
     * @param zoneId
     */
    public EventParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    /**
     * Creates an event parser with the default zone id
     */
    public EventParser(){
        this.ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Parse a sherdog page
     * @param url of the sherdog page
     * @return
     * @throws IOException if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    @Override
    public Event parse(String url) throws IOException, ParseException {
        Event event = new Event();
        event.setShergodUrl(url);

        Document doc = Jsoup.connect(url).timeout(Constants.PARSING_TIMEOUT).get();

        //getting name
        Elements name = doc.select(".header .section_title h1 span[itemprop=\"name\"]");
        event.setName(name.html().replace("<br>", " - "));

        Elements date = doc.select(".authors_info .date meta[itemprop=\"startDate\"]");
        //TODO: get date to proper format
        date.first().attr("content");

        getFights(doc, event);

        Element org = doc.select(".header .section_title h2 a").get(0);
        Organization organization = new Organization();
        organization.setSherdogUrl(org.attr("abs:href"));
        organization.setName(org.select("span[itemprop=\"name\"").get(0).html());

        event.setOrganization(organization);
        return event;
    }


    /**
     * Gets the fight of the event
     * @param doc the jsoup HTML document
     * @param event The current event
     */
    private void getFights(Document doc, Event event) {
        logger.info("Getting fights for event #{}[{}]", event.getShergodUrl(), event.getName());

        List<Fight> fights = new ArrayList<Fight>();

        //Checking on main event
        Elements mainFightElement = doc.select(".content.event");

        Elements fighters = mainFightElement.select("h3 a");
        //First fighter
        Fighter mainFighter1 = new Fighter();
        Element mainFighter1Element = fighters.get(0);

        mainFighter1.setSherdogUrl(mainFighter1Element.attr("abs:href"));
        mainFighter1.setName(mainFighter1Element.select("span[itemprop=\"name\"]").html());

        //second fighter
        Fighter mainFighter2 = new Fighter();
        Element mainFighter2Element = fighters.get(1);
        mainFighter2.setSherdogUrl(mainFighter2Element.attr("abs:href"));
        mainFighter2.setName(mainFighter2Element.select("span[itemprop=\"name\"]").html());


        Fight mainFight = new Fight();
        mainFight.setEvent(event);
        mainFight.setFighter1(mainFighter1);
        mainFight.setFighter2(mainFighter2);
        mainFight.setResult(ParserUtils.getFightResult(mainFightElement.first()));

        //getting method
        Elements mainTd = mainFightElement.select("td");
        mainFight.setWinMethod(mainTd.get(1).html().replaceAll("<em(.*)<br>", "").trim());
        mainFight.setWinRound(Integer.parseInt(mainTd.get(3).html().replaceAll("<em(.*)<br>", "").trim()));
        mainFight.setWinTime(mainTd.get(4).html().replaceAll("<em(.*)<br>", "").trim());

        fights.add(mainFight);
        logger.info("Fight added: {}", mainFight);
        //Checking on card results
        Elements tds = doc.select(".event_match table td");
        int i = 1;

        Fight fight = new Fight();

        tds.remove(0);
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);

        for (Element td : tds) {

            String name, url;
            Fighter fighter = new Fighter();

            switch (i % 7) {

                case FIGHTER1_COLUMN:
                    Elements name1 = td.select("span[itemprop=\"name\"]");
                    name = name1.get(0).html();
                    url = td.select("a[itemprop=\"url\"]").get(0).attr("abs:href");

                    fighter.setSherdogUrl(url);
                    fighter.setName(name);

                    fight.setFighter1(fighter);
                    fight.setResult(ParserUtils.getFightResult(td));
                    break;
                case FIGHTER2_COLUMN:
                    Elements name2 = td.select("span[itemprop=\"name\"]");

                    name = name2.get(0).html();
                    url = td.select("a[itemprop=\"url\"]").get(0).attr("abs:href");

                    fighter.setSherdogUrl(url);
                    fighter.setName(name);
                    fight.setFighter2(fighter);
                    break;
                case METHOD_COLUMN:
                    fight.setWinMethod(td.html().replaceAll("<br>(.*)", ""));
                    break;
                case ROUND_COLUMN:
                    fight.setWinRound(Integer.parseInt(td.html()));
                    break;
                case TIME_COLUMN:
                    fight.setWinTime(td.html());

                    fight.setEvent(event);
                    //fight.setDate(event.getDate());
                    fights.add(fight);
                    logger.info("Fight added: {}", fight);

                    fight = new Fight();
                    break;
                default:
                    break;
            }
            i++;

        }

        logger.info("Found {} fights", fights.size());

        event.setFights(fights);
    }
}
