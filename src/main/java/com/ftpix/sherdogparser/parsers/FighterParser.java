package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gz on 20-Aug-16.
 */
public class FighterParser implements SherdogParser<Fighter> {
    private final Logger logger = LoggerFactory.getLogger(FighterParser.class);
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-MM");

    private final String CACHE_FOLDER;
    private final ZoneId ZONE_ID;
    private final int COLUMN_RESULT = 0, COLUMN_OPPONENT = 1, COLUMN_EVENT = 2, COLUMN_METHOD = 3, COLUMN_ROUND = 4, COLUMN_TIME = 5;

    /**
     * Create a fight parser with a specified cache folder
     */
    public FighterParser(String cacheFolder) {
        this.CACHE_FOLDER = cacheFolder;
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Generates a fight parser with specified cache folder and zone id
     */
    public FighterParser(String cacheFolder, ZoneId zoneId) {
        this.CACHE_FOLDER = cacheFolder;
        this.ZONE_ID = zoneId;
    }

    /**
     * FighterPArser with default cache folder location
     */
    public FighterParser() {
        this.CACHE_FOLDER = Constants.FIGHTER_PICTURE_CACHE_FOLDER;
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
            Elements name = doc.select("h1[itemprop=\"name\"] span.fn");
            fighter.setName(name.get(0).html());
        } catch (Exception e) {
            // no info, skipping
        }

        // Getting nick name
        try {
            Elements nickname = doc.select("h1[itemprop=\"name\"] span.nickname em");
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

        if (pictureUrl.length() > 0) {

            String newPath = CACHE_FOLDER + hash(fighter.getSherdogUrl()) + ".JPG";
            File f = new File(newPath);
            FileUtils.copyURLToFile(new URL(pictureUrl), f);
        }

        getFights(doc, fighter);
        fighter.getFights().sort((f1, f2) -> f1.getDate().compareTo(f2.getDate()));

        logger.info("Found {} fights for {}", fighter.getFights().size(), fighter.getName());
        return fighter;
    }


    /**
     * Get a fighter fights
     *
     * @param doc     JSOUP html document
     * @param fighter a fighter to parse against
     */
    private void getFights(Document doc, Fighter fighter) {

        Elements tds = doc.select(".fight_history .table table td");
        // removing header row...
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);
        tds.remove(0);

        Fight fight = new Fight();
        fight.setFighter1(fighter);

        for (int i = 0; i < tds.size(); i++) {
            Element td = tds.get(i);
            switch (i % 6) {
                case COLUMN_RESULT:
                    fight.setResult(ParserUtils.getFightResult(td));
                    break;
                case COLUMN_OPPONENT:
                    Fighter opponent = new Fighter();
                    Element opponentLink = td.select("a").get(0);
                    opponent.setName(opponentLink.html());
                    opponent.setSherdogUrl(opponentLink.attr("abs:href"));
                    fight.setFighter2(opponent);
                    break;
                case COLUMN_EVENT:
                    Element link = td.select("a").get(0);

                    Event event = new Event();
                    event.setName(link.html().replaceAll("<span itemprop=\"award\">|<\\/span>", ""));
                    event.setSherdogUrl(link.attr("abs:href"));
                    //date
                    Element date = td.select("span.sub_line").first();
                   fight.setDate(ParserUtils.getDateFromStringToZoneId(date.html(), ZONE_ID, DateTimeFormatter.ofPattern("MMM / dd / yyyy")));
                    fight.setEvent(event);
                    break;
                case COLUMN_METHOD:
                    fight.setWinMethod(td.html().replaceAll("<br>(.*)", ""));
                    break;
                case COLUMN_ROUND:
                    fight.setWinRound(Integer.parseInt(td.html()));
                    break;
                case COLUMN_TIME:
                    fight.setWinTime(td.html());
                    //last column adding fight and resetting it;
                    fighter.getFights().add(fight);
                    logger.info("{}", fight);
                    fight = new Fight();
                    fight.setFighter1(fighter);
                    break;
            }
        }
    }

    /**
     * Hashes a string
     */
    public static String hash(String s) {
        return DigestUtils.sha256Hex(s);
    }
}
