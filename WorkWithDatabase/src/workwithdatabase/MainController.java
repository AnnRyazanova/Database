package workwithdatabase;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onMakeDelivery() throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setResizable(false);
        dialog.setScene(new Scene(new DeliveryController()));
        dialog.showAndWait();
    }

    @FXML
    private void onMakeTransfer() throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setResizable(false);
        dialog.setScene(new Scene(new TransferController()));
        dialog.showAndWait();
    }

    @FXML
    private void onMakeSupply() throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setResizable(false);
        dialog.setScene(new Scene(new SupplyController()));
        dialog.showAndWait();
    }
}
