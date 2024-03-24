package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.TelegramBotBody;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


@Component
public class MessageHandler {
    TelegramBotBody botBody;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    HashMap<String, Event> usersIvents;
    HashMap<String, String> usersLastMessages;
    org.slf4j.Logger logger;

    PostgresDBAdapter dbAdapter;
    public MessageHandler(HashMap<String, Event> usersIvents, TelegramBotBody botBody,HashMap<String, String> usersLastMessages){
        this.botBody = botBody;
        dbAdapter = new PostgresDBAdapter();
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
        this.usersLastMessages = usersLastMessages;
        this.usersIvents = usersIvents;
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
    }

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();

        String messageText = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String answerText;

        System.out.println(messageText);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        //TODO Добавить отправку фото
        //TODO Map для разных чатов
        //"https://proprikol.ru/wp-content/uploads/2021/08/kartinki-tokijskij-gul-11.jpg"

        //sendPhoto.setPhoto();

        if (!usersIvents.containsKey(chatId)) usersIvents.put(chatId,new Event());
        if (!usersLastMessages.containsKey(chatId)) usersLastMessages.put(chatId,"start");
        if (messageText == null) {
            throw new IllegalArgumentException();
        }

        switch (messageText){
            case "/start":
                if (!usersIvents.containsKey(chatId)) usersIvents.put(chatId,new Event());
                if (!usersLastMessages.containsKey(chatId)) usersLastMessages.put(chatId,"start");
                 answerText = "Здравствуйте, " + message.getChat().getFirstName();
            sendMessage.setText(answerText);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getCalendarMainMenuKeyboard());
            return sendMessage;
            case "/гуль":
                sendMessage.setText("Who are you?(Кто вы по жизни?)");
                sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
                return sendMessage;
            case "Я котик чипи чипи":
                sendMessage.setText("Поздравляю, теперь вы котик чипи чипи");
                sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons("/"));
                return sendMessage;
            case "Я котик happy happy":
                sendMessage.setText("Поздравляю, теперь вы котик happy happy");
                sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons("/"));
                return sendMessage;
            case "Я ГУЛЬ":
                return ghoulCommandReceived(chatId);
            case "Я человек":
                sendMessage.setText("Круто");
                return sendMessage;
            case "Календарь":
                sendMessage.setText("Переключаюсь в режим календаря");
                sendMessage.setReplyMarkup(replyKeyboardMaker.getCalendarMainMenuKeyboard());
                return sendMessage;
            case "Создать событие":
                sendMessage.setText("Выберите месяц");
                sendMessage.setReplyMarkup(inlineKeyboardMaker.getCalendarMonthsButtons("/"));
                usersIvents.get(chatId).clear();
                usersLastMessages.put(chatId,messageText);
                return sendMessage;
            case "События сегодня":
                return getTodayEvents(chatId);
            case "Вывести события":
                return getAllEvents(chatId);
            case "Найти события":
                usersLastMessages.put(chatId, messageText);
                sendMessage.setText("Введите несколько букв, слово или несколько слов из описания события");
                return sendMessage;
            default:
                switch (usersLastMessages.get(chatId)){
                    case "Найти события":
                        return findEventsByText(chatId, messageText);
                }
                if(usersIvents.get(chatId).getText().isEmpty()) {
                    if (usersLastMessages.get(chatId).equals("edit")){
                        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                        dbAdapter.insertInto("text", messageText, usersIvents.get(chatId).getId());
                        return new SendMessage(chatId, "Изменено");
                    }
                    usersIvents.get(chatId).setText(messageText);
                    sendMessage.setReplyMarkup(inlineKeyboardMaker.getDefaultDurationButtons("/"));
                    sendMessage.setText("Введите длительность(мин)");
                    return sendMessage;
                }
                if(usersIvents.get(chatId).getDuration() < 0) {
                    int duration = 0;
                    try {
                        duration = Integer.parseInt(messageText);
                    }catch (NumberFormatException e){
                        sendMessage.setText("Введите длительность(число, равное длительности события в минутах)");
                        return sendMessage;
                    }
                    usersIvents.get(chatId).setDuration(duration);
                    if (usersLastMessages.get(chatId).equals("edit")){
                        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                        dbAdapter.insertInto("duration", duration, usersIvents.get(chatId).getId());
                        return new SendMessage(chatId, "Изменено");
                    }
                    try {
                        dbAdapter.addEvent(chatId,usersIvents.get(chatId));
                    } catch (SQLException e) {
                        logger.error("Ошибка при вызове addEvent",e);
                        throw new RuntimeException(e);
                    }
                    usersIvents.get(chatId).clear();
                    return new SendMessage(chatId,"Событие создано");
                }
                break;
        }
        return new SendMessage(chatId,"Нет такой команды");
    }

    private SendMessage ghoulCommandReceived(String chatId) {
        String answer = "";

        for (int i = 1000; i > 0; i = i - 7){
            answer = answer + Integer.toString(i) + "-7=" + Integer.toString(i-7) + "\n";
        }

        return new SendMessage(chatId, answer);
    }

    private SendMessage getTodayEvents(String chatId) {
        String response = "";
        Iterable<Event> events = null;
        try {
            events = dbAdapter.getTodayEvents(LocalDate.now());
        } catch (SQLException e) {
            System.err.println("Error in handler");
            throw new RuntimeException(e);
        }
        for (Event e:events) {
            response = response + e.toString() + "\n";
        }
        if (response.isEmpty()) response = "Список событий на сегодня пуст";
        SendMessage sendMessage = new SendMessage(chatId, response);
        return sendMessage;
    }

    private SendMessage getAllEvents(String chatId) {
        Iterable<Event> events = null;
        try {
            events = dbAdapter.getAllEvents();
        } catch (SQLException e) {
            System.err.println("Error in handler");
        }
        String response = "";

        for (Event e:events) {
            response = response + e.toString() + "\n";
        }
        if (response.isEmpty()) response = "Список событий пуст";
        SendMessage sendMessage = new SendMessage(chatId, response);
        return sendMessage;
    }

    private SendMessage findEventsByText(String chatId, String text) {
        String response = "";
        Iterable<Event> events = null;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.findEventsByText(text);
        } catch (SQLException e) {
            logger.error("Text message handler error:", e);
            throw new RuntimeException(e);
        }
        for (Event e:events) {
            SendMessage sendMessage = new SendMessage(chatId,e.toString());
            sendMessage.setReplyMarkup(inlineKeyboardMaker.getEventActionsButtons("/",e.getId()));
            botApiMethods.add(sendMessage);
        }
        botBody.executeMethods(botApiMethods);
        response = "Выберите действие";
        if (botApiMethods.isEmpty()) response = "Не найдено событий";
        SendMessage sendMessage = new SendMessage(chatId, response);
        return sendMessage;

    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Button");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }
}
