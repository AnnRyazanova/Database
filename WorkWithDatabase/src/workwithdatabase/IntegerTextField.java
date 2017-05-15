package workwithdatabase;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class IntegerTextField extends TextField {
    public IntegerTextField() {
        setText("0");
        setTextFormatter(new TextFormatter<>((change) -> {
            try {
                Integer.parseInt(change.getControlNewText());
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }
    
    public int getValue() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            assert false;
            return 0;
        }
    }
}
