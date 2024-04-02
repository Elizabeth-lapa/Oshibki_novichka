package Calendar.CalendarBot.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.TelegramBotBody;
import Calendar.CalendarBot.body.handlers.CallbackQueryHandler;
import Calendar.CalendarBot.body.handlers.MessageHandler;
import Calendar.CalendarBot.config.BotConfig;
import Calendar.CalendarBot.entities.Event;
import org.junit.jupiter.api.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MessageHandlerTest {

    HashMap<String, Event> usersIvents;
    HashMap<String, Event> usersEventInMemory;
    HashMap<String, String> usersLastMessages;
    PostgresDBAdapter dbAdapter;
    MessageHandler messageHandler;
    TelegramBotBody botBody;
    Message message;

    ArrayList<Event> expectedEvents;

    @BeforeEach
    void initializeAll(){
        expectedEvents = new ArrayList<>();
        usersEventInMemory = new HashMap<>();
        usersIvents = new HashMap<>();
        usersLastMessages = new HashMap<>();
        dbAdapter = new PostgresDBAdapter();
        botBody = new TelegramBotBody(new BotConfig());
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
        messageHandler = new MessageHandler(usersIvents, botBody, usersLastMessages, usersEventInMemory);
        message = new Message();
        message.setFrom(new User(1093937171L, "Дмитрий", false));
        message.setChat(new Chat(1093937171L, "f"));
    }

    @BeforeAll
    static void beforeTest(){
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

    @Test
    void testGetAllEventsMethod() {
        String chatId = message.getChatId().toString();
        message.setText("Все события");
        String expected = messageHandler.getAllEvents(chatId).toString();
        String get = messageHandler.answerMessage(message).toString();
        Assertions.assertEquals(expected, get);

    }

    @Test
    void testGetMethods() {
        String chatId = message.getChatId().toString();
        usersLastMessages.put(chatId, "Найти по описанию");
        messageHandler.setUsersLastMessages(usersLastMessages);
        message.setText("3");
        String expected = messageHandler.findEventsByText(chatId, "3").toString();
        String get = messageHandler.answerMessage(message).toString();
        Assertions.assertEquals(expected,get);
    }
}