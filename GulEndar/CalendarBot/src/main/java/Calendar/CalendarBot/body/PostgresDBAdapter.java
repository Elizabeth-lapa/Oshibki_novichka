package Calendar.CalendarBot.body;

import Calendar.CalendarBot.config.PostgresDBConfig;
import Calendar.CalendarBot.entities.Event;
import org.springframework.stereotype.Component;

import java.sql.*;
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
                String schemaSQL = "CREATE SCHEMA calendar";
                stmt.executeUpdate(schemaSQL);
                String tableSql = "create table calendar.Event(chatID varchar(15) , text text, datetime timestamp,duration integer);";
                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            throw e;
        }
        logger.info("Schema was created");
        return true;
    }

    public boolean addEvent(String chatID, String text, java.time.LocalDateTime dateTime, int duration) throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "Insert into calendar.Event(chatID, text, datetime, duration) values ('"+ chatID + "','" + text + "','" + dateTime +"',"+ duration + " )";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            throw e;
        }
        return true;
    }


    public ArrayList<Event> showAllMessages() throws SQLException {
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                ArrayList<Event> eventsList = new ArrayList<>();

                String tableSql = "Select * from calendar.Event";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
                // Перебор строк с данными
                System.out.println(columns);
                while(res.next()){
                    for (int i = 1; i <= columns; i++){
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        eventsList.add(new Event(res.getString(2),LocalDateTime.parse(res.getString(3), formatter),res.getInt(4)));
                    }
                    }
                res.close();
                return eventsList;
            }

        }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            throw e;
        }

    }
}
