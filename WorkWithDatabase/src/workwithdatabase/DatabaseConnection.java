package workwithdatabase;

import workwithdatabase.models.Goods;
import java.io.FileInputStream;
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
import workwithdatabase.models.Agent;
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

    private <T> void query(String query, Callback<ArrayList<T>> callback,
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
                    // It's better to pass this error to higher layers 
                    // but only 3 days left.
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    callback.call(objects);
                });
            }
        }).start();
    }

    public void getAllGoods(Callback<ArrayList<Goods>> callback) {
        final String query = "select * from goods";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }

    public void getAllClients(Callback<ArrayList<Client>> callback) {
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

    public void getGoodsAtWarehouse(int idWarehouse, Callback<ArrayList<Goods>> callback) {
        final String query = "select * from get_goods_at_warehouse(" + idWarehouse + ")";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String nomenclature = resultSet.getString("nomenclature");
            String measure = resultSet.getString("measure");
            return new Goods(id, nomenclature, measure);
        });
    }

    public void getAllWarehouses(Callback<ArrayList<Warehouse>> callback) {
        final String query = "select * from warehouse";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String city = resultSet.getString("city");
            String name = resultSet.getString("name");
            return new Warehouse(id, city, name);
        });
    }

    public void getAllAgents(Callback<ArrayList<Agent>> callback) {
        final String query = "select * from agent";
        query(query, callback, (resultSet) -> {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String phone = resultSet.getString("phone");
            String city = resultSet.getString("city");
            return new Agent(id, name, phone, city);
        });
    }
    
    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e1) {
            e1.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public void createDelivery(Warehouse warehouse, Client client,
            List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        new Thread(() -> {
            synchronized (connection) {
                int deliveryId;
                try {
                    try (Statement statement = connection.createStatement();
                            ResultSet resultSet
                            = statement.executeQuery("execute procedure create_delivery("
                                    + client.getId() + ")")) {
                        resultSet.next();
                        deliveryId = resultSet.getInt(1);
                    }
                    for (SelectedGoods selectedGoods : goods) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(
                                    "execute procedure add_goods_to_delivery("
                                    + deliveryId + ", " + warehouse.getId() + ", "
                                    + selectedGoods.getGoods().getId() + ", "
                                    + selectedGoods.getCount() + ")");
                        }
                    }
                    connection.commit();
                    Platform.runLater(() -> {
                        callback.call(null);
                    });
                } catch (SQLException e) {
                    rollback();
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                }
            }
        }).start();
    }

    public void createTransfer(Warehouse source, Warehouse destination, List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        new Thread(() -> {
            synchronized (connection) {
                int transferId;
                try {
                    try (Statement statement = connection.createStatement();
                            ResultSet resultSet
                            = statement.executeQuery(
                                    "execute procedure create_transfer("
                                    + source.getId() + ", " + destination.getId() + ")")) {
                        resultSet.next();
                        transferId = resultSet.getInt(1);
                    }
                    for (SelectedGoods selectedGoods : goods) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(
                                    "execute procedure add_goods_to_transfer("
                                    + transferId + ", "
                                    + selectedGoods.getGoods().getMeasure() + ", "
                                    + selectedGoods.getCount() + ")");
                        }
                    }
                    connection.commit();
                    Platform.runLater(() -> {
                        callback.call(null);
                    });
                } catch (SQLException e) {
                    rollback();
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                }
            }
        }).start();
    }

    public void createSupply(Warehouse warehouse, Agent agent, List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        new Thread(() -> {
            synchronized (connection) {
                int supplyId;
                try {
                    try (Statement statement = connection.createStatement();
                            ResultSet resultSet
                            = statement.executeQuery(
                                    "execute procedure create_supply")) {
                        resultSet.next();
                        supplyId = resultSet.getInt(1);
                    }
                    for (SelectedGoods selectedGoods : goods) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(
                                    "execute procedure add_goods_to_supply("
                                    + supplyId + ", "
                                    + warehouse.getId() + ", "
                                    + selectedGoods.getGoods().getId() + ", "
                                    + selectedGoods.getCount() + ")");
                        }
                    }
                    connection.commit();
                    Platform.runLater(() -> {
                        callback.call(null);
                    });
                } catch (SQLException e) {
                    rollback();
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                }
            }
        }).start();
    }

    public void addGoods(String nomenclature, String measure, Callback<SQLException> callback) {
        new Thread(() -> {
            synchronized (connection) {
                try (PreparedStatement statement
                        = connection.prepareStatement(
                                "insert into goods(nomenclature, measure) values(?, ?)")) {
                    statement.setString(1, nomenclature);
                    statement.setString(2, measure);
                    statement.execute();
                    connection.commit();
                    Platform.runLater(() -> {
                        callback.call(null);
                    });
                } catch (SQLException e) {
                    rollback();
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                }
            }
        }).start();
    }

    public void removeGoods(Goods goods, Callback<SQLException> callback) {
        new Thread(() -> {
            synchronized (connection) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("delete from goods where id = " + goods.getId());
                    connection.commit();
                    Platform.runLater(() -> {
                        callback.call(null);
                    });
                } catch (SQLException e) {
                    rollback();
                    Platform.runLater(() -> {
                        callback.call(e);
                    });
                }
            }
        }).start();
    }
}
