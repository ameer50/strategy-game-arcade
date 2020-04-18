package ooga.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ButtonGroup {

    private static ResourceBundle myResources =
            ResourceBundle.getBundle("resources", Locale.getDefault());
    private List<Button> buttons;
    private String style;

    public ButtonGroup(List<String> buttonNames, String style) {
        buttons = new ArrayList<>();
        this.style = style;
        createButtons(buttonNames);
    }

    private void createButtons(List<String> buttonNames) {
        for (String buttonName : buttonNames) {
            Button button = new Button(buttonName);
            button.getStyleClass().add(style);
            buttons.add(button);
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
