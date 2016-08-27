package com.ftpix.sherdogparser;

import com.google.gson.Gson;

import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.sherdogparser.models.Organizations;
import com.ftpix.sherdogparser.parsers.EventParser;
import com.ftpix.sherdogparser.parsers.FighterParser;
import com.ftpix.sherdogparser.parsers.OrganizationParser;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.time.ZoneId;

import io.gsonfire.GsonFireBuilder;

import static org.junit.Assert.*;

/**
 * Created by gz on 20-Aug-16.
 */
public class ParserTest {

    @Test
    public void testOrganizationParser() throws IOException, ParseException {
        Organization ufc = new OrganizationParser().parse(Organizations.UFC.url);



        //ufc.getEvents().forEach(System.out::println);
        assertEquals("Ultimate Fighting Championship", ufc.getName());

        //Checking on few main events
        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getName().equalsIgnoreCase("UFC 1 - The Beginning")));
        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getSherdogUrl().equalsIgnoreCase("http://www.sherdog.com/events/UFC-1-The-Beginning-7")));

        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getName().equalsIgnoreCase("UFC 100 - Lesnar vs. Mir 2")));
        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getSherdogUrl().equalsIgnoreCase("http://www.sherdog.com/events/UFC-100-Lesnar-vs-Mir-2-9568")));

        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getName().equalsIgnoreCase("UFC 200 - Tate vs. Nunes")));
        assertTrue(ufc.getEvents().stream().anyMatch(e -> e.getSherdogUrl().equalsIgnoreCase("http://www.sherdog.com/events/UFC-200-Tate-vs-Nunes-47285")));
        assertEquals(Organizations.UFC.url, ufc.getSherdogUrl());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(ufc);


    }

    @Test
    public void testEventParser() throws IOException, ParseException {
//
//        Event test = new EventParser(ZoneId.of("Asia/Kuala_Lumpur")).parse("http://www.sherdog.com/events/UFC-Mexico-City-51653");
//        System.out.println(test);
//        test.getFights().forEach(System.out::println);


        //System.out.println(gson.toJson(test));

        Event ufc1 = new EventParser(ZoneId.of("Asia/Kuala_Lumpur")).parse("http://www.sherdog.com/events/UFC-1-The-Beginning-7");

        assertEquals("UFC 1 - The Beginning", ufc1.getName());
        assertEquals("Ultimate Fighting Championship", ufc1.getOrganization().getName());
        assertEquals("http://www.sherdog.com/organizations/Ultimate-Fighting-Championship-2", ufc1.getOrganization().getSherdogUrl());
        assertEquals(8, ufc1.getFights().size());
        assertEquals("http://www.sherdog.com/events/UFC-1-The-Beginning-7", ufc1.getSherdogUrl());
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", ufc1.getDate().toString());

        //Testing main event
        Fight fight = ufc1.getFights().get(0);
        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertEquals("http://www.sherdog.com/fighter/Royce-Gracie-19", fight.getFighter1().getSherdogUrl());
        assertEquals("Royce Gracie", fight.getFighter1().getName());
        assertEquals("http://www.sherdog.com/fighter/Gerard-Gordeau-15", fight.getFighter2().getSherdogUrl());
        assertEquals("Gerard Gordeau", fight.getFighter2().getName());
        assertEquals("Submission (Rear-Naked Choke)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("1:44", fight.getWinTime());
        assertEquals("UFC 1 - The Beginning", fight.getEvent().getName());
        assertEquals("http://www.sherdog.com/events/UFC-1-The-Beginning-7", fight.getEvent().getSherdogUrl());
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());

        //Testing main event
        fight = ufc1.getFights().get(6);
        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertEquals("http://www.sherdog.com/fighter/Kevin-Rosier-17", fight.getFighter1().getSherdogUrl());
        assertEquals("Kevin Rosier", fight.getFighter1().getName());
        assertEquals("http://www.sherdog.com/fighter/Zane-Frazier-18", fight.getFighter2().getSherdogUrl());
        assertEquals("Zane Frazier", fight.getFighter2().getName());
        assertEquals("TKO (Punches)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("4:20", fight.getWinTime());
        assertEquals("UFC 1 - The Beginning", fight.getEvent().getName());
        assertEquals("http://www.sherdog.com/events/UFC-1-The-Beginning-7", fight.getEvent().getSherdogUrl());
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(ufc1);

    }

    @Test
    public void testFighterParser() throws IOException, ParseException {
        //trying to test on a passed away fighter to make the data won't change
        //RIP Kevin
        Fighter fighter = new FighterParser(Constants.FIGHTER_PICTURE_CACHE_FOLDER, ZoneId.of("Asia/Kuala_Lumpur")).parse("http://www.sherdog.com/fighter/Kevin-Randleman-162");
       // Fighter condit = new FighterParser(Constants.FIGHTER_PICTURE_CACHE_FOLDER, ZoneId.of("Asia/Kuala_Lumpur")).parse("http://www.sherdog.com/fighter/Bec-Rawlings-84964");




        assertEquals("Kevin Randleman", fighter.getName());
        assertEquals(17, fighter.getWins());
        assertEquals(16, fighter.getLosses());
        assertEquals(0, fighter.getDraws());
        assertEquals(0, fighter.getNc());
        assertEquals("The Monster", fighter.getNickname());
        assertEquals("5'10\"", fighter.getHeight());
        assertEquals("205 lbs", fighter.getWeight());
        assertEquals("http://www.sherdog.com/fighter/Kevin-Randleman-162", fighter.getSherdogUrl());

        if (fighter.getPicture() != null && fighter.getPicture().trim().length() > 0) {
            File f = new File(fighter.getPicture());
            assertTrue(f.exists());
        }

        assertEquals(17 + 16, fighter.getFights().size());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(fighter);


        Fight fight = fighter.getFights().get(fighter.getFights().size() - 1);

        //fighter.getFights().forEach(System.out::println);

        assertEquals(FightResult.FIGHTER_2_WIN, fight.getResult());
        assertEquals("http://www.sherdog.com/fighter/Baga-Agaev-11793", fight.getFighter2().getSherdogUrl());
        assertEquals("Baga Agaev", fight.getFighter2().getName());
        assertEquals("Submission (Armbar)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("4:05", fight.getWinTime());
        assertEquals("FEFoMP - Mayor's Cup 2011", fight.getEvent().getName());
        assertEquals("http://www.sherdog.com/events/FEFoMP-Mayors-Cup-2011-16778", fight.getEvent().getSherdogUrl());
        assertEquals("2011-05-07T12:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());
        //Testing fight with award span
        fight = fighter.getFights().get(2);

        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertEquals("http://www.sherdog.com/fighter/Dan-Bobish-174", fight.getFighter2().getSherdogUrl());
        assertEquals("Dan Bobish", fight.getFighter2().getName());
        assertEquals("Submission (Punches)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("5:50", fight.getWinTime());
        assertEquals("UVF 4 - Universal Vale Tudo Fighting 4", fight.getEvent().getName());
        assertEquals("http://www.sherdog.com/events/UVF-4-Universal-Vale-Tudo-Fighting-4-394", fight.getEvent().getSherdogUrl());
        assertEquals("1996-10-22T12:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());
        //assertTrue(fighter.getBirthday() == 0);
    }

    @Test
    public void testBuilder() throws IOException {

        Sherdog parser = new Sherdog.Builder().withCacheFolder("cache-test").withTimezone("Asia/Kuala_Lumpur").build();

//        Organization ufc = parser.getOrganization(Organizations.UFC.url);
//
//        Event ufc1 = parser.getEvent(ufc.getEvents().get(0).getSherdogUrl());
//
//        Fight firstFight = ufc1.getFights().get(0);
//
//        Fighter fighter = parser.getFighter(firstFight.getFighter1().getSherdogUrl());

        assertEquals("cache-test/", parser.getCacheFolder());
        assertEquals("Asia/Kuala_Lumpur", parser.getZoneId().getId());

        File f = new File(parser.getCacheFolder());
        assertTrue(f.exists());

    }
}
