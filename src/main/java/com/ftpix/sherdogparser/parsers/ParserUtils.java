package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.Constants;
import com.ftpix.sherdogparser.models.FightResult;

import org.jsoup.nodes.Element;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by gz on 20-Aug-16.
 * tools to help during parsing
 */
class ParserUtils {
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
        } else if (element.select(".nc").size() > 0) {
            return FightResult.NO_CONTEST;
        } else {
            return FightResult.NOT_HAPPENED;
        }
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
            }catch(DateTimeParseException e2){
                return null;
            }
        }
    }
}
