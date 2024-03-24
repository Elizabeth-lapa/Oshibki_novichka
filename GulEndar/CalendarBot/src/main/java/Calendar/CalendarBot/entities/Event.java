package Calendar.CalendarBot.entities;


import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void setDayOfMonth(int dayOfMonth){
        dateTime = dateTime.withDayOfMonth(dayOfMonth);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMonth(int month){
        dateTime = dateTime.withMonth(month);
    }

    public void setHour(int hour){
        dateTime = dateTime.withHour(hour);
    }

    public void setMinute(int minute){
        dateTime = dateTime.withMinute(minute);
    }

    public void setTime(int hour, int minute){
        dateTime = dateTime.withHour(hour);
        dateTime = dateTime.withMinute(minute);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void clear(){
        duration = -1;
        text = "";
    }

    public String toString() {
        String str =  dateTime.getDayOfMonth() + " " +dateTime.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) + " " + dateTime.getHour() +":" +dateTime.getMinute() + " - ";
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
