package workwithdatabase;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class DatabaseConnection {
    private Connection connection;
    
    public class Column {
        public final ArrayList<Integer> ids = new ArrayList<>();
        public final ArrayList<String> names = new ArrayList<>();
    }

    public DatabaseConnection() throws Exception {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("database.prop")) {
            properties.load(inputStream);
        }
        String databaseURL = properties.getProperty("dbURL");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String driverName = properties.getProperty("driver");
        Class.forName(driverName);
        connection = DriverManager.getConnection(databaseURL, user, password);
    }
    
    public Column selectColumn(String from, 
            String idField, String nameField) {
        Column column = new Column();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from " + from)) {
            while (resultSet.next()) {
                column.ids.add(resultSet.getInt(idField));
                column.names.add(resultSet.getString(nameField));
            }
        } catch (SQLException e) {
            throw new Error(e);
        }
        return column;
    }
}
