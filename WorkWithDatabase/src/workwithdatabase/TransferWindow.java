
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

public class TransferWindow {
    private Stage dialogStage;
    
    public TransferWindow(Stage primaryStage, DatabaseConnection connection) {
        dialogStage = new Stage();
        dialogStage.setTitle("Transfer");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.resizableProperty().set(false);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid);
        dialogStage.setScene(scene); 

        Label from = new Label("Откуда выполнить доставку:");
        grid.add(from, 0, 1);

        Column warehouseList = connection.selectColumn("WAREHOUSE", "ID", "NAME");
        ObservableList<String> options = FXCollections.observableArrayList(warehouseList.names);
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
            int selected = warehouse2Box.getSelectionModel().getSelectedIndex();
            int idWarehouse = warehouseList.ids.get(selected);
            ArrayList<Goods> goods = connection.getGoodsAtWarehouse(idWarehouse);
            AddGoodsWindow.AddGoodsListener listener = new AddGoodsWindow.AddGoodsListener() {
                @Override
                public void onAddGoods(int index, int count) {
                    // ToDo: some shit
                }
            };
            ArrayList<String> nomenclatures = new ArrayList<>();
            for (Goods good : goods) nomenclatures.add(good.nomenclature.get());
            AddGoodsWindow window 
                    = new AddGoodsWindow(dialogStage, connection, nomenclatures, listener);
            window.show();
        }});
    }
    
    public void show() {
        dialogStage.showAndWait(); 
    }
}
