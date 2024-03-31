package Calendar.CalendarBot.body;

import Calendar.CalendarBot.config.PostgresDBConfig;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@Component
public class PostgresDBAdapter {
    org.slf4j.Logger logger;
    private PostgresDBConfig db_config;
    private String DB_URL  = "jdbc:postgresql://localhost:5432/testdb";
     private String USER = "postgres";
     private String PASS = "1123";

    public PostgresDBAdapter(){
        logger = org.slf4j.LoggerFactory.getLogger(PostgresDBAdapter.class);
    }

    public boolean createTable() throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                String schemaSQL = "CREATE SCHEMA IF NOT EXISTS calendar";
                stmt.executeUpdate(schemaSQL);
                String tableSql = "create table IF NOT EXISTS calendar.Event(id serial ,chatID varchar(15) , text text, datetime timestamp,duration integer);";
                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            throw e;
        }
        logger.info("Schema and table was created");
        return true;
    }
    public boolean addEvent(String chatID,Event event) throws SQLException {
        return addEvent(chatID, event.getText(),event.getDateTime(),event.getDuration());
    }
    public boolean addEvent(String chatID, String text, java.time.LocalDateTime dateTime, int duration) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "Insert into calendar.Event(chatID, text, datetime, duration) values ('"+ chatID + "','" + text + "','" + dateTime +"',"+ duration + " )";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection in addEvent interrupted with:", e);
            throw e;
        }
        return true;
    }


    public ArrayList<Event> getEventsByDate(String id, LocalDate date) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String tableSql = "Select * from calendar.Event where DATE(datetime) <= '" + date.format(formatter)+ "' AND DATE(datetime + make_interval(mins => duration)) >= '" + date.format(formatter)+ "' AND chatID = '" + id + "' ORDER BY datetime;";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public ArrayList<Event> getEventsBeforeOrAfterDate(String chatId, LocalDate date, String lessORmoreSign) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String tableSql = "Select * from calendar.Event where DATE(datetime) "+ lessORmoreSign +" '" + date.format(formatter)+ "'AND chatID = '" + chatId + "' ORDER BY datetime;";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);


                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public void deleteByID(String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "delete from calendar.Event where id = " + id+ ";";
                stmt.executeUpdate(tableSql);
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public void insertInto(String parametr, int value, String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "update calendar.Event set " + parametr + " = " + value + " where id = " + id + ";";
                stmt.executeUpdate(tableSql);
            }
        }catch (SQLException e){
            logger.error("Can not insert in DB:", e);
            throw e;
        }
    }

    public void insertInto(java.time.LocalDateTime dateTime, String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "update calendar.Event set datetime = '" + dateTime + "' where id = " + id + ";";
                stmt.executeUpdate(tableSql);
            }
        }catch (SQLException e){
            logger.error("Can not insert timestamp in data base:", e);
            throw e;
        }
    }

    public void insertInto(String parametr, String value, String id) throws SQLException {
            try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
                try (Statement stmt = DB_connection.createStatement()) {

                    String tableSql = "update calendar.Event set " + parametr + " = '" + value + "' where id = " + id + ";";
                    stmt.executeUpdate(tableSql);
                }
            }catch (SQLException e){
                logger.error("Can not insert in DB:", e);
                throw e;
            }
        }

    public Event getByID(String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where id = " + id+ ";";
                ResultSet res = stmt.executeQuery(tableSql);

                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList.get(0);
            }

        }catch (SQLException e){
            logger.error("Сan not get an event from DB:", e);
            throw e;
        }
    }

    public ArrayList<Event> getEventsByText(String id ,String text) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where text ilike '%" + text+ "%'AND chatID = '" + id + "' ORDER BY datetime";
                ResultSet res = stmt.executeQuery(tableSql);
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public ArrayList<Event> getAllEvents(String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where chatID = '" + id + "' ORDER BY datetime";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public Event getFirstBeforeDate(String chatId, LocalDateTime dateTime, String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;
                String tableSql = "Select * from calendar.Event where datetime <= '" + dateTime + "' AND id != "+id+" AND duration <= 1440 AND chatID = '" + chatId + "' order by datetime limit 1;";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList.get(0);
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public Event getFirstAfterDate(String chatId, LocalDateTime dateTime,String id) throws SQLException, ArrayIndexOutOfBoundsException{
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;
                String tableSql = "Select * from calendar.Event where date_trunc('second',datetime) >= date_trunc('second', TIMESTAMP '" + dateTime + "') AND id != "+id+" AND duration <= 1440 AND duration != 0 AND chatID = '" + chatId + "'order by datetime limit 1;";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                Event event;
                try {
                    event = eventsList.get(0);
                }catch (ArrayIndexOutOfBoundsException e){
                    logger.error("index out of bounds exception:", e);
                    throw e;
                }
                return event;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public ArrayList<Event> getConflictsBefore(String chatId, LocalDateTime dateTime) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where datetime < '"+dateTime+"' AND datetime + make_interval(mins => duration) > '" + dateTime + "'AND chatID = '"+ chatId +"' AND duration <= 1440 AND duration != 0 ORDER BY datetime;";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    public ArrayList<Event> getConflictsBetween(String chatId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where (datetime >= '"+dateTimeStart+"'AND datetime < '" + dateTimeEnd +"') OR (datetime > '"+dateTimeStart+"'AND datetime <= '" +dateTimeEnd+"') AND chatID = '"+chatId+"' AND duration <= 1440 AND duration != 0 ORDER BY datetime;\n";
                ResultSet res = stmt.executeQuery(tableSql);
                // Перебор строк с данными
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
            throw e;
        }
    }

    private ArrayList<Event> getEventsListFromResultSet(ResultSet res){
        ArrayList<Event> eventsList = new ArrayList<>();
        while(true){
            try {
                if (!res.next()) break;

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                eventsList.add(new Event(Integer.toString(res.getInt(1)),res.getString(3),LocalDateTime.parse(res.getString(4), formatter),res.getInt(5)));

            } catch (SQLException e) {
                logger.error("DB connection in interrupted with:", e);
                throw new RuntimeException(e);
            }
        }
return eventsList;
    }
}
