
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

public class SupplyWindow {
    private Stage dialogStage;
    
    public SupplyWindow(Stage primaryStage, DatabaseConnection connection) {
        dialogStage = new Stage();
        dialogStage.setTitle("Supply");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.resizableProperty().set(false);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid);
        dialogStage.setScene(scene); 

        Label agent = new Label("Выберите агента:");
        grid.add(agent, 0, 1);

        Column agentList = connection.selectColumn("AGENT", "ID", "NAME");
        ObservableList<String> options = FXCollections.observableArrayList(agentList.names);
        final ComboBox agentBox = new ComboBox(options);
        agentBox.setValue(agentBox.getItems().get(0));

        grid.add(agentBox, 1, 1);    

        Label warehouse = new Label("Выберите склад:");
        grid.add(warehouse, 0, 2);

        Column warehouseList = connection.selectColumn("WAREHOUSE", "ID", "NAME");
        ObservableList<String> options2 = FXCollections.observableArrayList(warehouseList.names);
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
            ArrayList<Goods> goods = connection.getAllGoods();
            AddGoodsWindow.AddGoodsListener listener = new AddGoodsWindow.AddGoodsListener() {
                @Override
                public void onAddGoods(int index, int count) {
                    // ToDo: 
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
