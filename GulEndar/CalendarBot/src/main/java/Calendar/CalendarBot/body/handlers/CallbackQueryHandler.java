package Calendar.CalendarBot.body.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;

@Component
public class CallbackQueryHandler {

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();

        System.out.println("text:" + data);

        if (data.equals("/button1")) {
            System.out.println("Button 1 pressed");
            return new SendMessage(chatId, "Вы порадовались");
        }
        if (data.equals("/button 2")) {
            return new SendMessage(chatId, "Вы не порадовались");
        }
        return new SendMessage(chatId, "Message 2");
    }


}
