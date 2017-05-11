package workwithdatabase;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.event.*;
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
                if(delivery.isSelected())
                    System.out.println("TO DO 1"); // TO DO
                else if (transfer.isSelected())
                    System.out.println("TO DO 2"); // TO DO
                else if (supply.isSelected())
                    System.out.println("TO DO 3"); // TO DO
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
