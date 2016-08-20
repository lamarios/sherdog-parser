package com.ftpix.sherdogparser.parsers;

import com.ftpix.sherdogparser.models.FightResult;

import org.jsoup.nodes.Element;

/**
 * Created by gz on 20-Aug-16.
 */
public class ParserUtils {

    /**
     * Gets the result of a fight following sherdog website win/lose/draw/nc
     * Make sure to use on Fighter1 only
     *
     * @param element Jsoup element
     * @return a FightResult
     */
    public static FightResult getFightResult(Element element) {
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
}
