
package workwithdatabase;

import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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
        warehouseBox.setValue(warehouseBox.getItems().get(0));

        grid.add(warehouseBox, 1, 1);                    

        Label client = new Label("Выберите клиента:");
        grid.add(client, 0, 2);

        Column clientList = connection.selectColumn("CLIENT", "ID", "NAME");
        ObservableList<String> options2 = FXCollections.observableArrayList(clientList.names);
        final ComboBox clientBox = new ComboBox(options2);
        clientBox.setValue(clientBox.getItems().get(0));

        grid.add(clientBox, 1, 2);  
        
        ObservableList<String> goodsOptions = FXCollections.observableArrayList();
        TableView<String> goodsTable = new TableView<>(goodsOptions);
        TableColumn<String, String> goodsColumn = new TableColumn<>("Товары");
        goodsColumn.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                TableCell cell = new TableCell<String, String>();
                cell.setText(param.getText());
                return cell;
            }
        });
        goodsTable.getColumns().add(goodsColumn);
        grid.add(goodsTable, 0, 3, 2, 1);

        Button btn = new Button("Добавить товары");
        grid.add(btn, 0, 4);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int selected = warehouseBox.getSelectionModel().getSelectedIndex();
            int idWarehouse = warehouseList.ids.get(selected);
            String from = "get_goods_at_warehouse(" + idWarehouse + ")";
            Column goods = connection.selectColumn(from, "ID", "NOMENCLATURE");
            AddGoodsWindow.AddGoodsListener listener = new AddGoodsWindow.AddGoodsListener() {
                @Override
                public void onAddGoods(int id, String nomenclature, int count) {
                    goodsOptions.add(nomenclature);
                }
            };
            AddGoodsWindow window 
                    = new AddGoodsWindow(dialogStage, connection, goods, listener);
            window.show();
        }});
        
        Button btnOrder = new Button("Сделать заказ");
        grid.add(btnOrder, 1, 4);

    }
    
    public void show() {
        dialogStage.showAndWait(); 
    }
    
}
