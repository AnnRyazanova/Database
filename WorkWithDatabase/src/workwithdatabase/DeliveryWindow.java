
package workwithdatabase;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import workwithdatabase.DatabaseConnection.Column;

public class DeliveryWindow {
    private Stage dialogStage;
    
    public DeliveryWindow(Stage primaryStage, DatabaseConnection connection) {
        dialogStage = new Stage();
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

        Column warehouseList = connection.selectColumn("WAREHOUSE", "ID", "NAME");
        ObservableList<String> options = FXCollections.observableArrayList(warehouseList.names);
        final ComboBox warehouseBox = new ComboBox(options);

        grid.add(warehouseBox, 1, 1);                    

        Label client = new Label("Выберите клиента:");
        grid.add(client, 0, 2);

        Column clientList = connection.selectColumn("CLIENT", "ID", "NAME");
        ObservableList<String> options2 = FXCollections.observableArrayList(clientList.names);
        final ComboBox clientBox = new ComboBox(options2);
        clientBox.setValue(clientBox.getItems().get(0));

        grid.add(clientBox, 1, 2);  

        Button btn = new Button("Добавить товары");
        HBox hbBtn = new HBox(10);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 4);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int selected = warehouseBox.getSelectionModel().getSelectedIndex();
            int idWarehouse = warehouseList.ids.get(selected);
            String from = "get_goods_at_warehouse(" + idWarehouse + ")";
            Column goods = connection.selectColumn(from, "ID", "NOMENCLATURE");
            AddGoodsWindow window 
                    = new AddGoodsWindow(dialogStage, connection, goods.names);
            window.show();
        }});

    }
    
    public void show() {
        dialogStage.showAndWait(); 
    }
    
}
