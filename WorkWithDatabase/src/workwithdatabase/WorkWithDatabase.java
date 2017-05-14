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
import javafx.stage.Modality;
import javax.swing.*;

public class WorkWithDatabase extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        WorkWithDatabase database = new WorkWithDatabase();
        database.connect();
        
        primaryStage.setTitle("Work with database");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
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
                    ArrayList<String> warehouseList = database.selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouseBox = new ComboBox(options);
                    //warehouseBox.setValue("Option 1");
                    
                    grid.add(warehouseBox, 1, 1);                    
 
                    Label client = new Label("Выберите клиента:");
                    grid.add(client, 0, 2);
                    
                    String infoClient[] = {"ID", "NAME", "CITY", "ADDRESS", "PHONE"};
                    ArrayList<String> clientList = database.selectAll("CLIENT", infoClient, "NAME");
                    
                    ObservableList<String> options2 = FXCollections.observableArrayList(clientList);
                    final ComboBox clientBox = new ComboBox(options2);
                    //clientBox.setValue("Option 1");
                    
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
                    ArrayList<String> warehouseList = database.selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouse1Box = new ComboBox(options);
                    //warehouse1Box.setValue("Option 1");
                    
                    grid.add(warehouse1Box, 1, 1);    
 
                    Label to = new Label("Куда выполнить доставку:");
                    grid.add(to, 0, 2);
                    
                    final ComboBox warehouse2Box = new ComboBox(options);
                    //warehouse2Box.setValue("Option 1");
                    
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
                    ArrayList<String> agentList = database.selectAll("AGENT", infoAgent, "NAME");
                    
                    ObservableList<String> options = FXCollections.observableArrayList(agentList);
                    final ComboBox agentBox = new ComboBox(options);
                   //agentBox.setValue("Option 1");
                    
                    grid.add(agentBox, 1, 1);    
 
                    Label warehouse = new Label("Выберите склад:");
                    grid.add(warehouse, 0, 2);
                    
                    String infoWarehouse[] = {"ID", "CITY", "NAME"};
                    ArrayList<String> warehouseList = database.selectAll("WAREHOUSE", infoWarehouse, "NAME");
                    
                    ObservableList<String> options2 = FXCollections.observableArrayList(warehouseList);
                    final ComboBox warehouseBox = new ComboBox(options2);
                    //warehouseBox.setValue("Option 1");
                    
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
                }
                else 
                    JOptionPane.showMessageDialog(null, "Выберите тип доставки!", "Не выбран тип доставки", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
    
    private Properties pr;
    private String databaseURL;
    private String user;
    private String password;
    private String driverName;
    private Driver d;
    private Connection c;
    private Statement s;
    private ResultSet rs;
    
    public boolean connect() {
        pr = new Properties();
        try {
            FileInputStream inp = new FileInputStream("database.prop");
            pr.load(inp);
            inp.close();
        } catch (IOException e) {
            return false;
        }

        databaseURL = pr.getProperty("dbURL");
        user = pr.getProperty("user");
        password = pr.getProperty("password");
        driverName = pr.getProperty("driver");

        try {
            Class.forName(driverName);
            c = DriverManager.getConnection(databaseURL, user, password);

        } catch (ClassNotFoundException e) {
            System.out.println("Fireberd JDBC driver not found");
        } catch (SQLException e) {
            System.out.println("SQLException" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
            }
            try {
                if (s != null) s.close();
            } catch (SQLException e) {
            }
            try {
                if (c != null) c.close();
            } catch (SQLException e) {
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    static public String[][] data;
    
    public ArrayList<String> selectAll(String from, String[] args, String field) {
        ResultSet rs = null;
        ArrayList<String> listNames = new ArrayList();

        try {
            Class.forName(driverName);
            c = DriverManager.getConnection(databaseURL, user, password);
            DatabaseMetaData dbM = c.getMetaData();
            rs = dbM.getTables(null, null, "%", new String[]{"TABLE", "VIEW"});
            while (rs.next()) {
            }
            s = c.createStatement();
            rs = s.executeQuery("select * from " + from);
            ResultSetMetaData rsM = rs.getMetaData();

            int i = 0;
            data = new String[50][args.length];
            while (rs.next()) {
                for (int k = 0; k < args.length; k++){ 
                    data[i][k] = rs.getString(args[k]);
                    if (args[k].equals(field))
                        listNames.add(data[i][k]);
                }
                i++;
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Fireberd JDBC driver not found");
        } catch (SQLException e) {
            System.out.println("SQLException" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
            }
            try {
                if (s != null) s.close();
            } catch (SQLException e) {
            }
            try {
                if (c != null) c.close();
            } catch (SQLException e) {
            }
        }
        return listNames;
    }
    
}
