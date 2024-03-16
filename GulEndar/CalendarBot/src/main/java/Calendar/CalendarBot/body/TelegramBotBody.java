package Calendar.CalendarBot.body;

import Calendar.CalendarBot.body.handlers.CallbackQueryHandler;
import Calendar.CalendarBot.body.keyboards.InlineKeyboardMaker;
import Calendar.CalendarBot.body.keyboards.ReplyKeyboardMaker;
import Calendar.CalendarBot.config.BotConfig;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBotBody extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    CallbackQueryHandler callbackQueryHandler;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    org.slf4j.Logger logger;
    public TelegramBotBody(BotConfig botConfig){
        this.botConfig = botConfig;
        callbackQueryHandler = new CallbackQueryHandler();
        replyKeyboardMaker = new ReplyKeyboardMaker();
        inlineKeyboardMaker = new InlineKeyboardMaker();
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
        String currency = "";

        if (update.hasCallbackQuery()) {
            //обработка команд с reply кнопок
            logger.info("CallbackQuery received");
            CallbackQuery callbackQuery = update.getCallbackQuery();
            try {
               execute(callbackQueryHandler.processCallbackQuery(callbackQuery));
            } catch (IOException e) {
                logger.info("CallbackHandlerError:", e);
                throw new RuntimeException(e);
            } catch (TelegramApiException e) {
            logger.error("Send message in CallbackHandler error:", e);
        }
        } else if(update.hasMessage() && update.getMessage().hasText()){
            //обработка текстовых сообщений
            logger.info("text message received");
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "Я котик чипи чипи":
                    sendMessage(chatId, "Поздравляю, теперь вы котик чипи чипи", inlineKeyboardMaker.getInlineMessageButtons("/"));
                    break;
                case "Я котик happy happy":
                    sendMessage(chatId, "Поздравляю, теперь вы котик happy happy", inlineKeyboardMaker.getInlineMessageButtons("/"));
                    break;
                case "Я ГУЛЬ":
                    ghoulCommandReceived(chatId);
                    break;
                case "Я человек":
                    sendMessage(chatId, "Круто");
                    break;
                default:
                    sendMessage(chatId, currency);
            }

        }

    }

    private void ghoulCommandReceived(Long chatId) {
        String answer = "";

        for (int i = 1000; i > 0; i = i - 7){
            answer = answer + Integer.toString(i) + "-7=" + Integer.toString(i-7) + "\n";
        }
        sendMessage(chatId, answer);
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", who are you?(Кто вы по жизни?)";

        sendMessage(chatId, answer, replyKeyboardMaker.getMainMenuKeyboard());
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

