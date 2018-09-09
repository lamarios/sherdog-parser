package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.models.*;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Created by gz on 20-Aug-16.
 * tools to help during parsing
 */
public class ParserUtils {
    /**
     * Gets the result of a fight following sherdog website win/lose/draw/nc
     * Make sure to use on Fighter1 only
     *
     * @param element Jsoup element
     * @return a FightResult
     */
    static FightResult getFightResult(Element element) {
        if (element.select(".win").size() > 0) {
            return FightResult.FIGHTER_1_WIN;
        } else if (element.select(".loss").size() > 0) {
            return FightResult.FIGHTER_2_WIN;
        } else if (element.select(".draw").size() > 0) {
            return FightResult.DRAW;
        } else if (element.select(".no_contest").size() > 0) {
            return FightResult.NO_CONTEST;
        } else {
            return FightResult.NOT_HAPPENED;
        }
    }


    /**
     * PArses a URL with all the required parameters
     *
     * @param url
     * @return
     * @throws IOException
     */
    static Document parseDocument(String url) throws IOException {
        return Jsoup
                .connect(url)
                .timeout(Constants.PARSING_TIMEOUT)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.google.com")
                .get();
    }


    /**
     * Converts a String to the given timezone.
     *
     * @param date   Date to format
     * @param zoneId Zone id to convert from sherdog's time
     * @return the  converted zonedatetime
     */
    static ZonedDateTime getDateFromStringToZoneId(String date, ZoneId zoneId) throws DateTimeParseException {
        ZonedDateTime usDate = ZonedDateTime.parse(date).withZoneSameInstant(ZoneId.of(Constants.SHERDOG_TIME_ZONE));
        return usDate.withZoneSameInstant(zoneId);
    }


    /**
     * Converts a String to the given timezone.
     *
     * @param date      Date to format
     * @param zoneId    Zone id to convert from sherdog's time
     * @param formatter Formatter for exotic date format
     * @return the converted zonedatetime
     */
    static ZonedDateTime getDateFromStringToZoneId(String date, ZoneId zoneId, DateTimeFormatter formatter) throws DateTimeParseException {
        try {
            ZonedDateTime usDate = ZonedDateTime.parse(date, formatter).withZoneSameInstant(ZoneId.of(Constants.SHERDOG_TIME_ZONE));
            return usDate.withZoneSameInstant(zoneId);
        } catch (Exception e) {
            //In case the parsing fail, we try without time
            try {
                ZonedDateTime usDate = LocalDate.parse(date, formatter).atStartOfDay(ZoneId.of(Constants.SHERDOG_TIME_ZONE));
                return usDate.withZoneSameInstant(zoneId);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    /**
     * Downloads an image to a file with the adequate headers to the http query
     *
     * @param url  the url of the image
     * @param file the file to create
     * @throws IOException if the file download fails
     */
    public static void downloadImageToFile(String url, Path file) throws IOException {


        if (Files.exists(file.getParent())) {
            URL urlObject = new URL(url);
            URLConnection connection = urlObject.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
            connection.setRequestProperty("Referer", "https://www.google.com");


            FileUtils.copyInputStreamToFile(connection.getInputStream(), file.toFile());
        }
    }


    /**
     * Gets the type of a fight (Pro, amateur etc...)
     *
     * @param parser the sherdsog parser, required as we need to use the parser to get info on the fighter
     * @param fight the fight to check
     * @return the type of the fight
     */
    public static FightType getFightType(Sherdog parser, Fight fight) {

        //getting one of the fighter, and parsing his/her fights to find this fight

        BiFunction<SherdogBaseObject, SherdogBaseObject, FightType> getType = (f1, f2) -> {
            try {
                return parser.getFighter(f1.getSherdogUrl())
                        .getFights()
                        .stream()
                        .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                        .filter(f -> f.getFighter2() != null)
                        .filter(f -> f.getFighter2().getSherdogUrl().equalsIgnoreCase(f2.getSherdogUrl()) && f.getEvent().getSherdogUrl().equalsIgnoreCase(fight.getEvent().getSherdogUrl()))
                        .findFirst()
                        .map(Fight::getType)
                        .orElse(FightType.PRO); //if we don't know, assume pro
            } catch (Exception e) {
                e.printStackTrace();
                return FightType.PRO; //if we dont know assume pro
            }
        };

        if (fight.getResult() == FightResult.NOT_HAPPENED) {
            return FightType.UPCOMING;
        }

        Optional<SherdogBaseObject> fighter = Optional.ofNullable(fight)
                .map(Fight::getFighter1)
                .filter(f -> f != null)
                .filter(f -> f.getSherdogUrl() != null && f.getSherdogUrl().length() > 0);

        if (fighter.isPresent()) {
            return getType.apply(fighter.get(), fight.getFighter2());
        } else {
            return getType.apply(fight.getFighter2(), fight.getFighter1());
        }


    }


    /**
     * Gets the url of a page using the meta tags in head
     * @param doc the jsoup document to extract the page url from
     * @return the url of the document
     */
    public static String getSherdogPageUrl(Document doc){
        String url = doc.head().select("meta[property=\"og:url\"").attr("content");
        return url.replace("http://", "https://")
                ; //forcing https, for secure scrapping
    }
}
