package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DisplayError {

    public DisplayError(String errorType) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Program Error!");
        alert.setContentText(errorType);
        alert.showAndWait();
    }
}
