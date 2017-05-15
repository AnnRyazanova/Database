package workwithdatabase;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AddGoodsWindow {
    private Stage dialogStage;
    
    public AddGoodsWindow(Stage parent, DatabaseConnection connection,
             ArrayList<String> goodsList) {
        dialogStage = new Stage();
        dialogStage.setTitle("Добавить товар");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(25));
        gridPane.add(new Label("Товар"), 0, 0);
        gridPane.add(new Label("Количество"), 1, 0);
        
        ObservableList<String> options = FXCollections.observableArrayList(goodsList);
        final ComboBox goodsBox = new ComboBox(options);
        goodsBox.setValue(goodsBox.getItems().get(0));
        gridPane.add(goodsBox, 0, 1); 
        
        TextField count = new TextField();
        count.setPromptText("0");
        gridPane.add(count, 1, 1);
        
        Button add = new Button();
        add.setText("Добавить");
        gridPane.add(add, 2, 1);
        dialogStage.setScene(new Scene(gridPane));
    }
    
    public void show() {
        dialogStage.showAndWait();
    }
}
