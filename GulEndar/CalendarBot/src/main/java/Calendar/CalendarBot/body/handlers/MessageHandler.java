package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.TelegramBotBody;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


@Component
public class MessageHandler {
    TelegramBotBody botBody;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    HashMap<String, Event> usersIvents;
    HashMap<String, String> usersLastMessages;
    HashMap<String, Event> usersEventInMemory;

    org.slf4j.Logger logger;

    PostgresDBAdapter dbAdapter;
    public MessageHandler(HashMap<String, Event> usersIvents, TelegramBotBody botBody,HashMap<String, String> usersLastMessages,HashMap<String, Event> usersEventInMemory ){
        this.botBody = botBody;
        this.usersEventInMemory = usersEventInMemory;
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


        if (!usersIvents.containsKey(chatId)) usersIvents.put(chatId,new Event());
        if (!usersLastMessages.containsKey(chatId)) usersLastMessages.put(chatId,"start");
        if (messageText == null) {
            throw new IllegalArgumentException();
        }

        switch (messageText){
            case "/start":
                if (!usersIvents.containsKey(chatId)) usersIvents.put(chatId,new Event());
                if (!usersLastMessages.containsKey(chatId)) usersLastMessages.put(chatId,"start");
                answerText = "Здравствуйте, " + message.getChat().getFirstName() + ". Наш бот - это современный календарь-бот, он может быть всем: "
                        +"от простого списка, до персонального менеджера. Только в телеграм.";
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
            case "События завтра":
                return getTomorrowEvents(chatId);
            case "Найти по дате":
                usersLastMessages.put(chatId,messageText);
                sendMessage.setText("Введите дату в формате дд, дд.мм или дд.мм.гггг. \n+В 1-м и 2-м шаблоне месяц и год автоматически заменяются текущими."
                        + "\n+Поставьте знак < или > перед датой, чтобы найти события до/после введеной даты.");
                return sendMessage;
            case "Все события":
                return getAllEvents(chatId);
            case "Найти по описанию":
                usersLastMessages.put(chatId, messageText);
                sendMessage.setText("Введите несколько букв, слово или несколько слов из описания события");
                return sendMessage;
            default:
                switch (usersLastMessages.get(chatId)){
                    case "Найти по описанию":
                        return findEventsByText(chatId, messageText);
                    case "Найти по дате":
                        messageText = messageText.trim();
                        if (messageText.startsWith("<")){
                            return findEventsBeforeDate(chatId, messageText.substring(messageText.indexOf('<') + 1).trim());
                        } else if (messageText.startsWith(">")) {
                            return findEventsAfterDate(chatId, messageText.substring(messageText.indexOf('>') + 1).trim());
                        }
                        return findEventsByDate(chatId, messageText);
                }
                if(usersIvents.get(chatId).getText().isEmpty()) {
                    if (usersLastMessages.get(chatId).equals("edit")){
                        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                        try {

                            dbAdapter.insertInto("text", messageText, usersIvents.get(chatId).getId());
                        } catch(SQLException e){
                            logger.error("can not update field in database: ", e);
                            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
                        }
                        return new SendMessage(chatId, "Изменено");
                    }
                    usersIvents.get(chatId).setText(messageText);
                    sendMessage.setReplyMarkup(inlineKeyboardMaker.getDefaultDurationButtons("/"));
                    sendMessage.setText("Выберите длительность");
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
                    //TODO сюда проверку конфликтов
                    ArrayList<Event> events;
                    try {
                        events = haveConflicts(chatId);
                    }catch (SQLException e){
                        sendMessage.setText("Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
                        return sendMessage;
                    }
                    if (!events.isEmpty() && (usersIvents.get(chatId).getDuration() != 0)){
                        usersEventInMemory.put(chatId,usersIvents.get(chatId));
                        return handleConflicts(chatId,events);
                    }
                    if (usersLastMessages.get(chatId).equals("edit")){
                        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                        try {
                            dbAdapter.insertInto("duration", duration, usersIvents.get(chatId).getId());
                        } catch(SQLException e){
                            logger.error("can not update field in database: ", e);
                            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
                        }
                        return new SendMessage(chatId, "Изменено");
                    }
                    try {
                        dbAdapter.addEvent(chatId,usersIvents.get(chatId));
                    } catch(SQLException e){
                        logger.error("can not insert data into database: ", e);
                        return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
                    }
                    String event = usersIvents.get(chatId).toString();
                    usersIvents.get(chatId).clear();
                    return new SendMessage(chatId,"Событие создано:\n " + event);
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
            events = dbAdapter.getEventsByDate(chatId,LocalDate.now());
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        response = eventsToString(events);
        if (response.isEmpty()) response = "Список событий на сегодня пуст";
        SendMessage sendMessage = new SendMessage(chatId, response);
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private SendMessage getTomorrowEvents(String chatId) {
        String response = "";
        Iterable<Event> events = null;
        try {
            events = dbAdapter.getEventsByDate(chatId, LocalDate.now().plusDays(1));
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        response = eventsToString(events);
        if (response.isEmpty()) response = "Список событий на завтра пуст";
        SendMessage sendMessage = new SendMessage(chatId, response);
        sendMessage.enableMarkdown(true);

        return sendMessage;
    }

    public SendMessage getAllEvents(String chatId) {
        Iterable<Event> events = null;
        try {
            events = dbAdapter.getAllEvents(chatId);
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        String response = "";
        response = eventsToStringWithDuration(events);

        if (response.isEmpty()) response = "Список событий пуст";
        SendMessage sendMessage = new SendMessage(chatId, response);
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private String eventsToString(Iterable<Event> events){
        String response = "";
        int currentMonth = 0;
        int currentDay = 0;

        for (Event e:events) {
            if (e.getDateTime().getDayOfMonth() != currentDay){
                if(e.getDateTime().getMonthValue() != currentMonth){
                    currentMonth = e.getDateTime().getMonthValue();
                }
                currentDay = e.getDateTime().getDayOfMonth();
                Month jan = Month.of(currentMonth);
                jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"));
                LocalDate currentDate = LocalDate.now();
                if(currentDate.getDayOfMonth() == currentDay && currentDate.getMonthValue() == currentMonth) response = response +"\n*"+ jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))+ " " +currentDay  + "*\n";
                else response = response +"\n"+ jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))+ " " +currentDay  + "\n";
            }

            if(e.getDateTime().toLocalDate().equals(e.getDateTime().plusMinutes(e.getDuration()).toLocalDate())) response = response + e.toStringWithoutDuration() + "\n";
            else response = response + e.toStringStartAndEndDates() + "\n";
        }
        return response;
    }

    private String eventsToStringWithDuration(Iterable<Event> events){
        String response = "";
        int currentMonth = 0;
        int currentDay = 0;

        for (Event e:events) {
            if (e.getDateTime().getDayOfMonth() != currentDay){
                if(e.getDateTime().getMonthValue() != currentMonth){
                    currentMonth = e.getDateTime().getMonthValue();
                }
                currentDay = e.getDateTime().getDayOfMonth();
                Month jan = Month.of(currentMonth);
                jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"));
                LocalDate currentDate = LocalDate.now();
                if(currentDate.getDayOfMonth() == currentDay && currentDate.getMonthValue() == currentMonth)response = response +"\n*_"+ jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))+ " " +currentDay  + "*\n";
                else response = response +"\n"+ jan.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))+ " " +currentDay  + "\n";
            }
            if(e.getDateTime().toLocalDate().equals(e.getDateTime().plusMinutes(e.getDuration()).toLocalDate())) response = response + e.toStringWithoutDate() + "\n";
            else response = response + e.toStringStartAndEndDates() + "\n";
        }
        return response;
    }

    public SendMessage findEventsByText(String chatId, String text) {
        String response = "";
        Iterable<Event> events;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.getEventsByText(chatId,text);
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
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

    private SendMessage findEventsByDate(String chatId, String text) {
        String response = "";
        LocalDate dateTime;
        try {
            dateTime = stringToDate(text);
        }catch (NumberFormatException e) {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Дата не соответствует шаблону или выходит за допустимые границы");
            return sm;
        }
        Iterable<Event> events = null;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.getEventsByDate(chatId, dateTime);
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
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

    private SendMessage findEventsBeforeDate(String chatId, String text) {
        String response = "";
        LocalDate dateTime;
        try {
            dateTime = stringToDate(text);
        }catch (NumberFormatException e) {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Дата не соответствует шаблону или выходит за допустимые границы");
            return sm;
        }
        Iterable<Event> events = null;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.getEventsBeforeOrAfterDate(chatId,dateTime, "<");
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
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

    private SendMessage findEventsAfterDate(String chatId, String text) {
        String response = "";
        LocalDate dateTime;
        try {
            dateTime = stringToDate(text);
        }catch (NumberFormatException e) {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Дата не соответствует шаблону или выходит за допустимые границы");
            return sm;
        }
        Iterable<Event> events = null;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.getEventsBeforeOrAfterDate(chatId,dateTime, ">");
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
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

    private LocalDate stringToDate(String text) throws NumberFormatException{
        LocalDate dateTime = LocalDate.now();
        if (text.isEmpty()) return dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            dateTime = LocalDate.parse(text, formatter);
        }catch (DateTimeParseException e){
            String withYearAdded = "";
            withYearAdded = text + "." + dateTime.getYear();
            try{
                dateTime = LocalDate.parse(withYearAdded, formatter);
            }catch (DateTimeParseException e2) {
                try{
                    dateTime = dateTime.withDayOfMonth(Integer.parseInt(text));
                } catch (NumberFormatException e3) {
                    throw e3;
                }
            }
        }
        return dateTime;
    }

    private ArrayList<Event> haveConflicts(String chatId) throws SQLException{
        ArrayList<Event> events = null;
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        try {
            events = dbAdapter.getConflictsBefore(chatId,usersIvents.get(chatId).getDateTime());
            events.addAll(dbAdapter.getConflictsBetween(chatId,usersIvents.get(chatId).getDateTime(),usersIvents.get(chatId).getDateTime().plusMinutes(usersIvents.get(chatId).getDuration())));
        } catch(SQLException e){
            logger.error("can not get records from database: ", e);
            throw e;
        }

        return events;
    }

    private SendMessage handleConflicts(String chatId,ArrayList<Event> events){
        String response = "";
        ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
        for (Event e:events) {
            SendMessage sendMessage = new SendMessage(chatId,e.toStringStartAndEndDates());
            sendMessage.setReplyMarkup(inlineKeyboardMaker.getEventActionsButtons("/",e.getId()));
            botApiMethods.add(sendMessage);
        }
        botBody.executeMethods(botApiMethods);
        response = "Событие вызывает конфликты\n" + usersIvents.get(chatId).toStringStartAndEndDates() + "\n";
        response = response + "Разрешите конфликты вручную и попробуйте снова или выберите одно из других действий:";
        if (botApiMethods.isEmpty()) response = "Не найдено конфликтов";
        SendMessage sendMessage = new SendMessage(chatId, response);
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getConflictsSolveButtons("/"));
        return sendMessage;
    }

    public void setUsersLastMessages(HashMap<String, String> usersLastMessages){
        this.usersLastMessages = usersLastMessages;
    }
}
