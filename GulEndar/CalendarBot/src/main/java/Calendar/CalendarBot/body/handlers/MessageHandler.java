package Calendar.CalendarBot.body.handlers;

import Calendar.CalendarBot.body.PostgresDBAdapter;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;


@Component
public class MessageHandler {

    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    Event event;
    org.slf4j.Logger logger;

    PostgresDBAdapter dbAdapter;
    public MessageHandler(Event event){
        dbAdapter = new PostgresDBAdapter();
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
        this.event = event;
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
    }

    public BotApiMethod<?> answerMessage(Message message) {

        SendPhoto sendPhoto = new SendPhoto();

        String chatId = message.getChatId().toString();

        String messageText = message.getText();

        sendPhoto.setChatId(chatId);
        //TODO Добавить отправку фото
        //"https://proprikol.ru/wp-content/uploads/2021/08/kartinki-tokijskij-gul-11.jpg"

        //sendPhoto.setPhoto();

        if (messageText == null) {
            throw new IllegalArgumentException();
        }

        switch (messageText){
            case "/start":
                return startCommandReceived(chatId, message.getChat().getFirstName());
            case "Я котик чипи чипи":
                SendMessage sm = new SendMessage(chatId, "Поздравляю, теперь вы котик чипи чипи");
                sm.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons("/"));
                return sm;
            case "Я котик happy happy":
                SendMessage sndm = new SendMessage(chatId, "Поздравляю, теперь вы котик happy happy");
                sndm.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons("/"));
                return sndm;
            case "Я ГУЛЬ":
                return ghoulCommandReceived(chatId);
            case "Я человек":
                return new SendMessage(chatId, "Круто");
            case "Календарь":
                SendMessage calendar = new SendMessage(chatId,"Переключаюсь в режим календаря");
                calendar.setReplyMarkup(replyKeyboardMaker.getCalendarMainMenuKeyboard());
                return calendar;
            case "Создать событие":
                SendMessage sm2 = new SendMessage(chatId, "Выберите месяц");
                event.clear();
                sm2.setReplyMarkup(inlineKeyboardMaker.getCalendarMonthsButtons("/"));
                return sm2;
            case "Вывести события":
                PostgresDBAdapter DB = new PostgresDBAdapter();
                ArrayList<Event> events = new ArrayList<>();
                try {
                    events = DB.getAllEvents();
                } catch (SQLException e) {
                    System.out.println("Error in handler");
                }
                String response = "";
                for (Event e:events) {
                    response = response + e.toString() + "\n";
                }
                events.clear();
                SendMessage sndm2 = new SendMessage(chatId, response);

                return sndm2;
            default:
                if(event.getText().isEmpty()) {
                    event.setText(messageText);
                    return new SendMessage(chatId,"Введите длительность");
                }
                if(event.getDuration() < 0) {
                    event.setDuration(Integer.parseInt(messageText));
                    try {
                        dbAdapter.addEvent(chatId,event.getText(), event.getDateTime(), event.getDuration());
                    } catch (SQLException e) {
                        System.out.println("Ошибка в addEvent");
                        throw new RuntimeException(e);
                    }
                    event.clear();
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

    private SendMessage startCommandReceived(String chatId, String name) {
        String answer = "Hi, " + name + ", who are you?(Кто вы по жизни?)";
        SendMessage sm = new SendMessage(chatId, answer);
        sm.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sm;
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Button");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }
}
