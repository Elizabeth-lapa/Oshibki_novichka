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


    public ArrayList<Event> getTodayEvents(LocalDate todayDate) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String tableSql = "Select * from calendar.Event where DATE(datetime) = '" + todayDate.format(formatter)+ "'";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
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

    public void deleteByID(String id)  {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "delete from calendar.Event where id = " + id+ ";";
                stmt.executeUpdate(tableSql);
            }

        }catch (SQLException e){
            logger.error("DB connection in interrupted with:", e);
        }
    }

    public void insertInto(String parametr, int value, String id) {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "update calendar.Event set " + parametr + " = " + value + " where id = " + id + ";";
                stmt.executeUpdate(tableSql);
            }
        }catch (SQLException e){
            logger.error("Can not insert in DB:", e);
        }
    }

    public void insertInto(java.time.LocalDateTime dateTime, String id){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "update calendar.Event set datetime = '" + dateTime + "' where id = " + id + ";";
                stmt.executeUpdate(tableSql);
            }
        }catch (SQLException e){
            logger.error("Can not insert timestamp in data base:", e);
        }
    }

    public void insertInto(String parametr, String value, String id){
            try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
                try (Statement stmt = DB_connection.createStatement()) {

                    String tableSql = "update calendar.Event set " + parametr + " = '" + value + "' where id = " + id + ";";
                    stmt.executeUpdate(tableSql);
                }
            }catch (SQLException e){
                logger.error("Can not insert in DB:", e);
            }
        }

    public Event findByID(String id) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where id = " + id+ ";";
                ResultSet res = stmt.executeQuery(tableSql);

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                eventsList = getEventsListFromResultSet(res);
                res.close();
                return eventsList.get(0);
            }

        }catch (SQLException e){
            logger.error("Сan not get an event from DB:", e);
            throw e;
        }
    }

    public ArrayList<Event> findEventsByText(String text) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList;

                String tableSql = "Select * from calendar.Event where text ilike '%" + text+ "%'";
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

    public ArrayList<Event> getAllEvents() throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList = new ArrayList<>();

                String tableSql = "Select * from calendar.Event";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
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
