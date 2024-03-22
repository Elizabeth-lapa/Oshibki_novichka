package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;
import java.sql.SQLException;

@Component
public class CallbackQueryHandler {

    Event event;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    org.slf4j.Logger logger;

    public CallbackQueryHandler(Event event){
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
        this.event = event;
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
    }



    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();

        if (data.contains("/day:")) {
            int day = Integer.parseInt(data.substring(5));
            event.setDayOfMonth(day);
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarHourButtons("/"));
            sm.setText("Выберите час");
            return sm;
        }
            else if (data.contains("/delete:")) {
                PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
                String id = data.substring(data.indexOf(':') + 1);
            try {
                dbAdapter.deleteByID(id);
            } catch (SQLException e) {
                logger.error("Deleting message error:", e);
                throw new RuntimeException(e);
            }
            SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                sm.setText("Событие удалено");
                return sm;
            }
         else if (data.contains("/month:")) {
            int month = Integer.parseInt(data.substring(7));
            event.setMonth(month);
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarDaysButtons("/"));
            sm.setChatId(chatId);
            sm.setText("Выберите день");
            return sm;

        }else if (data.contains("/hour:")) {
            int hour = Integer.parseInt(data.substring(6));
            event.setHour(hour);
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/"));
            sm.setChatId(chatId);
            sm.setText("Выберите минуты");
            return sm;

        } else if (data.contains("/minute:")) {
            int minute = Integer.parseInt(data.substring(data.indexOf(':') + 1));
            event.setMinute(minute);
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Введите текст");
            return sm;
        }else if (data.contains("/duration:")) {
            int duration = Integer.parseInt(data.substring(data.indexOf(':') + 1));
            event.setDuration(duration);
            if (duration == -1) {
                SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                sm.setText("Введите длительность:");
                return sm;
            }
            PostgresDBAdapter dbAdapter = new PostgresDBAdapter();
            try {
                dbAdapter.addEvent(chatId,event.getText(), event.getDateTime(), event.getDuration());
            } catch (SQLException e) {
               logger.error("Ошибка при вызове addEvent",e);
                throw new RuntimeException(e);
            }
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Событие создано");
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
