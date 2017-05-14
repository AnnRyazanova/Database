package workwithdatabase;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class WorkWithDatabase extends Application {
    private Connection connection;
    
    @Override
    public void start(Stage primaryStage) {
/*        try {
            Parent parent = FXMLLoader.load(getClass().getResource("window.fxml"));
            primaryStage.setScene(new Scene(parent));
            primaryStage.show();
        } catch (Exception e) {
            throw new Error(e);
        }*/
        
        
        try {
            connect();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR, 
                    "Не могу подключиться к базе данных.");
            alert.showAndWait();
            System.exit(1);
        }
        
        primaryStage.setTitle("Работа с базой данных");
        
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));
        
        Text scenetitle = new Text("Выберите тип доставки : ");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
                
        ToggleGroup tg = new ToggleGroup();
        RadioButton delivery = new RadioButton("Delivery");
        delivery.setToggleGroup(tg);
        RadioButton transfer = new RadioButton("Transfer");
        transfer.setToggleGroup(tg);
        RadioButton supply = new RadioButton("Supply");
        supply.setToggleGroup(tg);
        HBox box = new HBox(20, delivery, transfer, supply);
        grid.add(box,1,3);
        
        Button btn = new Button("Продолжить");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 3, 4);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(delivery.isSelected()) {
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Transfer");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Scene scene = new Scene(grid, 400, 200);
                    dialogStage.setScene(scene); 
                    
                    Label warehouse = new Label("Выберите склад:");
                    grid.add(warehouse, 0, 1);
                                     
                    String infoWarehouse[] = {"ID", "CITY", "NAME"};
                    ArrayList<String> warehouseList = selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouseBox = new ComboBox(options);
                    warehouseBox.setValue(warehouseBox.getItems().get(0));
                    
                    grid.add(warehouseBox, 1, 1);                    
 
                    Label client = new Label("Выберите клиента:");
                    grid.add(client, 0, 2);
                    
                    String infoClient[] = {"ID", "NAME", "CITY", "ADDRESS", "PHONE"};
                    ArrayList<String> clientList = selectAll("CLIENT", infoClient, "NAME");
                    
                    ObservableList<String> options2 = FXCollections.observableArrayList(clientList);
                    final ComboBox clientBox = new ComboBox(options2);
                    clientBox.setValue(clientBox.getItems().get(0));
                    
                    grid.add(clientBox, 1, 2);  
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TO DO SECTION DELIVERY");
                    }});
                    
                    dialogStage.showAndWait(); 
                }
                else if (transfer.isSelected()) {
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Transfer");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Scene scene = new Scene(grid, 400, 200);
                    dialogStage.setScene(scene); 
                    
                    Label from = new Label("Откуда выполнить доставку:");
                    grid.add(from, 0, 1);
                    
                    String infoWarehouse[] = {"ID", "CITY", "NAME"};
                    ArrayList<String> warehouseList = selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouse1Box = new ComboBox(options);
                    warehouse1Box.setValue(warehouse1Box.getItems().get(0));
                    
                    grid.add(warehouse1Box, 1, 1);    
 
                    Label to = new Label("Куда выполнить доставку:");
                    grid.add(to, 0, 2);
                    
                    final ComboBox warehouse2Box = new ComboBox(options);
                    warehouse2Box.setValue(warehouse2Box.getItems().get(0));
                    
                    grid.add(warehouse2Box, 1, 2);    
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TO DO SECTION TRANSFER");
                    }});
                    
                    dialogStage.showAndWait(); 
                }
                else if (supply.isSelected()) {
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Supply");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Scene scene = new Scene(grid, 400, 200);
                    dialogStage.setScene(scene); 
                    
                    Label agent = new Label("Выберите агента:");
                    grid.add(agent, 0, 1);
                    
                    String infoAgent[] = {"ID", "NAME", "PHONE", "CITY"};
                    ArrayList<String> agentList = selectAll("AGENT", infoAgent, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(agentList);
                    final ComboBox agentBox = new ComboBox(options);
                    agentBox.setValue(agentBox.getItems().get(0));
                    
                    grid.add(agentBox, 1, 1);    
 
                    Label warehouse = new Label("Выберите склад:");
                    grid.add(warehouse, 0, 2);
                    
                    String infoWarehouse[] = {"ID", "CITY", "NAME"};
                    ArrayList<String> warehouseList = selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options2 = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouseBox = new ComboBox(options2);
                    warehouseBox.setValue(warehouseBox.getItems().get(0));
                    
                    grid.add(warehouseBox, 1, 2);    
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TO DO SECTION SUPPLY");
                    }});
                    
                    dialogStage.showAndWait(); 
                } else {
                    new Alert(AlertType.ERROR, "Не выбран тип доставки").showAndWait();
                }
            }
        });
        
        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
    
    public void connect() throws Exception {
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
    
    public static void main(String[] args) {
        launch(args);
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
