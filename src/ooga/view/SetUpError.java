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
    public static final String RETURN_TO_MENU = "Return to Menu";
    public static final String CLOSE = "close";
    public static final String VBOX = "vbox";
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private Popup pop;
    private Button returnToMenuButton;

    public SetUpError(String errorType) {
        pop = new Popup(STAGE_WIDTH, STAGE_HEIGHT, res.getString(popupStyle));
        pop.getNewPopup();
        VBox box = pop.getPopupBox();

        String errorMessage = String.format("%s", errorType);
        Text text = new Text(errorMessage);
        text.getStyleClass().add(textStyle);

        returnToMenuButton = new Button(RETURN_TO_MENU);
        returnToMenuButton.getStyleClass().add(CLOSE);
        box.getChildren().addAll(text, returnToMenuButton);
        box.getStyleClass().add(VBOX);
    }

    public void show() {
        pop.getStage().show();
    }

    public void setReturnToMenuFunction(EventHandler<ActionEvent> e) {
        returnToMenuButton.setOnAction(event -> {
            pop.closePopup();
            e.handle(event);
        });
    }
}
