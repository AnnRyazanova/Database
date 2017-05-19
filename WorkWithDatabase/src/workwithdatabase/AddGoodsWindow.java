package workwithdatabase;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AddGoodsWindow {

    public interface AddGoodsListener {

        void onAddGoods(int index, int count);
    }

    private final Label labelLoading;
    private final GridPane gridPaneContent;
    private ComboBox comboBoxGoods;

    private final Stage dialogStage;

    public AddGoodsWindow(Stage parent, DatabaseConnection connection, AddGoodsListener listener) {
        dialogStage = new Stage();
        dialogStage.setTitle("Добавить товар");
        dialogStage.resizableProperty().set(false);
        gridPaneContent = new GridPane();
        gridPaneContent.setVisible(false);
        gridPaneContent.setHgap(5);
        gridPaneContent.setVgap(5);
        gridPaneContent.setPadding(new Insets(25));
        gridPaneContent.add(new Label("Товар"), 0, 0);
        gridPaneContent.add(new Label("Количество"), 1, 0);

        comboBoxGoods = new ComboBox();
        gridPaneContent.add(comboBoxGoods, 0, 1);

        IntegerTextField count = new IntegerTextField();
        gridPaneContent.add(count, 1, 1);

        Button add = new Button();
        add.setText("Добавить");
        gridPaneContent.add(add, 2, 1);
        add.setOnAction((ActionEvent event) -> {
            int selected = comboBoxGoods.getSelectionModel().getSelectedIndex();
            int countValue = Integer.parseInt(count.getText());
            listener.onAddGoods(selected, countValue);
        });
        StackPane stackPane = new StackPane();
        labelLoading = new Label("Загрузка...");
        stackPane.getChildren().addAll(labelLoading, gridPaneContent);
        dialogStage.setScene(new Scene(stackPane));
        dialogStage.show();
    }

    public void setGoods(List<String> goods) {
        comboBoxGoods.setItems(FXCollections.observableArrayList(goods));
        labelLoading.setVisible(false);
        gridPaneContent.setVisible(true);
    }

}
