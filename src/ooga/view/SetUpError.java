package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Locale;
import java.util.ResourceBundle;

public class SetUpError extends RuntimeException {

    private static final int STAGE_WIDTH = 500;
    private static final int STAGE_HEIGHT = 500;
    private static final String popupStyle = "PopupStyleSheet";
    private static final String textStyle = "prefer";
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private Popup pop;
    private Button returnToMenuButton;
    
    public SetUpError(String errorType) {
        pop = new Popup(STAGE_WIDTH, STAGE_HEIGHT, res.getString(popupStyle));
        VBox box = pop.getPopupBox();

        String errorMessage = String.format("ERROR: %s", errorType);
        Text text = new Text(errorMessage);
        text.getStyleClass().add(textStyle);

        returnToMenuButton = new Button("Return to Menu");
        box.getChildren().addAll(text, returnToMenuButton);
    }

    public void show() {
        pop.getNewPopup();
    }

    public void setReturnToMenuFunction(EventHandler<ActionEvent> e) {
        returnToMenuButton.setOnAction(event -> {
            pop.closePopup();
            e.handle(event);
        });
    }
}
