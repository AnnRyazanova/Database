package workwithdatabase;

import workwithdatabase.models.Goods;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.util.Callback;
import workwithdatabase.models.Client;
import workwithdatabase.models.Warehouse;

public class DatabaseConnection {
    private final Connection connection;
    
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
        connection.setAutoCommit(false);
    }

    private <T> void query(String query, Callback<ArrayList<T>, Void> callback, 
            ObjectFactory<T> factory) {
        new Thread(() -> {
            ArrayList<T> objects = new ArrayList<>();
            synchronized (connection) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(query);
                    try (ResultSet resultSet = statement.getResultSet()) {
                        while (resultSet.next()) {
                            objects.add(factory.create(resultSet));
                        }
                    }
                    connection.commit();
                } catch (SQLException e) {
                    throw new Error(e); // It's better to pass this error to higher layers 
                                        // but only 3 days left.
                }
                Platform.runLater(() -> {
                    callback.call(objects);
                });
            }
        }).start();
    }
    
    public void getAllGoods(Callback<ArrayList<Goods>, Void> callback) {
        final String query = "select * from goods";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }
    
    public void getAllClients(Callback<ArrayList<Client>, Void> callback) {
        final String query = "select * from client";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String city = resultSet.getString("city");
            String address = resultSet.getString("address");
            String phone = resultSet.getString("phone");
            return new Client(id, name, city, address, phone);
        });
    }
    
    public void getGoodsAtWarehouse(int idWarehouse, Callback<ArrayList<Goods>, Void> callback) {
        final String query = "select * from get_goods_at_warehouse(" + idWarehouse + ")";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }
    
    public void getAllWarehouses(Callback<ArrayList<Warehouse>, Void> callback) {
        final String query = "select * from warehouse";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String city = resultSet.getString("city");
            String name = resultSet.getString("name");
            return new Warehouse(id, city, name);
        });
    }
    
    public void createDelivery(Warehouse warehouse, Client client, 
            List<DeliveryWindowController.SelectedGoods> goods, 
            Callback<SQLException, Void> callback) {
        new Thread(() -> {
            synchronized (connection) {
                int deliveryId;
                try {
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = 
                                 statement.executeQuery(
                                         "execute procedure create_delivery(" + client.id + ")")) {
                        resultSet.next();
                        deliveryId = resultSet.getInt(1);
                    }
                    for (DeliveryWindowController.SelectedGoods selectedGoods : goods) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(
                                    "execute procedure add_goods_to_delivery(" + 
                                    deliveryId + ", " + warehouse.id + ", " + 
                                    selectedGoods.getGoods().id + ", " + 
                                    selectedGoods.getCount() + ")");
                        } 
                    } 
                    connection.commit();
                } catch (SQLException e) {
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        throw new RuntimeException(e1);
                    }
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                    return;
                }
                Platform.runLater(() -> {
                    callback.call(null);
                });
            }
        }).start();
    }
}
