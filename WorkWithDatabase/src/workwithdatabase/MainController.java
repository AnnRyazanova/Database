package workwithdatabase;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private <T> void runDialog(String title, Parent root) throws IOException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(stage);
        window.setTitle(title);
        window.setResizable(false);
        window.setScene(new Scene(root));
        window.showAndWait();
    }

    @FXML
    private void onMakeDelivery() throws IOException {
        runDialog("Доставка", new DeliveryController());
    }

    @FXML
    private void onMakeTransfer() throws IOException {
        runDialog("Транспортировка", new TransferController());
    }

    @FXML
    private void onMakeSupply() throws IOException {
        runDialog("Поставка", new SupplyController());
    }
    
    @FXML
    private void onGoodsTable() throws IOException {
        runDialog("Справочник товаров", new GoodsTableController());
    }
}
