package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.*;
import com.ftpix.sherdogparser.parsers.ParserUtils;
import com.google.gson.Gson;
import io.gsonfire.GsonFireBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

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
    public void testOrganizationParser() throws IOException, ParseException, SherdogParserException {
        Organization ufc = sherdog.getOrganization(Organizations.UFC);


        //ufc.getEvents().forEach(System.out::println);
        assertEquals("Ultimate Fighting Championship (UFC)", ufc.getName());
        assertEquals(Organizations.UFC.url, ufc.getSherdogUrl());


        Event ufc1 = ufc.getEvents().get(0);
        assertEquals("UFC 1 - The Beginning", ufc1.getName());
        assertTrue( ufc1.getSherdogUrl().matches("https?://www.sherdog.com/events/UFC-1-The-Beginning-7"));
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
    public void testEventParser() throws IOException, ParseException, SherdogParserException {
//
//        Event test = new EventParser(ZoneId.of("Asia/Kuala_Lumpur")).parse("https://www.sherdog.com/events/UFC-Mexico-City-51653");
//        System.out.println(test);
//        test.getFights().forEach(System.out::println);


        //System.out.println(gson.toJson(test));

        Event ufc1 = sherdog.getEvent("https://www.sherdog.com/events/UFC-1-The-Beginning-7");

        assertEquals("UFC 1 - The Beginning", ufc1.getName());
        assertEquals("Ultimate Fighting Championship (UFC)", ufc1.getOrganization().getName());
        assertTrue( ufc1.getOrganization().getSherdogUrl().matches("https?://www.sherdog.com/organizations/Ultimate-Fighting-Championship-UFC-2"));
        assertEquals(8, ufc1.getFights().size());
        assertEquals("https://www.sherdog.com/events/UFC-1-The-Beginning-7", ufc1.getSherdogUrl());
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", ufc1.getDate().toString());

        //Testing main event
        Fight fight = ufc1.getFights().get(0);
        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertTrue(fight.getFighter1().getSherdogUrl().matches("https?://www.sherdog.com/fighter/Royce-Gracie-19"));
        assertEquals("Royce Gracie", fight.getFighter1().getName());
        assertTrue( fight.getFighter2().getSherdogUrl().matches("https?://www.sherdog.com/fighter/Gerard-Gordeau-15"));
        assertEquals("Gerard Gordeau", fight.getFighter2().getName());
        assertEquals("Submission (Rear-Naked Choke)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("1:44", fight.getWinTime());
        assertEquals("UFC 1 - The Beginning", fight.getEvent().getName());
        assertTrue( fight.getEvent().getSherdogUrl().matches("https?://www.sherdog.com/events/UFC-1-The-Beginning-7"));
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());

        //Testing main event
        fight = ufc1.getFights().get(6);
        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertTrue( fight.getFighter1().getSherdogUrl().matches("https?://www.sherdog.com/fighter/Kevin-Rosier-17"));
        assertEquals("Kevin Rosier", fight.getFighter1().getName());
        assertTrue( fight.getFighter2().getSherdogUrl().matches("https?://www.sherdog.com/fighter/Zane-Frazier-18"));
        assertEquals("Zane Frazier", fight.getFighter2().getName());
        assertEquals("TKO (Punches)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("4:20", fight.getWinTime());
        assertEquals("UFC 1 - The Beginning", fight.getEvent().getName());
        assertTrue( fight.getEvent().getSherdogUrl().matches("https://www.sherdog.com/events/UFC-1-The-Beginning-7"));
        assertEquals("1993-11-12T16:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(ufc1);

    }

    @Test
    public void testFighterParser() throws IOException, ParseException, SherdogParserException {
        //trying to test on a passed away fighter to make sure the data won't change
        //RIP Kevin
        Fighter fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Kevin-Randleman-162");
        // Fighter condit = new FighterParser(Constants.FIGHTER_PICTURE_CACHE_FOLDER, ZoneId.of("Asia/Kuala_Lumpur")).parse("https://www.sherdog.com/fighter/Bec-Rawlings-84964");


        assertEquals("Kevin Randleman", fighter.getName());
        assertEquals(17, fighter.getWins());
        assertEquals(16, fighter.getLosses());
        assertEquals(0, fighter.getDraws());
        assertEquals(0, fighter.getNc());
        assertEquals("The Monster", fighter.getNickname());
        assertEquals("5'10\"", fighter.getHeight());
        assertEquals("205 lbs", fighter.getWeight());
        assertEquals("https://www.sherdog.com/fighter/Kevin-Randleman-162", fighter.getSherdogUrl());
        assertEquals("https://www.sherdog.com/image_crop/200/300/_images/fighter/20141021014120_IMG_4313.JPG", fighter.getPicture());
        assertEquals(17 + 16, fighter.getFights().size());

        //Testing gson in case of stackoverflow.
        Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).serializeSpecialFloatingPointValues().create();
        gson.toJson(fighter);


        Fight fight = fighter.getFights().get(fighter.getFights().size() - 1);

        //fighter.getFights().forEach(System.out::println);

        assertEquals(FightResult.FIGHTER_2_WIN, fight.getResult());
        assertEquals("https://www.sherdog.com/fighter/Magomedbag-Agaev-11793", fight.getFighter2().getSherdogUrl());
        assertEquals("Magomedbag Agaev", fight.getFighter2().getName());
        assertEquals("Submission (Armbar)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("4:05", fight.getWinTime());
        assertEquals("FEFoMP - Mayor's Cup 2011", fight.getEvent().getName());
        assertEquals("https://www.sherdog.com/events/FEFoMP-Mayors-Cup-2011-16778", fight.getEvent().getSherdogUrl());
        assertEquals("2011-05-07T12:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());
        //Testing fight with award span
        fight = fighter.getFights().get(0);

        assertEquals(FightResult.FIGHTER_1_WIN, fight.getResult());
        assertEquals("https://www.sherdog.com/fighter/Dan-Bobish-174", fight.getFighter2().getSherdogUrl());
        assertEquals("Dan Bobish", fight.getFighter2().getName());
        assertEquals("Submission (Punches)", fight.getWinMethod());
        assertEquals(1, fight.getWinRound());
        assertEquals("5:50", fight.getWinTime());
        assertEquals("UVF 4 - Universal Vale Tudo Fighting 4", fight.getEvent().getName());
        assertEquals("https://www.sherdog.com/events/UVF-4-Universal-Vale-Tudo-Fighting-4-394", fight.getEvent().getSherdogUrl());
        assertEquals("1996-10-22T12:00+08:00[Asia/Kuala_Lumpur]", fight.getDate().toString());
        //assertTrue(fighter.getBirthday() == 0);
    }


    @Test
    public void testFighterWithWrongDateFormatInfights() throws IOException, ParseException, SherdogParserException {
        Fighter fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Johil-de-Oliveira-6");

        fighter.getFights().forEach(f -> {
            System.out.println(f.getDate());
        });

        //will throw exception if fail.

    }


    @Test
    public void testEventWithIndexOutOfBoundsException() throws IOException, ParseException, SherdogParserException {
        Event event = sherdog.getEvent("https://www.sherdog.com/events/DNRF-Ukrainian-Octagon-2-16471");
    }

    @Test
    public void testCustomPictureProcessor() throws IOException, ParseException, SherdogParserException {
        final Path tempFile = Files.createTempFile("parser-test", "");

        Sherdog sherdog = new Sherdog.Builder().withPictureProcessor((u, f) -> {
            //downloading a file

            ParserUtils.downloadImageToFile(u, tempFile);

            return tempFile.toAbsolutePath().toString();

        }).build();

        Fighter fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Kevin-Randleman-162");
        assertEquals("The fighter picture should have the same value as our temp file absolute path", tempFile.toAbsolutePath().toString(), fighter.getPicture());

    }


    @Test
    public void testGettingFightType() throws IOException, ParseException, SherdogParserException {
        Event event = sherdog.getEvent("https://www.sherdog.com/events/UFC-Fight-Night-115-Volkov-vs-Struve-58751");
        assertEquals(FightType.PRO, ParserUtils.getFightType(sherdog, event.getFights().get(7)));


        Fighter fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Rose-Namajunas-69083");

        assertEquals(FightType.AMATEUR, fighter.getFights().get(0).getType());
        assertEquals(FightType.PRO_EXHIBITION, fighter.getFights().get(7).getType());
        assertEquals(FightType.PRO, fighter.getFights().get(10).getType());


        //Testing the method that will try to find the fight type if not available
        event = sherdog.getEvent("https://www.sherdog.com/events/KOTC-Trump-Card-19961");

        Fight fight = event.getFights().get(10);
        assertEquals(FightType.AMATEUR, ParserUtils.getFightType(sherdog, fight));

        fight = event.getFights().get(1);
        assertEquals(FightType.PRO, ParserUtils.getFightType(sherdog, fight));


        //test fight that is failing in MMATH
        event = sherdog.getEvent("https://www.sherdog.com/events/Invicta-FC-2-Baszler-vs-McMann-22035");
        fight = event.getFights().get(1);
        assertEquals(FightType.PRO, ParserUtils.getFightType(sherdog, fight));

    }


    /**
     * Sherdog recently changed this so testing it
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testFightResults() throws IOException, ParseException, SherdogParserException {

        //from fighters
        Fighter fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Matt-Baker-49956");

        assertEquals(FightResult.NO_CONTEST, fighter.getFights().get(18).getResult());


        fighter = sherdog.getFighter("https://www.sherdog.com/fighter/Mark-Hunt-10668");
        assertEquals(FightResult.DRAW, fighter.getFights().get(17).getResult());
        assertEquals(FightResult.FIGHTER_2_WIN, fighter.getFights().get(0).getResult());
        assertEquals(FightResult.FIGHTER_1_WIN, fighter.getFights().get(1).getResult());


        //from events
        Event event = sherdog.getEvent("https://www.sherdog.com/events/WSOF-18-Moraes-vs-Hill-43147");
        Fight fight = event.getFights().get(6);
        assertEquals(FightResult.NO_CONTEST, fight.getResult());
        assertEquals(FightResult.FIGHTER_1_WIN, event.getFights().get(0).getResult());


        //draw
        event = sherdog.getEvent("https://www.sherdog.com/events/UFC-Fight-Night-33-Hunt-vs-Bigfoot-32293");
        assertEquals(FightResult.DRAW, event.getFights().get(0).getResult());

    }

}
