package Calendar.CalendarBot.entities;


import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class Event {

    private int duration;
    private String text;
    private LocalDateTime dateTime;
    private String id = "";

    public Event(){
        duration = 1;
        text = "blank";
        dateTime = LocalDateTime.now(Clock.tickSeconds(ZoneId.of("UTC+3")));
    }

    public Event(String id, String text, LocalDateTime dateTime,int duration) {
        this.id = id;
        this.duration = duration;
        this.text = text;
        this.dateTime = dateTime;
    }

    public Event(String text, LocalDateTime dateTime,int duration) {
        this.duration = duration;
        this.text = text;
        this.dateTime = dateTime;
    }

    public void setEvent(Event event){
        this.id = event.getId();
        this.duration = event.getDuration();
        this.text = event.getText();
        this.dateTime = event.getDateTime();
    }

    public Event getEvent(){
        return new Event(id,text,dateTime,duration);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDayOfMonth(int dayOfMonth) throws DateTimeException {
        try {
            dateTime = dateTime.withDayOfMonth(dayOfMonth);
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMonth(int month) throws DateTimeException{
        try{
        dateTime = dateTime.withMonth(month);
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public void setHour(int hour)throws DateTimeException{
        try{
        dateTime = dateTime.withHour(hour);
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public void setMinute(int minute)throws DateTimeException{
        try{
        dateTime = dateTime.withMinute(minute);
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public void setTime(int hour, int minute)throws DateTimeException{
        try{
        dateTime = dateTime.withHour(hour);
        dateTime = dateTime.withMinute(minute);
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public void setDateTime(LocalDateTime dateTime) {
        try{
        this.dateTime = dateTime;
        }catch (DateTimeException e) {
            throw e;
        }
    }

    public void clear(){
        duration = -1;
        text = "";
    }


    public String toStringWithoutDuration() {
        //выводит: HH:mm - HH:mm текст
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String str = " ";
        String endTime = dateTime.plusMinutes(duration).format(formatter);
        str =  dateTime.format(formatter) + "-" + endTime + " ";
        str = str + " - " + text;
        return str;
    }

    public String toStringWithoutDate() {
        //выводит: HH:mm длительность текст
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm ");

        String str =  dateTime.format(formatter);
        if (duration % 60 == 0) {
            int hours = duration / 60;
            str = str + hours + "ч";
        } else if(duration > 60) {
            int minutes = duration % 60;
            int hours = duration / 60;
            str = str + hours + "ч " + minutes + "мин";
        } else str = str + duration +"мин";
        str = str + " - " + text;
        return str;
    }

    public String toStringStartAndEndDates() {
        //выводит: HH:mm длительность текст
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        String startDate = dateTime.format(formatter);
        LocalDateTime endDateTime = dateTime.plusMinutes(duration);
        String endDate = endDateTime.format(formatter);
        String res = startDate + " - " + endDate + ": " + text;
        return res;
    }

    public String toString()
    { //выводит: dd.MM HH:mm длительность текст
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm ");

         String str =  dateTime.format(formatter);
        if (duration % 60 == 0) {
            int hours = duration / 60;
            str = str + hours + "ч";
        } else if(duration > 60) {
            int minutes = duration % 60;
            int hours = duration / 60;
            str = str + hours + "ч " + minutes + "мин";
        } else str = str + duration +"мин";
        str = str + " - " + text;
        return str;
    }
}
