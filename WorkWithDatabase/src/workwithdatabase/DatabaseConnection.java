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

public class DatabaseConnection {
    private final Connection connection;
    
    public class Column {
        public final ArrayList<Integer> ids = new ArrayList<>();
        public final ArrayList<String> names = new ArrayList<>();
    }
    
    private interface ObjectFactory<T> {
        T create(ResultSet set) throws SQLException;
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
    
    private <T> ArrayList<T> query(String query, ObjectFactory<T> factory) {
        ArrayList<T> objects = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                objects.add(factory.create(resultSet));
            }
        } catch (SQLException e) {
            throw new Error(e);
        }
        return objects;
    }
    
    public ArrayList<Goods> getAllGoods() {
        final String query = "select * from goods";
        return query(query, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }
    
    public ArrayList<Goods> getGoodsAtWarehouse(int idWarehouse) {
        final String query = "select * from get_goods_at_warehouse(" + idWarehouse + ")";
        return query(query, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }
    
    public Column selectColumn(String from, String idField, String nameField) {
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
