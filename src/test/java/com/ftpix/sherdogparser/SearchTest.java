package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.models.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SearchTest {
    private static Sherdog sherdog;

    @BeforeClass
    public static void setup() {
        sherdog = new Sherdog.Builder().withTimezone("Asia/Kuala_Lumpur").build();
    }


    // Ignoring search tests as results varies too much breaking the build to often.
    @Ignore
    @Test
    public void testSearch() throws IOException {
        SearchResults jon = sherdog.search("jon")
                .query();


        List<SherdogBaseObject> fighters = jon.getFighters();
        List<SherdogBaseObject> events = jon.getEvents();
        assertEquals(20, fighters.size());
        assertEquals(1, events.size());

        List<Fighter> hydratedFighters = jon.getFightersWithCompleteData();
        assertEquals(0, hydratedFighters.get(0).getWins());
        assertEquals(3, hydratedFighters.get(0).getLosses());
        assertEquals("Aaron Jones", hydratedFighters.get(0).getName());


        List<Event> hydratedEvents = jon.getEventsWithCompleteData();
        assertEquals(8, hydratedEvents.get(0).getFights().size());


        SearchResults nextPage = jon.nextPage();
        fighters = jon.getFighters();
        events = jon.getEvents();
        assertEquals(20, fighters.size());
        assertEquals(0, events.size());

        hydratedFighters = jon.getFightersWithCompleteData();
        assertEquals(3, hydratedFighters.get(0).getWins());
        assertEquals(2, hydratedFighters.get(0).getLosses());
        assertEquals("Alimjon Shadmanov", hydratedFighters.get(0).getName());


    }

    @Ignore
    @Test
    public void searchWithWeightClass() throws IOException {

        SearchResults jon = sherdog.search("alistair overeem")
                .withWeightClass(SearchWeightClass.WELTERWEIGHT)
                .query();

        assertEquals(0, jon.getFighters().size());
        assertEquals(0, jon.getEvents().size());


        jon = sherdog.search("alistair overeem")
                .withWeightClass(SearchWeightClass.HEAVYWEIGHT)
                .query();

        assertEquals(1, jon.getFighters().size());
        assertEquals(0, jon.getEvents().size());

    }
}
