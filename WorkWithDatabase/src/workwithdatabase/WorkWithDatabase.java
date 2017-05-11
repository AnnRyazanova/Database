package workwithdatabase;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javax.swing.*;

public class WorkWithDatabase extends Application {
    
    @Override
    public void start(Stage primaryStage) {
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
 
                    Label client = new Label("Выберите клиента:");
                    grid.add(client, 0, 2);
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
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
 
                    Label to = new Label("Куда выполнить доставку:");
                    grid.add(to, 0, 2);
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
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
 
                    Label warehouse = new Label("Выберите склад:");
                    grid.add(warehouse, 0, 2);
                    
                    Button btn = new Button("Сделать заказ");
                    HBox hbBtn = new HBox(10);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 0, 4);
                    
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
