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
    private DatabaseConnection connection;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            connection = new DatabaseConnection();
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
                    DeliveryWindow deliveryWindow = 
                            new DeliveryWindow(primaryStage, connection);
                    deliveryWindow.show();
                } else if (transfer.isSelected()) {
                    TransferWindow transferWindow = 
                            new TransferWindow(primaryStage, connection);
                    transferWindow.show();
                } else if (supply.isSelected()) {
                    SupplyWindow supplyWindow =
                            new SupplyWindow(primaryStage, connection);
                    supplyWindow.show();
                } else {
                    new Alert(AlertType.ERROR, "Не выбран тип доставки").showAndWait();
                }
            }
        });
        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
