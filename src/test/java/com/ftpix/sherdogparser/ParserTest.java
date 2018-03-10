package com.ftpix.sherdogparser;

import com.google.gson.Gson;

import com.ftpix.sherdogparser.models.Event;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.Organization;
import com.ftpix.sherdogparser.models.Organizations;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import io.gsonfire.GsonFireBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gz on 20-Aug-16.
 */
public class ParserTest {

    private static Sherdog sherdog;

    @BeforeClass
    public static void setup() {
        sherdog = new Sherdog.Builder().withTimezone("Asia/Kuala_Lumpur").build();

    }


    @Test
    public void testBuilder() throws IOException {

        assertEquals("Asia/Kuala_Lumpur", sherdog.getZoneId().getId());

    }

    @Test
    public void testOrganizationParser() throws IOException, ParseException {
        Organization ufc = sherdog.getOrganization(Organizations.UFC);


        //ufc.getEvents().forEach(System.out::println);
        assertEquals("Ultimate Fighting Championship (UFC)", ufc.getName());
        assertEquals(Organizations.UFC.url, ufc.getSherdogUrl());


        Event ufc1 = ufc.getEvents().get(0);
        assertEquals("UFC 1 - The Beginning", ufc1.getName());
        assertEquals("http://www.sherdog.com/events/UFC-1-The-Beginning-7", ufc1.getSherdogUrl());
        assertEquals(ufc.getName(), ufc1.getOrganization().getName());
        assertEquals(ufc.getSherdogUrl(), ufc1.getOrganization().getSherdogUrl());
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", ufc1.getDate().toString());
        assertEquals("McNichols Arena, Denver, Colorado, United States", ufc1.getLocation());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(ufc);

        //ufc.getEvents().forEach(System.out::println);


    }

    @Test
    public void testEventParser() throws IOException, ParseException {
//
//        Event test = new EventParser(ZoneId.of("Asia/Kuala_Lumpur")).parse("http://www.sherdog.com/events/UFC-Mexico-City-51653");
//        System.out.println(test);
//        test.getFights().forEach(System.out::println);


        //System.out.println(gson.toJson(test));

        Event ufc1 = sherdog.getEvent("http://www.sherdog.com/events/UFC-1-The-Beginning-7");

        assertEquals("UFC 1 - The Beginning", ufc1.getName());
        assertEquals("Ultimate Fighting Championship (UFC)", ufc1.getOrganization().getName());
        assertEquals("http://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2", ufc1.getOrganization().getSherdogUrl());
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
        Fighter fighter = sherdog.getFighter("http://www.sherdog.com/fighter/Kevin-Randleman-162");
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
        assertEquals("http://www.sherdog.com/image_crop/200/300/_images/fighter/20141021014120_IMG_4313.JPG", fighter.getPicture());
        assertEquals(17 + 16, fighter.getFights().size());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(fighter);


        Fight fight = fighter.getFights().get(fighter.getFights().size() - 1);

        //fighter.getFights().forEach(System.out::println);

        assertEquals(FightResult.FIGHTER_2_WIN, fight.getResult());
        assertEquals("http://www.sherdog.com/fighter/Magomedbag-Agaev-11793", fight.getFighter2().getSherdogUrl());
        assertEquals("Magomedbag Agaev", fight.getFighter2().getName());
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
    public void testFighterWithWrongDateFormatInfights() throws IOException, ParseException {
        Fighter fighter = sherdog.getFighter("http://www.sherdog.com/fighter/Johil-de-Oliveira-6");

        fighter.getFights().forEach(f ->{
            System.out.println(f.getDate());
        });

        //will throw exception if fail.

    }


    @Test
    public void testEventWithIndexOutOfBoundsException() throws IOException, ParseException {
        Event event = sherdog.getEvent("http://www.sherdog.com/events/DNRF-Ukrainian-Octagon-2-16471");
    }

    @Test
    public void testCustomPictureProcessor() throws IOException, ParseException {
        final Path tempFile = Files.createTempFile("parser-test", "");

        Sherdog sherdog = new Sherdog.Builder().withPictureProcessor((u, f) -> {
            //downloading a file

            FileUtils.copyURLToFile(new URL(u), tempFile.toFile());

            return tempFile.toAbsolutePath().toString();

        }).build();

        Fighter fighter = sherdog.getFighter("http://www.sherdog.com/fighter/Kevin-Randleman-162");
        assertEquals("The fighter picture should have the same value as our temp file absolute path", tempFile.toAbsolutePath().toString(), fighter.getPicture());

    }


}
