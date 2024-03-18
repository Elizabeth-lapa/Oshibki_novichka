package Calendar.CalendarBot.entities;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class Event {
    int duration;
    String text;
    LocalDateTime dateTime;
    public Event(){
        duration = -1;
        text = "";
        dateTime = LocalDateTime.now(Clock.tickSeconds(ZoneId.of("UTC+3")));
    }

    public Event(String text, LocalDateTime dateTime,int duration) {
        this.duration = duration;
        this.text = text;
        this.dateTime = dateTime;
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
        return dateTime.getDayOfMonth() + "." +dateTime.getMonth().toString() + " " + dateTime.getHour() +":" +dateTime.getMinute() + " - " + Integer.toString(duration) +" мин" + " - " + text;
    }
}
