
package workwithdatabase;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import workwithdatabase.DatabaseConnection.Column;

public class DeliveryWindow {
    private Stage dialogStage;
    
    private static class GoodsItem {
        public final int id;
        public final String nomenclature;
        public final int count;
        public final String measure;
        
        public GoodsItem(int id, String nomenclature, int count, String measure) {
            this.id = id;
            this.nomenclature = nomenclature;
            this.count = count;
            this.measure = measure;
        }
    }
    
    public DeliveryWindow(Stage primaryStage, DatabaseConnection connection) {
        dialogStage = new Stage();
        dialogStage.setTitle("Transfer");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.resizableProperty().set(false);
        dialogStage.initOwner(primaryStage);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid);
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
        
        ObservableList<GoodsItem> goodsOptions = FXCollections.observableArrayList();
        TableView<GoodsItem> goodsTable = new TableView<>(goodsOptions);
        goodsTable.setMinHeight(200);
        TableColumn<GoodsItem, String> goodsColumn = new TableColumn<>("Товары");
        goodsColumn.setCellValueFactory((cellData) -> {
            return new SimpleStringProperty(cellData.getValue().nomenclature);
        });
        TableColumn<GoodsItem, Number> countColumn = new TableColumn<>("Кол-во");
        countColumn.setCellValueFactory((cellData) -> {
            return new SimpleIntegerProperty(cellData.getValue().count);
        });
        goodsTable.getColumns().addAll(goodsColumn, countColumn);
        grid.add(goodsTable, 0, 3, 2, 1);

        Button btn = new Button("Добавить товары");
        grid.add(btn, 0, 4);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int selected = warehouseBox.getSelectionModel().getSelectedIndex();
            int idWarehouse = warehouseList.ids.get(selected);
            ArrayList<Goods> goods = connection.getGoodsAtWarehouse(idWarehouse);
            AddGoodsWindow.AddGoodsListener listener = new AddGoodsWindow.AddGoodsListener() {
                @Override
                public void onAddGoods(int index, int count) {
                    Goods good = goods.get(index);
                    int id = good.id.get();
                    String nomenclature = good.nomenclature.get();
                    String measure = good.measure.get();
                    goodsOptions.add(new GoodsItem(id, nomenclature, count, measure));
                }
            };
            ArrayList<String> nomenclatures = new ArrayList<>();
            for (Goods good : goods) nomenclatures.add(good.nomenclature.get());
            new AddGoodsWindow(dialogStage, connection, nomenclatures, listener).show();
        }});
        
        Button btnOrder = new Button("Сделать заказ");
        grid.add(btnOrder, 1, 4);
    }
    
    public void show() {
        dialogStage.showAndWait(); 
    }
    
}
