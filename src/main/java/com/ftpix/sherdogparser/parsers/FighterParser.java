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
import java.util.Locale;
import java.util.stream.Collectors;

import static com.ftpix.sherdogparser.parsers.ParserUtils.*;

/**
 * Created by gz on 20-Aug-16.
 * Parse a fighter through a url
 */
public class FighterParser implements SherdogParser<Fighter> {
    private final Logger logger = LoggerFactory.getLogger(FighterParser.class);
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-MM");
    private final SimpleDateFormat df2 = new SimpleDateFormat("MMM dd, yyyy");
    private final SimpleDateFormat df3 = new SimpleDateFormat("MMMM dd, yyyy");

    private final PictureProcessor PROCESSOR;
    private final ZoneId ZONE_ID;
    private final int COLUMN_RESULT = 0, COLUMN_OPPONENT = 1, COLUMN_EVENT = 2, COLUMN_METHOD = 3, COLUMN_ROUND = 4, COLUMN_TIME = 5;
    private final int METHOD_KO = 0, METHOD_SUBMISSION = 1, METHOD_DECISION = 2, METHOD_OTHERS = 3;

    /**
     * Create a fight parser with a specified cache folder
     *
     * @param processor the picture processor to use for the fighter pictures
     */
    public FighterParser(PictureProcessor processor) {
        this.PROCESSOR = processor;
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Generates a fight parser with specified cache folder and zone id
     *
     * @param processor the picture processor to use for the fighter pictures
     * @param zoneId    specified zone id for time conversion
     */
    public FighterParser(PictureProcessor processor, ZoneId zoneId) {
        this.PROCESSOR = processor;
        this.ZONE_ID = zoneId;
    }

    /**
     * FighterPArser with default cache folder location
     *
     * @param zoneId specified zone id for time conversion
     */
    public FighterParser(ZoneId zoneId) {
        this.PROCESSOR = Constants.DEFAULT_PICTURE_PROCESSOR;
        ZONE_ID = zoneId;

    }


    /**
     * Parse a sherdog page
     *
     * @param doc Jsoup document of the sherdog page
     * @throws IOException if connecting to sherdog fails
     */
    @Override
    public Fighter parseDocument(Document doc) throws IOException {
        Fighter fighter = new Fighter();
        fighter.setSherdogUrl(getSherdogPageUrl(doc));

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

        Elements bioTable = doc.select(".bio-holder table tr");

        try {
            for (Element tr : bioTable) {
                Elements td = tr.select("td");
                if (td.size() == 2) {
                    switch (td.get(0).text()) {
                        case "AGE":
                            Elements birthday = td.get(1).select("span[itemprop=\"birthDate\"]");
                            fighter.setBirthday(df2.parse(birthday.get(0).html()));
                            break;
                        case "HEIGHT":
                            fighter.setHeight(ParserUtils.getText(td.get(1), "b:nth-child(1)"));
                            break;
                        case "WEIGHT":
                            fighter.setWeight(getText(td.get(1), "b:nth-child(1)"));
                            break;

                    }
                }

            }
        } catch (Exception e) {
            logger.error("Couldn't parse bio", e);
        }

        // wins
        try {
            Elements wins = doc.select("div.win > span:nth-child(2)");
            fighter.setWins(Integer.parseInt(wins.get(0).html()));
        } catch (Exception e) {
            // no info, skipping
        }
        try {
            fighter.setWinsKo(getInt(doc, ".wins > div:nth-child(3) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }

        try {
            fighter.setWinsSub(getInt(doc, ".wins > div:nth-child(5) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }

        try {
            fighter.setWinsDec(getInt(doc, ".wins > div:nth-child(7) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }


        try {
            fighter.setWinsOther(getInt(doc, ".wins > div:nth-child(9) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }
        // loses
        try {
            fighter.setLosses(getInt(doc, ".lose > span:nth-child(2)"));
        } catch (Exception e) {
            // no info, skipping
        }


        try {

            fighter.setLossesKo((getInt(doc, ".loses > div:nth-child(3) > div:nth-child(1)")));
        } catch (Exception e) {
            // no info, skipping
        }

        try {
            fighter.setLossesSub(getInt(doc, ".loses > div:nth-child(5) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }

        try {
            fighter.setLossesDec(getInt(doc, ".loses > div:nth-child(7) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }

        try {
            fighter.setLossesOther(getInt(doc, ".loses > div:nth-child(9) > div:nth-child(1)"));
        } catch (Exception e) {
            // no info, skipping
        }
        // draws and NC
        try {
            fighter.setDraws(getInt(doc, "div.winloses.draws > span:nth-child(2)"));
        } catch (Exception e) {
        }

        try {
            fighter.setNc(getInt(doc, "div.winloses.nc > span:nth-child(2)"));
        } catch (Exception e) {
        }

        Elements picture = doc.select("img.profile-image.photo[itemprop=\"image\"]");
        String pictureUrl = "https://www.sherdog.com" + picture.attr("src").trim();


        fighter.setFights(new ArrayList<>());

        // upcoming fight
        try {
            Elements fightPreview = doc.select(".fight_card_preview");
            if (fightPreview.size() == 1) {
                Fight fight = new Fight();
                fight.setFighter1(fighter);

                SherdogBaseObject fighter2 = new SherdogBaseObject();
                fighter2.setName(getText(doc, "div.fighter:nth-child(3) > h3:nth-child(2) > a:nth-child(1) > span:nth-child(1)"));
                fighter2.setSherdogUrl(doc.select("div.fighter:nth-child(3) > h3:nth-child(2) > a:nth-child(1)").get(0).attr("abs:href"));
                fight.setFighter2(fighter2);

                Elements fightCardButton = doc.select(".fight_card_preview .card_button");
                if (!fightCardButton.isEmpty()) {
                    Event event = new Event();
                    event.setSherdogUrl(fightCardButton.attr("abs:href"));
                    event.setName(getText(doc, ".fight_card_preview > h2:nth-child(1)"));
                    event.setLocation(getText(doc, ".date_location > em:nth-child(2) > span:nth-child(2)"));

                    String date = doc.select(".date_location meta").get(0).attr("content");
                    ZonedDateTime dateFromStringToZoneId = getDateFromStringToZoneId(date, ZONE_ID);
                    event.setDate(dateFromStringToZoneId);
                    fight.setDate(dateFromStringToZoneId);

                    fight.setEvent(event);

                }

                fighter.getFights().add(fight);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        Elements fightTables = doc.select(".fight_history");
        logger.info("Found {} fight history tables", fightTables.size());

        doc.select("section").stream().filter(section -> !section.select(".fight_history").isEmpty()).forEach(section -> {
            Elements title = section.select(".slanted_title div:nth-child(1)");
            FightType type = FightType.fromString(title.html());

            Elements trs = section.select(".new_table.fighter tbody tr");
            List<Fight> fights = this.getFights(trs, fighter).stream().map(f -> {
                f.setType(type);
                return f;
            }).collect(Collectors.toList());
            fighter.getFights().addAll(fights);

        });


        List<Fight> sorted = fighter.getFights().stream().sorted(Comparator.comparing(Fight::getDate, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());

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
                fight.setWinMethod(getWinMethod(tds.get(COLUMN_METHOD)).replaceAll("</?b>",""));
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
        event.setName(link.html().replaceAll("<span itemprop=\"award\">|</span>", ""));
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

        return getDateFromStringToZoneId(date.html(), ZONE_ID, DateTimeFormatter.ofPattern("MMM / dd / yyyy", Locale.US));
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
