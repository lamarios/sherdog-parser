package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.PictureProcessor;
import com.ftpix.sherdogparser.models.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param processor the picture processor to use for the fighter pictures
     */
    public FighterParser(PictureProcessor processor) {
        this.PROCESSOR = processor;
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Generates a fight parser with specified cache folder and zone id
     * @param processor the picture processor to use for the fighter pictures
     * @param zoneId  specified zone id for time conversion
     */
    public FighterParser(PictureProcessor processor, ZoneId zoneId) {
        this.PROCESSOR = processor;
        this.ZONE_ID = zoneId;
    }

    /**
     * FighterPArser with default cache folder location
     *
     * @param zoneId  specified zone id for time conversion
     */
    public FighterParser(ZoneId zoneId) {
        this.PROCESSOR = Constants.DEFAULT_PICTURE_PROCESSOR;
        ZONE_ID = ZoneId.systemDefault();

    }


    /**
     * Parse a sherdog page
     *
     * @param doc Jsoup document of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     */
    @Override
    public Fighter parseDocument(Document doc) throws IOException {
        Fighter fighter = new Fighter();
        fighter.setSherdogUrl(ParserUtils.getSherdogPageUrl(doc));

        logger.info("Refreshing fighter {}", fighter.getSherdogUrl());


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
        String pictureUrl = "https://www.sherdog.com" + picture.attr("src").trim();


        Elements fightTables = doc.select(".fight_history ");
        logger.info("Found {} fight history tables", fightTables.size());


        fightTables.stream()
                //excluding upcoming fights
                .filter(div -> !div.select(".module_header h2").html().trim().contains("Upcoming"))
                .collect(Collectors.groupingBy(div -> {
                    String categoryName = div.select(".module_header h2").html().trim().replaceAll("(?i)FIGHT HISTORY - ", "").trim();

                    return FightType.fromString(categoryName);
                }))
                .forEach((key, div) -> {
                    div.stream()
                            .map(d -> d.select(".table table tr"))
                            .filter(tdList -> tdList.size() > 0)
                            .findFirst()
                            .ifPresent(tdList -> {
                                        List<Fight> f = getFights(tdList, fighter);

                                        f.forEach(fight -> fight.setType(key));

                                        fighter.getFights().addAll(f);
                                    }
                            );
                });

        List<Fight> sorted = fighter.getFights()
                .stream()
                .sorted(Comparator.comparing(Fight::getDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        fighter.setFights(sorted);


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
        ZonedDateTime dateFromStringToZoneId = ParserUtils.getDateFromStringToZoneId(date.html(), ZONE_ID, DateTimeFormatter.ofPattern("MMM / dd / yyyy"));

        return dateFromStringToZoneId;
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
