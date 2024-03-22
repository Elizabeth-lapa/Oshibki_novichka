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
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
            keyboardButtonsRow1.add(getButton(
                    "Радоваться",
                    prefix + "button1"
            ));
rowList.add(keyboardButtonsRow1);
            keyboardButtonsRow2.add(getButton(
                    "Не радоваться",
                    prefix + "button 2"
            ));
        rowList.add(keyboardButtonsRow2);

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

        keyboardButtonsRow.add(getButton(
                Integer.toString(0),
                prefix + "hour:" + Integer.toString(0)
        ));
        for (int i= 1; i < 24; i++) {
            keyboardButtonsRow.add(getButton(
                    Integer.toString(i),
                    prefix + "hour:" + Integer.toString(i)
            ));
            if((i+1) % 6 == 0){
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
        keyboardButtonsRow.add(getButton(
                Integer.toString(0),
                prefix + "minute:" + Integer.toString(0)
        ));
        for (int i = 1; i < 60; i++) {
            keyboardButtonsRow.add(getButton(
                    Integer.toString(i),
                    prefix + "minute:" + Integer.toString(i)
            ));
            if((i+1) % 6 == 0){
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

    public InlineKeyboardMarkup getEventActionsButtons(String prefix, String eventID) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(getButton(
                "Удалить",
                prefix + "delete:" + eventID
        ));
        keyboardButtonsRow.add(getButton(
                "Изменить",
                prefix + "edit:" + eventID
        ));

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getDefaultDurationButtons(String prefix) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();


        keyboardButtonsRow.add(getButton(
                "0 минут",
                prefix + "duration:0"
        ));
        keyboardButtonsRow.add(getButton(
                "5 минут",
                prefix + "duration:5"
        ));
        keyboardButtonsRow.add(getButton(
                "10 минут",
                prefix + "duration:10"
        ));

        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();
        keyboardButtonsRow.add(getButton(
                "15 минут",
                prefix + "duration:15"
        ));
        keyboardButtonsRow.add(getButton(
                "30 минут",
                prefix + "duration:30"
        ));
        keyboardButtonsRow.add(getButton(
                "1 час",
                prefix + "duration:60"
        ));
        rowList.add(keyboardButtonsRow);
        keyboardButtonsRow= new ArrayList<>();
        keyboardButtonsRow.add(getButton(
                "Ввести свое:",
                prefix + "duration:-1"
        ));
        rowList.add(keyboardButtonsRow);
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
