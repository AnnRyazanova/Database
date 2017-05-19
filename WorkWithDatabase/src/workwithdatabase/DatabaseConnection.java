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
    
    private void runAsynchronously(Action workerThread, Action uiThread) {
        new Thread(() -> {
            Utils.catchAll(() -> {
                workerThread.call();
                Platform.runLater(() -> {
                    Utils.catchAll(() -> uiThread.call());
                });
            });
        }).run();
    }

    private <T> void query(String query, Callback<ArrayList<T>> callback,
            ObjectFactory<T> factory) {
        final ArrayList<T> objects = new ArrayList<>();
        runAsynchronously(
                () -> { 
                    synchronized (connection) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(query);
                            try (ResultSet resultSet = statement.getResultSet()) {
                                while (resultSet.next()) {
                                    objects.add(factory.create(resultSet));
                                }
                            }
                            connection.commit();
                        }
                    }
                }, 
                () -> { 
                    callback.call(objects);
                });
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

    public void createDelivery(Warehouse warehouse, Client client,
            List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        runAsynchronously(
                () -> { 
                    synchronized (connection) {
                        int deliveryId;
                        try {
                            try (Statement statement = connection.createStatement();
                                    ResultSet resultSet
                                    = statement.executeQuery("execute procedure create_delivery("
                                            + client.id + ")")) {
                                resultSet.next();
                                deliveryId = resultSet.getInt(1);
                            }
                            for (SelectedGoods selectedGoods : goods) {
                                try (Statement statement = connection.createStatement()) {
                                    statement.execute(
                                            "execute procedure add_goods_to_delivery("
                                            + deliveryId + ", " + warehouse.id + ", "
                                            + selectedGoods.getGoods().id + ", "
                                            + selectedGoods.getCount() + ")");
                                }
                            }
                            connection.commit();
                        } catch (SQLException e) {
                            Utils.catchAll(() -> connection.rollback());
                            Platform.runLater(() -> {
                                Utils.catchAll(() -> callback.call(e));
                            });
                        }
                    }
                }, 
                () -> { 
                    Platform.runLater(() -> {
                        Utils.catchAll(() -> callback.call(null));
                    });
                });
    }

    public void createTransfer(Warehouse source, Warehouse destination, List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        runAsynchronously(
                () -> {
                    synchronized (connection) {
                        int transferId;
                        try {
                            try (Statement statement = connection.createStatement();
                                    ResultSet resultSet
                                    = statement.executeQuery(
                                            "execute procedure create_transfer("
                                            + source.id + ", " + destination.id + ")")) {
                                resultSet.next();
                                transferId = resultSet.getInt(1);
                            }
                            for (SelectedGoods selectedGoods : goods) {
                                try (Statement statement = connection.createStatement()) {
                                    statement.execute(
                                            "execute procedure add_goods_to_transfer("
                                            + transferId + ", "
                                            + selectedGoods.getGoods().id + ", "
                                            + selectedGoods.getCount() + ")");
                                }
                            }
                            connection.commit();
                        } catch (SQLException e) {
                            Utils.catchAll(() -> connection.rollback());
                            Platform.runLater(() -> {
                                Utils.catchAll(() -> callback.call(e));
                            });
                        }
                    }
                }, 
                () -> { 
                    Platform.runLater(() -> {
                        Utils.catchAll(() -> callback.call(null));
                    });
                });
    }

    public void createSupply(Warehouse warehouse, Agent agent, List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        runAsynchronously(
                () -> {
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
                                            + warehouse.id + ", "
                                            + selectedGoods.getGoods().id + ", "
                                            + selectedGoods.getCount() + ")");
                                }
                            }
                            connection.commit();
                        } catch (SQLException e) {
                            Utils.catchAll(() -> connection.rollback());
                            Platform.runLater(() -> {
                                Utils.catchAll(() -> callback.call(e));
                            });
                        }
                    }
                }, 
                () -> { 
                    Platform.runLater(() -> {
                        Utils.catchAll(() -> callback.call(null));
                    });
                });
    }
    
    public void addGoods(String nomenclature, String measure, Callback<SQLException> callback)  {
        new Thread(() -> {
            synchronized (connection) {
                try (PreparedStatement statement = connection.prepareStatement("insert into goods(nomenclature, measure) values(?, ?)")) {
                    statement.setString(1, nomenclature);
                    statement.setString(2, measure);
                    statement.execute();
                    connection.commit();
                    
                } catch (SQLException e) {
                    
                }
            }
        }).start();
    }
}
