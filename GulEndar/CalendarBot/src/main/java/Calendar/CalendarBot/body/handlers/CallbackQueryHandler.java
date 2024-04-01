package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.TelegramBotBody;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;
import java.security.interfaces.EdECKey;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

//TODO Изменение дней в месяце при изменении месяца
//TODO корректный вывод событий, которые длятся несколько дней
@Component
public class CallbackQueryHandler {

    HashMap<String, Event> usersIvents;
    HashMap<String, Event> usersEventInMemory;
    HashMap<String, String> usersLastMessages;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    TelegramBotBody botBody;

    org.slf4j.Logger logger;

    public CallbackQueryHandler(HashMap<String, Event> usersIvents, TelegramBotBody botBody, HashMap<String, String> usersLastMessages, HashMap<String, Event> usersEventInMemory){
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
        this.botBody = botBody;
        this.usersEventInMemory = usersEventInMemory;
        this.usersIvents = usersIvents;
        this.usersLastMessages = usersLastMessages;
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
    }



    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();
        String mode;
        if (data.indexOf(':') == -1) mode = data.substring(data.indexOf('/') + 1);
        else mode = data.substring(data.indexOf('/') + 1, data.indexOf(':'));
        System.out.println(mode);
        switch (mode){
            case "day":
                return dayHandle(chatId,data);
            case "delete":
                return deleteHandle(chatId,data);
            case "month":
                return monthHandle(chatId,data);
            case "hour":
                return hourHandle(chatId,data);
            case "minutesRange":
                int minutesRange = Integer.parseInt(data.substring(data.indexOf(':') + 1));
                SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                sm.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/", minutesRange));
                sm.setText("Выберите минуты");
                return sm;
            case "minute":
                return minuteHandle(chatId,data);
            case "duration":
                return durationHandle(chatId,data);
            case "edit":
                return editHandle(chatId,data);
            case "ignore":
                return ignoreHandle(chatId);
            case "tryagain":
                return tryagainButtonHandle(chatId);
            case "autoresolve":
                return autoresolveButtonHandle(chatId);
        }

