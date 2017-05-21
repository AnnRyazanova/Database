package workwithdatabase;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import workwithdatabase.models.Goods;
import workwithdatabase.models.Warehouse;

public class TransferController extends GridPane implements Initializable {

    @FXML
    private GridPane root;
    @FXML
    private ComboBox<Warehouse> source;
    @FXML
    private ComboBox<Warehouse> destination;
    @FXML
    private TableView<SelectedGoods> selectedGoods;
    @FXML
    private TableColumn<SelectedGoods, String> measureColumn;
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

    private boolean isSourceSelected = false;
    private boolean isDestinationSelected = false;

    public TransferController() throws IOException {
        FXMLLoader deliveryWindowLoader = new FXMLLoader(getClass().getResource("transfer.fxml"));
        deliveryWindowLoader.setController(this);
        deliveryWindowLoader.setRoot(this);
        deliveryWindowLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        measureColumn.setCellValueFactory(((cell) -> {
            return new SimpleStringProperty(cell.getValue().getGoods().getMeasure());
        }));
        selectedGoods.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    removeGoods.setDisable(null == newValue);
                });
        connection = WorkWithDatabase.getConnection();
        connection.getAllWarehouses((warehouses) -> {
            source.setItems(FXCollections.observableArrayList(warehouses));
            destination.setItems(FXCollections.observableArrayList(warehouses));
            if (!warehouses.isEmpty()) {
                source.getSelectionModel().selectFirst();
                destination.getSelectionModel().selectFirst();
            }
        });
    }

    @FXML
    private void onSourceChosen() {
        Warehouse chosenWarehouse = source.getSelectionModel().getSelectedItem();
        isDestinationSelected = chosenWarehouse != null;
        placeOrder.setDisable(!isSourceSelected || !isDestinationSelected);
        if (!isDestinationSelected) {
            return;
        }
        selectedGoods.getItems().clear();
        connection.getGoodsAtWarehouse(chosenWarehouse.getId(), (goodsOptions) -> {
            goods.setItems(FXCollections.observableArrayList(goodsOptions));
            goods.setDisable(false);
            count.setDisable(false);
        });
    }

    @FXML
    private void onDestinationChosen() {
        isSourceSelected = destination.getSelectionModel().getSelectedItem() != null;
        placeOrder.setDisable(!isSourceSelected || !isDestinationSelected);
    }

    @FXML
    private void onGoodsChosen() {
        Goods selected = goods.getSelectionModel().getSelectedItem();
        if (null == selected) {
            measure.setText("  ");
            return;
        }
        measure.setText(selected.getMeasure());
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
            if (countValue <= 0){
                new Alert(AlertType.WARNING, "Количество должно быть положительным числом", ButtonType.OK).show();
                return;
            }
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
        Warehouse selectedSource = source.getSelectionModel().getSelectedItem();
        Warehouse selectedDestination = destination.getSelectionModel().getSelectedItem();
        ObservableList<SelectedGoods> listOfSelectedGoods = selectedGoods.getItems();
        connection.createTransfer(selectedSource, selectedDestination, listOfSelectedGoods,
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
                });
    }
}
