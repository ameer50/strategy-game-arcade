package ooga.view;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

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

    public void addStyle(String style) {
        for (Button button : buttons) {
            button.getStyleClass().add(style);
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
