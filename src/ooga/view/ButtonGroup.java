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

    private List<Button> buttons;

    public ButtonGroup(List<String> buttonNames) {
        buttons = new ArrayList<>();
        createButtons(buttonNames);
    }

    private void createButtons(List<String> buttonNames) {
        for (String buttonName : buttonNames) {
            Button button = new Button(buttonName);
            buttons.add(button);
        }
    }

    public void addStyle(String style){
        for (Button button : buttons) {
            button.getStyleClass().add(style);
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
