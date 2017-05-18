package workwithdatabase;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainWindowController {
    private Stage stage;
    
    public void setStage(Stage stage) { this.stage = stage; }
    
    @FXML
    private void onMakeDelivery() throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        FXMLLoader deliveryWindowLoader = new FXMLLoader(getClass().getResource("delivery.fxml"));
        dialog.setScene(new Scene((Parent) deliveryWindowLoader.load()));
        dialog.showAndWait();
    }

    @FXML
    private void onMakeTransfer() {
    }

    @FXML
    private void onMakeSupply() {
    }
}
