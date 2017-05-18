
package workwithdatabase;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import workwithdatabase.models.Client;
import workwithdatabase.models.Goods;
import workwithdatabase.models.Warehouse;

public class DeliveryWindowController implements Initializable {
    public static final class SelectedGoods {
        public final ObjectProperty<Goods> goods = new SimpleObjectProperty<>();
        public final IntegerProperty count = new SimpleIntegerProperty();
        
        public SelectedGoods() { this(new Goods(), 0); }
        
        public SelectedGoods(Goods goods, int count) {
            setGoods(goods);
            setCount(count);
        }

        public Goods getGoods() { return goods.get(); }
        public int getCount()    { return count.get(); }
        public void setGoods(Goods goods) { this.goods.set(goods); }
        public void setCount(int count)    { this.count.set(count); }
    };
    
    @FXML
    private GridPane root;
    @FXML
    private ComboBox<Warehouse> warehouse;
    @FXML
    private ComboBox<Client> client;
    @FXML
    private TableView<SelectedGoods> selectedGoods;
    @FXML
    private TextField count;
    @FXML
    private ComboBox<Goods> goods;
    @FXML
    private Button addGoods;
    @FXML
    private Button removeGoods;
    @FXML
    private Button placeOrder;
    @FXML
    private Label measure;
    
    private DatabaseConnection connection;
    
    private boolean isClientSelected = false;
    private boolean isWarehouseSelected = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedGoods.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    removeGoods.setDisable(null == newValue);
                });
        connection = WorkWithDatabase.getConnection();
        connection.getAllWarehouses((warehouses) -> { 
            warehouse.setItems(FXCollections.observableArrayList(warehouses));
            return null;
        });
        connection.getAllClients((clients) -> { 
            client.setItems(FXCollections.observableArrayList(clients));
            return null;
        });
    }
    
    @FXML
    private void onWarehouseChosen() {
        Warehouse chosenWarehouse = warehouse.getSelectionModel().getSelectedItem();
        isWarehouseSelected = chosenWarehouse != null;
        placeOrder.setDisable(!isClientSelected || !isWarehouseSelected);
        if (!isWarehouseSelected) return;
        selectedGoods.getItems().clear();
        connection.getGoodsAtWarehouse(chosenWarehouse.id, (goodsOptions) -> {
            goods.setItems(FXCollections.observableArrayList(goodsOptions));
            goods.setDisable(false);
            count.setDisable(false);
            return null;
        });
    }
    
    @FXML
    private void onClientChosen() {
        isClientSelected = client.getSelectionModel().getSelectedItem() != null;
        placeOrder.setDisable(!isClientSelected || !isWarehouseSelected);
    }
    
    @FXML
    private void onGoodsChosen() {
        Goods selected = goods.getSelectionModel().getSelectedItem();
        if (null == selected) {
            measure.setText("  ");
            return;
        }
        measure.setText(selected.measure);
        addGoods.setDisable(false);
    }
    
    @FXML
    private void onAddGoods() {
        Goods selected = goods.getSelectionModel().getSelectedItem();
        if (null == selected) {
            new Alert(AlertType.WARNING, "Выберите товар", ButtonType.OK).show();
            return;
        }
        int countValue;
        try {
            countValue = Integer.parseInt(count.getText());
            count.setText("");
        } catch (NumberFormatException e) {
            new Alert(AlertType.WARNING, "Не могу распознать количество", ButtonType.OK).show();
            return;
        }
        selectedGoods.getItems().add(new SelectedGoods(selected, countValue));
    }
    
    @FXML
    private void onRemoveGoods() {
        selectedGoods.getItems().remove(selectedGoods.getSelectionModel().getSelectedIndex());
    } 
    
    @FXML
    private void onPlaceOrder() {
        root.setDisable(true);
        Warehouse selectedWarehouse = warehouse.getSelectionModel().getSelectedItem();
        Client selectedClient = client.getSelectionModel().getSelectedItem();
        ObservableList<SelectedGoods> listOfSelectedGoods = selectedGoods.getItems();
        connection.createDelivery(selectedWarehouse, selectedClient, listOfSelectedGoods, 
                (SQLException error) -> {
                    root.setDisable(false);
                    if (null != error) {
                        new Alert(AlertType.ERROR, "Ошибка: " + error.getLocalizedMessage(), 
                                ButtonType.OK).show();
                    } else {
                        new Alert(AlertType.INFORMATION, "Готово!", ButtonType.OK).showAndWait();
                        Stage stage = (Stage) placeOrder.getScene().getWindow();
                        stage.close();
                    }
                    return null;
                });
    } 
}
