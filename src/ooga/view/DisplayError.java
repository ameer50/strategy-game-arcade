package ooga.view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DisplayError {

    private static final int STAGE_WIDTH = 500;
    private static final int STAGE_HEIGHT = 500;
    private static final String popupStyle = "PopupStyleSheet";
    private static final String textStyle = "prefer";
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());

    
    public DisplayError(String errorType) {
        Popup pop = new Popup(STAGE_WIDTH, STAGE_HEIGHT, res.getString(popupStyle));
        pop.getNewPopup();

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("close");
        VBox box = pop.getPopupBox();

        String errorMessage = String.format("ERROR: %s", errorType);
        Text text = new Text(errorMessage);
        text.getStyleClass().add(textStyle);
        box.getChildren().addAll(text, closeButton);

        closeButton.setOnAction(e -> {
            pop.closePopup();
        });





    }
}
