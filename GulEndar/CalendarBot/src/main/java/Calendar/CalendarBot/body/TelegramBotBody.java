package Calendar.CalendarBot.body;

import Calendar.CalendarBot.body.handlers.CallbackQueryHandler;
import Calendar.CalendarBot.body.handlers.MessageHandler;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.config.BotConfig;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class TelegramBotBody extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    CallbackQueryHandler callbackQueryHandler;
    HashMap<String, Event> usersIvents;
    HashMap<String, String> usersLastMessages;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    PostgresDBAdapter dbAdapter;
    MessageHandler messageHandler;
    org.slf4j.Logger logger;
    public TelegramBotBody(BotConfig botConfig){
         usersIvents = new HashMap<>();
         usersLastMessages = new HashMap<>();
        this.botConfig = botConfig;
        dbAdapter = new PostgresDBAdapter();
        callbackQueryHandler = new CallbackQueryHandler(usersIvents,this, usersLastMessages);
        messageHandler = new MessageHandler(usersIvents, this, usersLastMessages);
        logger = org.slf4j.LoggerFactory.getLogger(TelegramBotBody.class);
    }
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasCallbackQuery()) {
            //обработка команд с reply кнопок
            logger.info("CallbackQuery received from " + update.getCallbackQuery().getMessage().getChatId());
            CallbackQuery callbackQuery = update.getCallbackQuery();
            try {
                execute(callbackQueryHandler.processCallbackQuery(callbackQuery));
            } catch (IOException e) {
                logger.info("CallbackHandlerError:", e);
                throw new RuntimeException(e);
            } catch (TelegramApiException e) {
                logger.error("Send message in CallbackHandler call error:", e);
            }

        } else if(update.hasMessage() && update.getMessage().hasText()){
            //обработка текстовых сообщений
            logger.info("text message received from " + update.getMessage().getChat().getFirstName() +" " +  update.getMessage().getChatId());
            try{
                    execute(messageHandler.answerMessage(update.getMessage()));
            } catch (TelegramApiException e) {
                logger.error("Send message in CallbackHandler error:", e);
            }

        }

    }

    public void executeMethods(ArrayList<BotApiMethod<?>> methodsForExecute){
        for (BotApiMethod<?> method:methodsForExecute) {
            try {
                execute(method);
            } catch (TelegramApiException e) {
                logger.error("Many methods execute error:", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Send message error:", e);
        }
    }
    private void sendMessage(Long chatId, String textToSend, InlineKeyboardMarkup inlineKeyboard){

        SendMessage sendMessage = new SendMessage();

        sendMessage.setReplyMarkup(inlineKeyboard);

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Send message error:", e);
        }
    }
    private void sendMessage(Long chatId, String textToSend, ReplyKeyboardMarkup replyKeyboard){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Send message error:", e);
        }
    }
}
