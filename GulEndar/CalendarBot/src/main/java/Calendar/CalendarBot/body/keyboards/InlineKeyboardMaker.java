package Calendar.CalendarBot.body.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMarkup getInlineMessageButtons(String prefix) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

            keyboardButtonsRow.add(getButton(
                    "Радоваться",
                    prefix + "button1"
            ));
rowList.add(keyboardButtonsRow);
keyboardButtonsRow.clear();
            keyboardButtonsRow.add(getButton(
                    "Не радоваться",
                    prefix + "button 2"
            ));
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getCalendarDaysButtons(String prefix) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (int i= 1; i <= 31; i++) {
            keyboardButtonsRow.add(getButton(
                    Integer.toString(i),
                    prefix + "day:" + Integer.toString(i)
            ));
            if(i % 7 == 0){
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }
        }
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getCalendarHourButtons(String prefix) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (int i= 0; i < 24; i++) {
            keyboardButtonsRow.add(getButton(
                    Integer.toString(i),
                    prefix + "hour:" + Integer.toString(i)
            ));
            if(i % 6 == 0){
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }
        }
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getCalendarMinuteButtons(String prefix) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (int i= 0; i < 60; i++) {
            keyboardButtonsRow.add(getButton(
                    Integer.toString(i),
                    prefix + "minute:" + Integer.toString(i)
            ));
            if(i % 10 == 0){
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }
        }
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getCalendarMonthsButtons(String prefix) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();


            keyboardButtonsRow.add(getButton(
                    "Январь",
                    prefix + "month:1"
            ));
        keyboardButtonsRow.add(getButton(
                "Февраль",
                prefix + "month:2"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Март",
                prefix + "month:3"
        ));
        keyboardButtonsRow.add(getButton(
                "Апрель",
                prefix + "month:4"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Май",
                prefix + "month:5"
        ));
        keyboardButtonsRow.add(getButton(
                "Июнь",
                prefix + "month:6"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Июль",
                prefix + "month:7"
        ));
        keyboardButtonsRow.add(getButton(
                "Август",
                prefix + "month:8"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Сентябрь",
                prefix + "month:9"
        ));
        keyboardButtonsRow.add(getButton(
                "Октябрь",
                prefix + "month:10"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Ноябрь",
                prefix + "month:11"
        ));
        keyboardButtonsRow.add(getButton(
                "Декабрь",
                prefix + "month:12"
        ));
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    private InlineKeyboardButton getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);

        return button;
    }
}
