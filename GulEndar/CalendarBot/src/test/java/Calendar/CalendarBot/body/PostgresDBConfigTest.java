package Calendar.CalendarBot.body;

import Calendar.CalendarBot.entities.Event;
import org.junit.jupiter.api.*;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.SQLException;
import java.util.ArrayList;

public class PostgresDBConfigTest {

    PostgresDBAdapter dbAdapter;
    ArrayList<Event> expectedEvents;

    @BeforeAll
   static  void initializeAll(){
        PostgresDBAdapter.setTestMode();
        try {
            PostgresDBAdapter.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Database adapter test started");
    }

    @AfterAll
    static void endTest(){
        System.out.println("Database adapter test finished");
    }

    @AfterEach
    void clearAll(){
        ArrayList<Event> events;
        try {
            events = dbAdapter.getAllEvents("1000");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Event e:events){
            try {
                dbAdapter.deleteByID(e.getId());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @BeforeEach
    void initializeMes(){
        expectedEvents = new ArrayList<>();
        dbAdapter = new PostgresDBAdapter();
        PostgresDBAdapter.setTestMode();
        String chatId = String.valueOf(1000);
        try {
            expectedEvents.add(new Event("1000", "Event1",5));
            expectedEvents.add(new Event("1000", "Event2",10));
            expectedEvents.add(new Event("1000", "Event3",30));
            expectedEvents.add(new Event("1000", "Event4",120));
            dbAdapter.addEvent(chatId,expectedEvents.get(0));
            dbAdapter.addEvent(chatId,expectedEvents.get(1));
            dbAdapter.addEvent(chatId,expectedEvents.get(2));
            dbAdapter.addEvent(chatId,expectedEvents.get(3));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllEventsMethod() {
        String chatId = String.valueOf(1000);
        ArrayList<Event> list;
        try {
            list = dbAdapter.getAllEvents(chatId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String expected = expectedEvents.toString();
        String get = list.toString();
        Assertions.assertEquals(expected, get);

    }

    @Test
    void testGetByDateMethods() {
        String chatId = String.valueOf(1000);
        ArrayList<Event> list;
        try {
            list = dbAdapter.getEventsByDate(chatId,expectedEvents.get(0).getDateTime().toLocalDate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String expected = expectedEvents.toString();
        String get = list.toString();
        Assertions.assertEquals(expected,get);
    }

    @Test
    void testGetByDescriptionMethods() {
        String chatId = String.valueOf(1000);
        ArrayList<Event> list;
        try {
            list = dbAdapter.getEventsByText(chatId,expectedEvents.get(0).getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String expected = expectedEvents.get(0).toString();
        String get = list.get(0).toString();
        Assertions.assertEquals(expected, get);
    }

}
