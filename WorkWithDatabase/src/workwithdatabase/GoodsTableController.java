package workwithdatabase;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import workwithdatabase.models.Goods;

public class GoodsTableController extends GridPane implements Initializable {
    
    @FXML
    private TableView<Goods> goodsTable;
    @FXML
    private TextField name;
    @FXML
    private TextField measure;
    @FXML
    private Button add;
    @FXML
    private Button delete;
    
    private DatabaseConnection connection;

    public GoodsTableController() throws IOException {
        FXMLLoader deliveryWindowLoader
                = new FXMLLoader(getClass().getResource("goods_table.fxml"));
        deliveryWindowLoader.setController(this);
        deliveryWindowLoader.setRoot(this);
        deliveryWindowLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goodsTable.selectionModelProperty().addListener((observable) -> {
            delete.setDisable(null == goodsTable.getSelectionModel().getSelectedItem());
        });
        connection = WorkWithDatabase.getConnection();
        updateGoodsTable();
    }
    
    private void updateGoodsTable() {
        connection.getAllGoods((goods) -> {
            goodsTable.setItems(FXCollections.observableArrayList(goods));
        });
    }

    @FXML
    private void onAddGoods() {
        String nameValue = name.getText();
        if (nameValue.isEmpty()) {
            new Alert(AlertType.WARNING, "Поле \"Имя\" не должно быть пустым", ButtonType.OK)
                    .showAndWait();
            return;
        }
        String measureValue = measure.getText();
        if (measureValue.isEmpty()) {
            new Alert(AlertType.WARNING, "Поле \"Единицы\" не должно быть пустым", ButtonType.OK)
                    .showAndWait();
            return;
        }
        name.clear();
        measure.clear();
        add.setDisable(true);
        connection.addGoods(nameValue, measureValue, (error) -> {
            add.setDisable(false);
            if (null != error) {
                new Alert(AlertType.ERROR, "Не удалось добавтиь товар", ButtonType.OK)
                        .showAndWait();
                return;
            }
            updateGoodsTable();
        });
    }
    
    @FXML
    private void onDeleteGoods() {
    }

    @FXML
    private void onEditName(CellEditEvent<Goods, String> event) {
    }

    @FXML
    private void onEditMeasure(CellEditEvent<Goods, String> event) {
    }
}
