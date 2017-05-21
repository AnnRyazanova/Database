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
    
    private synchronized <T> void queryInternal(String query, Callback<ArrayList<T>> callback,
            ObjectFactory<T> factory) {
        ArrayList<T> objects = new ArrayList<>();
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
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            callback.call(objects);
        });
    }

    private <T> void query(String query, Callback<ArrayList<T>> callback,
            ObjectFactory<T> factory) {
        new Thread(() -> queryInternal(query, callback, factory)).start();
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
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    private synchronized void createDeliveryInternal(Warehouse warehouse, Client client,
            List<SelectedGoods> goods, Callback<SQLException> callback) {
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
    
    public void createDelivery(Warehouse warehouse, Client client,
            List<SelectedGoods> goods, Callback<SQLException> callback) {
        new Thread(() -> createDeliveryInternal(warehouse, client, goods, callback)).start();
    }

    private synchronized void createTransferInternal(Warehouse source, Warehouse destination, 
            List<SelectedGoods> goods, Callback<SQLException> callback) {
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
    
    public void createTransfer(Warehouse source, Warehouse destination, 
            List<SelectedGoods> goods, Callback<SQLException> callback) {
        new Thread(() -> createTransferInternal(source, destination, goods, callback)).start();
    }

    private synchronized void createSupplyInternal(Warehouse warehouse, Agent agent,
            List<SelectedGoods> goods, Callback<SQLException> callback) {
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

    public void createSupply(Warehouse warehouse, Agent agent, List<SelectedGoods> goods,
            Callback<SQLException> callback) {
        new Thread(() -> createSupplyInternal(warehouse, agent, goods, callback)).start();
    }

    private synchronized void addGoodsInternal(String nomenclature, String measure,
            Callback<SQLException> callback) {
        String query = "insert into goods(nomenclature, measure) values(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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

    public void addGoods(String nomenclature, String measure, Callback<SQLException> callback) {
        new Thread(() -> addGoodsInternal(nomenclature, measure, callback)).start();
    }

    private synchronized void removeGoodsInternal(Goods goods, Callback<SQLException> callback) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("delete from goods where id = " + goods.getId());
            connection.commit();
            Platform.runLater(() -> {
                callback.call(null);
            });
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }

    public void removeGoods(Goods goods, Callback<SQLException> callback) {
        new Thread(() -> removeGoodsInternal(goods, callback)).start();
    }

    private synchronized void changeGoodsNomenclatureInternal(Goods goods,
            Callback<SQLException> callback) {
        String query = "update goods set nomenclature = ? where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, goods.getNomenclature());
            statement.setInt(2, goods.getId());
            statement.execute();
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }

    public void changeGoodsNomenclature(Goods goods, Callback<SQLException> callback) {
        new Thread(() -> changeGoodsNomenclatureInternal(goods, callback)).start();
    }
    
    private synchronized void changeGoodsMeasureInternal(Goods goods,
            Callback<SQLException> callback) {
        String query = "update goods set measure = ? where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, goods.getMeasure());
            statement.setInt(2, goods.getId());
            statement.execute();
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }

    public void changeGoodsMeasure(Goods goods, Callback<SQLException> callback) {
        new Thread(() -> changeGoodsMeasureInternal(goods, callback)).start();
    }
    
    
    private synchronized void addAgentsInternal(String name, String phone, String city,
            Callback<SQLException> callback) {
        String query = "insert into agent(name, phone, city) values(?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, city);
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
    
    public void addAgents(String name, String phone, String city, Callback<SQLException> callback) {
        new Thread(() -> addAgentsInternal(name, phone, city, callback)).start();
    }
    
    private synchronized void removeAgentsInternal(Agent agent,
            Callback<SQLException> callback) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("delete from agent where id = " + agent.getId());
            connection.commit();
            Platform.runLater(() -> {
                callback.call(null);
            });
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }
    
    public void removeAgents(Agent agent, Callback<SQLException> callback) {
        new Thread(() -> removeAgentsInternal(agent, callback)).start();
    }
    
    private synchronized void changeAgentNameInternal(Agent agent,
            Callback<SQLException> callback) {
        String query = "update agent set name = ? where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, agent.getName());
            statement.setInt(2, agent.getId());
            statement.execute();
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }
    
    public void changeAgentName(Agent agent, Callback<SQLException> callback) {
        new Thread(() -> changeAgentNameInternal(agent, callback)).start();
    }
    
    private synchronized void changeAgentPhoneInternal(Agent agent,
            Callback<SQLException> callback) {
        String query = "update agent set phone = ? where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, agent.getPhone());
            statement.setInt(2, agent.getId());
            statement.execute();
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }
    
    public void changeAgentPhone(Agent agent, Callback<SQLException> callback) {
        new Thread(() -> changeAgentNameInternal(agent, callback)).start();
    }
    
    private synchronized void changeAgentCityInternal(Agent agent,
            Callback<SQLException> callback) {
        String query = "update agent set city = ? where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, agent.getCity());
            statement.setInt(2, agent.getId());
            statement.execute();
        } catch (SQLException e) {
            rollback();
            Platform.runLater(() -> callback.call(e));
        }
    }
    
    public void changeAgentCity(Agent agent, Callback<SQLException> callback) {
        new Thread(() -> changeAgentNameInternal(agent, callback)).start();
    }
}
