/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workwithdatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author User
 */
public class DatabaseConnection {
    private Connection connection;

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
    
    public ArrayList<String> selectAll(String from, String[] args, String field) {
        ArrayList<String> listNames = new ArrayList();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from " + from)) {
            int i = 0;
            String[][] data = new String[50][args.length];
            while (resultSet.next()) {
                for (int k = 0; k < args.length; k++){ 
                    data[i][k] = resultSet.getString(args[k]);
                    if (args[k].equals(field))
                        listNames.add(data[i][k]);
                }
                i++;
            }
        } catch (SQLException e) {
            throw new Error(e);
        }
        return listNames;
    }
}
