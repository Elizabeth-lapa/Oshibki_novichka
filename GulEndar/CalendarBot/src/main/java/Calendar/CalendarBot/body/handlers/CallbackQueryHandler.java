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
import java.sql.SQLException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Component
public class CallbackQueryHandler {

    HashMap<String, Event> usersIvents;
    HashMap<String, String> usersLastMessages;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    TelegramBotBody botBody;

    org.slf4j.Logger logger;

    public CallbackQueryHandler(HashMap<String, Event> usersIvents, TelegramBotBody botBody, HashMap<String, String> usersLastMessages){
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
        this.botBody = botBody;
        this.usersIvents = usersIvents;
        this.usersLastMessages = usersLastMessages;
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
    }



    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();



        if (data.contains("/day:")) {
            int day = Integer.parseInt(data.substring(5));
            usersIvents.get(chatId).setDayOfMonth(day);
            if (usersLastMessages.get(chatId).equals("edit")){
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
                return new SendMessage(chatId, "Изменено");
            }
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarHourButtons("/"));
            sm.setText("Выберите час");
            return sm;

        } else if (data.contains("/delete:")) {
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                String id = data.substring(data.indexOf(':') + 1);
                dbAdapter.deleteByID(id);
            SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                sm.setText("Событие удалено");
                return sm;

            }else if (data.contains("/month:")) {
            int month = Integer.parseInt(data.substring(7));
            usersIvents.get(chatId).setMonth(month);
            if (usersLastMessages.get(chatId).equals("edit")){
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
                return new SendMessage(chatId, "Изменено");
            }
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarDaysButtons("/",
                    usersIvents.get(chatId).getDateTime().getMonth().length(false)));
            sm.setChatId(chatId);
            sm.setText("Выберите день");
            return sm;

        }else if (data.contains("/hour:")) {
            int hour = Integer.parseInt(data.substring(6));
            usersIvents.get(chatId).setHour(hour);
            if (usersLastMessages.get(chatId).equals("edit")){
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
                return new SendMessage(chatId, "Изменено");
            }
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/"));
            sm.setChatId(chatId);
            sm.setText("Выберите минуты");
            return sm;

        } else if (data.contains("/minute:")) {
            int minute = Integer.parseInt(data.substring(data.indexOf(':') + 1));
            usersIvents.get(chatId).setMinute(minute);
            if (usersLastMessages.get(chatId).equals("edit")){
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                dbAdapter.insertInto(usersIvents.get(chatId).getDateTime(), usersIvents.get(chatId).getId());
                return new SendMessage(chatId, "Изменено");
            }
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Введите описание");
            return sm;

        }else if (data.contains("/duration:")) {
            int duration = Integer.parseInt(data.substring(data.indexOf(':') + 1));
            usersIvents.get(chatId).setDuration(duration);
            if (duration == -1) {
                SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                sm.setText("Введите длительность:");
                return sm;
            }

            if (usersLastMessages.get(chatId).equals("edit")){
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                dbAdapter.insertInto("duration",duration, usersIvents.get(chatId).getId());
                return new SendMessage(chatId, "Изменено");
            }

            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.addEvent(chatId,usersIvents.get(chatId));
            } catch (SQLException e) {
               logger.error("Ошибка при вызове addEvent",e);
                throw new RuntimeException(e);
            }
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Событие создано");
            return sm;

        }else if (data.contains("/edit:")) {
            usersLastMessages.put(chatId,"edit");
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            String id = data.substring(data.indexOf(':') + 1);
            if (id.indexOf(':') != -1){
                id.substring(id.indexOf(':') + 1);
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                try {
                    usersIvents.get(chatId).setEvent(dbAdapter.findByID(id.substring(id.indexOf(':') + 1)));
                } catch (SQLException e) {
                    logger.error("Ошибка в получении объекта из базы данных:", e);
                    throw new RuntimeException(e);
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
                        sendMessageTime.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/"));
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
        switch (data) {
            case "/button1":
                return new SendMessage(chatId, "Вы порадовались");
            case "/button 2":
                return new SendMessage(chatId, "Вы не порадовались");
            default:
                return new SendMessage(chatId, "Ошибка");
        }
    }
}
