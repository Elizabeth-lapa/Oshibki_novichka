package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;

@Component
public class CallbackQueryHandler {

    Event event;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;

    public CallbackQueryHandler(Event event){
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

        } else if (data.contains("/month:")) {
            int month = Integer.parseInt(data.substring(7));
            System.out.println(month);
            event.setMonth(month);
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarDaysButtons("/"));
            sm.setChatId(chatId);
            sm.setText("Выберите день");
            return sm;

        }else if (data.contains("/hour:")) {
            int hour = Integer.parseInt(data.substring(6));
            System.out.println(hour);
            event.setHour(hour);
            SendMessage sm = new SendMessage();
            sm.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/"));
            sm.setChatId(chatId);
            sm.setText("Выберите минуты");
            return sm;

        } else if (data.contains("/minute:")) {
            int minute = Integer.parseInt(data.substring(8));
            System.out.println(minute);
            event.setHour(minute);
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText("Введите текст");
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