        switch (data) {
            case "/button1":
                return new SendMessage(chatId, "Вы порадовались");
            case "/button 2":
                return new SendMessage(chatId, "Вы не порадовались");
            default:
                return new SendMessage(chatId, "Ошибка");
        }
    }
    private SendMessage dayHandle(String chatId, String data){
        int day = Integer.parseInt(data.substring(5));
        try{
            usersIvents.get(chatId).setDayOfMonth(day);
        }catch (DateTimeException e) {
            return new SendMessage(chatId,"Неправильный формат дня");
        }
        if (usersLastMessages.get(chatId).equals("edit")){
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
            }catch(SQLException e){
                logger.error("can not update field in database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            return new SendMessage(chatId, "Изменено");
        }
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setReplyMarkup(inlineKeyboardMaker.getCalendarHourButtons("/"));
        sm.setText("Выберите час");
        return sm;

    }

    private SendMessage deleteHandle(String chatId, String data){
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        String id = data.substring(data.indexOf(':') + 1);
        try {
            dbAdapter.deleteByID(id);
        }catch(SQLException e){
            logger.error("can not delete field from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Событие удалено");
        return sm;

    }

    private SendMessage monthHandle(String chatId, String data){
        int month = Integer.parseInt(data.substring(7));
        try{
            usersIvents.get(chatId).setMonth(month);
        }catch (DateTimeException e) {
            return new SendMessage(chatId,"Неправильный формат месяца");
        }
        if (usersLastMessages.get(chatId).equals("edit")){
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
            }catch(SQLException e){
                logger.error("can not update field in database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            return new SendMessage(chatId, "Изменено");
        }
        SendMessage sm = new SendMessage();
        sm.setReplyMarkup(inlineKeyboardMaker.getCalendarDaysButtons("/",
                usersIvents.get(chatId).getDateTime().getMonth().length(true)));
        sm.setChatId(chatId);
        sm.setText("Выберите день");
        return sm;

    }

    private SendMessage hourHandle(String chatId, String data){
        int hour = Integer.parseInt(data.substring(6));
        try {
            usersIvents.get(chatId).setHour(hour);
        } catch (DateTimeException e) {
            return new SendMessage(chatId, "Неправильный формат часа");
        }
        if (usersLastMessages.get(chatId).equals("edit")) {
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
            } catch (SQLException e) {
                logger.error("can not update field in database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            return new SendMessage(chatId, "Изменено");
        }
        SendMessage sm = new SendMessage();
        sm.setReplyMarkup(inlineKeyboardMaker.getMinutesRangeButtons("/"));
        sm.setChatId(chatId);
        sm.setText("Выберите диапазон минут");
        return sm;
    }

    private  SendMessage minuteHandle(String chatId, String data){
        int minute = Integer.parseInt(data.substring(data.indexOf(':') + 1));
        try{
            usersIvents.get(chatId).setMinute(minute);
        }catch (DateTimeException e) {
            return new SendMessage(chatId,"Неправильный формат минут");
        }
        if (usersLastMessages.get(chatId).equals("edit")){
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
            }catch(SQLException e){
                logger.error("can not update field in database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            return new SendMessage(chatId, "Изменено");
        }
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Введите описание");
        return sm;
    }

    private SendMessage durationHandle(String chatId, String data){
        int duration = Integer.parseInt(data.substring(data.indexOf(':') + 1));
        usersIvents.get(chatId).setDuration(duration);
        if (duration == -1) {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Введите длительность(мин)");
            return sm;
        }
        ArrayList<Event> events;
        try {
            events = haveConflicts(chatId);
        }catch (SQLException e){
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            return sm;
        }
        if (!events.isEmpty() && (usersIvents.get(chatId).getDuration() != 0)){
            //if there are conflicts
            usersEventInMemory.put(chatId,usersIvents.get(chatId));
            return handleConflicts(chatId,events);
        }


        if (usersLastMessages.get(chatId).equals("edit")){
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.insertInto("duration", duration, usersIvents.get(chatId).getId());
            }catch(SQLException e){
                logger.error("can not update field in database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            return new SendMessage(chatId, "Изменено");
        }

        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();

        try {
            dbAdapter.addEvent(chatId,usersIvents.get(chatId));
        } catch(SQLException e){
            logger.error("can not insert data in database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        String event = usersIvents.get(chatId).toString();
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Событие создано:\n " + event);
        return sm;

    }

    private ArrayList<Event> haveConflicts(String chatId) throws SQLException{
        ArrayList<Event> events;
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
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

    private SendMessage editHandle(String chatId, String data){
        usersLastMessages.put(chatId,"edit");
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        String id = data.substring(data.indexOf(':') + 1);
        if (id.indexOf(':') != -1){
            id.substring(id.indexOf(':') + 1);
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                usersIvents.get(chatId).setEvent(dbAdapter.getByID(id.substring(id.indexOf(':') + 1)));
            } catch(SQLException e){
                logger.error("can not get records from database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
            System.out.println(id.substring(0,id.indexOf(':')));
            switch (id.substring(0,id.indexOf(':'))){
                case "date":
                    System.out.println(chatId);
                    //dbAdapter.insertInto();
                    ArrayList<BotApiMethod<?>> botApiMethods = new ArrayList<>();
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Предыдущая дата: " + usersIvents.get(chatId).getDateTime().getDayOfMonth() + " " +usersIvents.get(chatId).getDateTime().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")));
                    sendMessage.setChatId(chatId);
                    botApiMethods.add(sendMessage);
                    sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(inlineKeyboardMaker.getCalendarMonthsButtons("/"));
                    sendMessage.setText("Изменить месяц:");
                    botApiMethods.add(sendMessage);
                    sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(inlineKeyboardMaker.getCalendarDaysButtons("/",
                            usersIvents.get(chatId).getDateTime().getMonth().length(false)));
                    sendMessage.setText("Изменить день:");
                    botBody.executeMethods(botApiMethods);
                    return sendMessage;
                case "time":
                    ArrayList<BotApiMethod<?>> botApiMethodsTime = new ArrayList<>();
                    SendMessage sendMessageTime = new SendMessage();
                    sendMessageTime.setText("Предыдущее время: " + usersIvents.get(chatId).getDateTime().getHour() + ":" + usersIvents.get(chatId).getDateTime().getMinute());
                    sendMessageTime.setChatId(chatId);
                    botApiMethodsTime.add(sendMessageTime);
                    sendMessageTime = new SendMessage();
                    sendMessageTime.setChatId(chatId);
                    sendMessageTime.setReplyMarkup(inlineKeyboardMaker.getCalendarHourButtons("/"));
                    sendMessageTime.setText("Изменить часы:");
                    botApiMethodsTime.add(sendMessageTime);
                    sendMessageTime = new SendMessage();
                    sendMessageTime.setChatId(chatId);
                    sendMessageTime.setReplyMarkup(inlineKeyboardMaker.getMinutesRangeButtons("/"));
                    sendMessageTime.setText("Изменить минуты:");
                    botBody.executeMethods(botApiMethodsTime);
                    return sendMessageTime;
                case "duration":
                    SendMessage sendMessageDuration = new SendMessage();
                    sendMessageDuration.setReplyMarkup(inlineKeyboardMaker.getDefaultDurationButtons("/"));
                    sendMessageDuration.setText("Заменить длительность " + usersIvents.get(chatId).getDuration() +" на ...");
                    usersIvents.get(chatId).setDuration(-1);
                    sendMessageDuration.setChatId(chatId);
                    return sendMessageDuration;
                case "text":
                    SendMessage sendMessageTextEdit = new SendMessage();
                    sendMessageTextEdit.setText("Предыдущее описание: " + usersIvents.get(chatId).getText() +"\n Введите новое описание");
                    usersIvents.get(chatId).setText("");
                    sendMessageTextEdit.setChatId(chatId);
                    return sendMessageTextEdit;
            }
        }

        sm.setReplyMarkup(inlineKeyboardMaker.getEditButtons("/", id));
        sm.setText("Что бы вы хотели изменить?");
        return sm;
    }

    private SendMessage ignoreHandle(String chatId){
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        try {
            dbAdapter.addEvent(chatId,usersEventInMemory.get(chatId));
        } catch(SQLException e){
            logger.error("can not insert data into database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        String event = usersIvents.get(chatId).toString();
        usersIvents.get(chatId).clear();
        return new SendMessage(chatId,"Событие создано:\n " + event);
    }

    private SendMessage tryagainButtonHandle(String chatId){
        ArrayList<Event> events;
        try {
            events = haveConflicts(chatId);
        }catch (SQLException e){
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            return sm;
        }
        if (!events.isEmpty()){
            //if there are conflicts
            usersEventInMemory.put(chatId,usersIvents.get(chatId));
            return handleConflicts(chatId,events);
        }


        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        try {
            dbAdapter.addEvent(chatId,usersIvents.get(chatId));
        } catch(SQLException e){
            logger.error("can not insert data in database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        String event = usersIvents.get(chatId).toString();
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Событие создано:\n " + event);
        return sm;
    }

    private SendMessage autoresolveButtonHandle(String chatId){
        int minutesInDay = 1440;
        if(usersEventInMemory.get(chatId).getDuration() > minutesInDay){
            SendMessage sm = new SendMessage(chatId, "Невозможно автоматически разрешить конфликты с событием, которое длится более суток(1440 минут)");
            return sm;
        }

        try{
            solveBeforeConflicts(chatId);
        } catch(SQLException e){
            logger.error("can not get data from database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        logger.info("Conflicts before solved");
        usersIvents.put(chatId, usersEventInMemory.get(chatId));
        ArrayList<Event> events;
        try {
            events = haveConflicts(chatId);
        }catch (SQLException e){
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            return sm;
        }
        logger.info(usersIvents.toString());
        logger.info("first autoresolve completed" + events);
        if (!events.isEmpty() ){
            try{
                solveAfterConflicts(chatId);
            } catch(SQLException e){
                logger.error("can not get data from database: ", e);
                return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
            }
        }
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        try {
            dbAdapter.addEvent(chatId,usersEventInMemory.get(chatId));
        } catch(SQLException e){
            logger.error("can not insert data into database: ", e);
            return new SendMessage(chatId, "Не удается подключиться к базе данных. Пожалуйста, попробуйте позже");
        }
        String event = usersIvents.get(chatId).toString();
        usersIvents.get(chatId).clear();
        return new SendMessage(chatId,"Событие создано:\n " + event);
    }

    private void solveAfterConflicts(String chatId) throws SQLException{
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        Event eventToAdd = usersEventInMemory.get(chatId);
        Event nextEvent = new Event();


        try{
            nextEvent = dbAdapter.getFirstAfterDate(chatId,eventToAdd.getDateTime(), "-1");
        } catch(SQLException e){
            logger.error("can not get data from database: ", e);
            throw e;
        }catch (IndexOutOfBoundsException e){
            try{
                dbAdapter.addEvent(chatId,eventToAdd);
            } catch(SQLException e2){
                logger.error("can not insert data in database: ", e);
                throw e;
            }
        }

        Duration difference = Duration.between(eventToAdd.getDateTime().plusMinutes(eventToAdd.getDuration()),nextEvent.getDateTime());
        long differenceMinutes = difference.toMinutes();
        logger.info("Conflicts after date solving...");
        System.out.println(differenceMinutes);
        if(differenceMinutes < 0){
            //if there is no space for the event
            Event currentEvent;
            HashMap<String, LocalDateTime> shiftEventOn = new HashMap<>();
            ArrayList<String> listOfEventsToShift = new ArrayList<>();
            shiftEventOn.put(nextEvent.getId(),nextEvent.getDateTime().plusMinutes(Math.abs(differenceMinutes)));
            listOfEventsToShift.add(nextEvent.getId());
            while(differenceMinutes < 0){
                currentEvent = nextEvent;
                try{
                    nextEvent = dbAdapter.getFirstAfterDate(chatId,currentEvent.getDateTime(), currentEvent.getId());
                } catch(SQLException e){
                    logger.error("can not get data from database: ", e);
                    throw e;
                }catch (IndexOutOfBoundsException e){
                    shiftEventOn.put(currentEvent.getId(), currentEvent.getDateTime().plusMinutes(Math.abs(differenceMinutes)));
                    listOfEventsToShift.add(currentEvent.getId());
                    differenceMinutes = 0;
                    continue;
                }
                System.out.println(nextEvent);
                difference = Duration.between(currentEvent.getDateTime().plusMinutes(currentEvent.getDuration()),nextEvent.getDateTime());
                //TODO добавить лист ивентов, которые перебрали или стразу сдвигать их
                differenceMinutes = differenceMinutes + difference.toMinutes();
                System.out.println(differenceMinutes);
                shiftEventOn.put(nextEvent.getId(), nextEvent.getDateTime().plusMinutes(Math.abs(differenceMinutes)));
                listOfEventsToShift.add(nextEvent.getId());
            }
            for(String id:listOfEventsToShift){
                try{
                    dbAdapter.insertInto(shiftEventOn.get(id), id);
                } catch(SQLException e){
                    logger.error("can not insert data in database: ", e);
                    throw e;
                }
            }
        }
    }

    private void solveBeforeConflicts(String chatId) throws SQLException{
        PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
        Event eventToAdd = usersEventInMemory.get(chatId);
        Event previousEvent;
        try{
            previousEvent = dbAdapter.getFirstBeforeDate(chatId,eventToAdd.getDateTime(), "-1");
        } catch(SQLException e){
            logger.error("can not get data from database: ", e);
            throw e;
        }

        Duration difference = Duration.between(eventToAdd.getDateTime(), previousEvent.getDateTime().plusMinutes(previousEvent.getDuration()));
        long differenceMinutes = difference.toMinutes() + 1;
        System.out.println(differenceMinutes);
        if(differenceMinutes > 0){
            //if there is no space for the event
            eventToAdd.setDateTime(eventToAdd.getDateTime().plusMinutes(differenceMinutes));
            usersEventInMemory.put(chatId,eventToAdd);
        }
    }
}
