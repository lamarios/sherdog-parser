package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.PictureProcessor;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.SherdogBaseObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gz on 20-Aug-16.
 * Parse a fighter through a url
 */
public class FighterParser implements SherdogParser<Fighter> {
    private final Logger logger = LoggerFactory.getLogger(FighterParser.class);
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-MM");

    private final PictureProcessor PROCESSOR;
    private final ZoneId ZONE_ID;
    private final int COLUMN_RESULT = 0, COLUMN_OPPONENT = 1, COLUMN_EVENT = 2, COLUMN_METHOD = 3, COLUMN_ROUND = 4, COLUMN_TIME = 5;

    /**
     * Create a fight parser with a specified cache folder
     */
    public FighterParser(PictureProcessor processor) {
        this.PROCESSOR = processor;
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Generates a fight parser with specified cache folder and zone id
     */
    public FighterParser(PictureProcessor processor, ZoneId zoneId) {
        this.PROCESSOR = processor;
        this.ZONE_ID = zoneId;
    }

    /**
     * FighterPArser with default cache folder location
     *
     * @param zoneId
     */
    public FighterParser(ZoneId zoneId) {
        this.PROCESSOR = Constants.DEFAULT_PICTURE_PROCESSOR;
        ZONE_ID = ZoneId.systemDefault();

    }


    /**
     * Parse a sherdog page
     *
     * @param url of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    @Override
    public Fighter parse(String url) throws IOException, ParseException {
        Fighter fighter = new Fighter();
        fighter.setSherdogUrl(url);

        logger.info("Refreshing fighter {}", fighter.getSherdogUrl());
        Document doc = Jsoup.connect(fighter.getSherdogUrl()).timeout(Constants.PARSING_TIMEOUT).get();

        try {
            Elements name = doc.select(".bio_fighter h1 span.fn");
            fighter.setName(name.get(0).html());
        } catch (Exception e) {
            // no info, skipping
        }

        // Getting nick name
        try {
            Elements nickname = doc.select(".bio_fighter span.nickname em");
            fighter.setNickname(nickname.get(0).html());
        } catch (Exception e) {
            // no info, skipping
        }

        // Birthday
        try {
            Elements birthday = doc.select("span[itemprop=\"birthDate\"]");
            fighter.setBirthday(df.parse(birthday.get(0).html()));
        } catch (Exception e) {
            // no info, skipping
        }
        // height
        try {
            Elements height = doc.select(".size_info .height strong");
            fighter.setHeight(height.get(0).html());
        } catch (Exception e) {
            // no info, skipping
        }
        // weight
        try {
            Elements weight = doc.select(".size_info .weight strong");
            fighter.setWeight(weight.get(0).html());
        } catch (Exception e) {
            // no info, skipping
        }
        // wins
        try {
            Elements wins = doc.select(".bio_graph .counter");
            fighter.setWins(Integer.parseInt(wins.get(0).html()));
        } catch (Exception e) {
            // no info, skipping
        }
        // wins
        try {
            Elements losses = doc.select(".bio_graph.loser .counter");
            fighter.setLosses(Integer.parseInt(losses.get(0).html()));
        } catch (Exception e) {
            // no info, skipping
        }
        // draws and NC
        Elements drawsNc = doc.select(".right_side .bio_graph .card");
        for (Element element : drawsNc) {

            switch (element.select("span.result").html()) {
                case "Draws":
                    fighter.setDraws(Integer.parseInt(element.select("span.counter").html()));
                    break;

                case "N/C":
                    fighter.setNc(Integer.parseInt(element.select("span.counter").html()));
                    break;
            }

        }

        Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
        String pictureUrl = picture.attr("src").trim();


        Elements fightTables = doc.select(".fight_history ");
        logger.info("Found {} fight history tables", fightTables.size());

        fightTables.stream()
                .filter(div -> div.select(".module_header h2").html().trim().equalsIgnoreCase("FIGHT HISTORY - PRO"))
                .map(div -> div.select(".table table tr"))
                .filter(tdList -> tdList.size() > 0)
                .findFirst()
                .ifPresent((tdList -> fighter.setFights(getFights(tdList, fighter))));

        //fighter.getFights().sort((f1, f2) -> f1.getDate().compareTo(f2.getDate()));
        Collections.reverse(fighter.getFights());
        logger.info("Found {} fights for {}", fighter.getFights().size(), fighter.getName());

        //setting the picture last to make sure the fighter variable has all the data
        if (pictureUrl.length() > 0) {
            fighter.setPicture(PROCESSOR.process(pictureUrl, fighter));
        }

        return fighter;
    }


    /**
     * Get a fighter fights
     *
     * @param trs     JSOUP TRs document
     * @param fighter a fighter to parse against
     */
    private List<Fight> getFights(Elements trs, Fighter fighter) throws ArrayIndexOutOfBoundsException {
        List<Fight> fights = new ArrayList<>();

        logger.info("{} TRs to parse through", trs.size());


        SherdogBaseObject sFighter = new SherdogBaseObject();
        sFighter.setName(fighter.getName());
        sFighter.setSherdogUrl(fighter.getSherdogUrl());


        // removing header row...
        if (trs.size() > 0) {
            trs.remove(0);

            trs.forEach(tr -> {
                Fight fight = new Fight();
                fight.setFighter1(sFighter);

                Elements tds = tr.select("td");
                fight.setResult(getFightResult(tds.get(COLUMN_RESULT)));
                fight.setFighter2(getOpponent(tds.get(COLUMN_OPPONENT)));
                fight.setEvent(getEvent(tds.get(COLUMN_EVENT)));
                fight.setDate(getDate(tds.get(COLUMN_EVENT)));
                fight.setWinMethod(getWinMethod(tds.get(COLUMN_METHOD)));
                fight.setWinRound(getWinRound(tds.get(COLUMN_ROUND)));
                fight.setWinTime(getWinTime(tds.get(COLUMN_TIME)));
                fights.add(fight);
                logger.info("{}", fight);
            });
        }

        return fights;
    }


    /**
     * Get the fight result
     *
     * @param td a td from sherdogs table
     * @return a fight result enum
     */
    private FightResult getFightResult(Element td) {
        return ParserUtils.getFightResult(td);
    }


    /**
     * Get the fight result
     *
     * @param td a td from sherdogs table
     * @return a fight result enum
     */
    private SherdogBaseObject getOpponent(Element td) {
        SherdogBaseObject opponent = new SherdogBaseObject();
        Element opponentLink = td.select("a").get(0);
        opponent.setName(opponentLink.html());
        opponent.setSherdogUrl(opponentLink.attr("abs:href"));

        return opponent;
    }


    /**
     * Get the fight event
     *
     * @param td a td from sherdogs table
     * @return a sherdog base object with url and name
     */
    private SherdogBaseObject getEvent(Element td) {
        Element link = td.select("a").get(0);

        SherdogBaseObject event = new SherdogBaseObject();
        event.setName(link.html().replaceAll("<span itemprop=\"award\">|<\\/span>", ""));
        event.setSherdogUrl(link.attr("abs:href"));


        return event;
    }

    /**
     * Get the date of the fight
     *
     * @param td a td from sherdogs table
     * @return the zonedatetime of the fight
     */
    private ZonedDateTime getDate(Element td) {
        //date
        Element date = td.select("span.sub_line").first();
        return ParserUtils.getDateFromStringToZoneId(date.html(), ZONE_ID, DateTimeFormatter.ofPattern("MMM / dd / yyyy"));
    }


    /**
     * Get the winning method
     *
     * @param td a td from sherdogs table
     * @return a string with the finishing method
     */
    private String getWinMethod(Element td) {
        return td.html().replaceAll("<br>(.*)", "");
    }


    /**
     * Get the winning round
     *
     * @param td a td from sherdogs table
     * @return an itneger
     */
    private int getWinRound(Element td) {
        return Integer.parseInt(td.html());
    }


    /**
     * Get time of win
     *
     * @param td a td from sherdogs table
     * @return the time of win
     */
    private String getWinTime(Element td) {
        return td.html();
    }

    /**
     * Hashes a string
     *
     * @param s the string to hash
     * @return the hashed string
     */
    private String hash(String s) {
        return DigestUtils.sha256Hex(s);
    }
}
