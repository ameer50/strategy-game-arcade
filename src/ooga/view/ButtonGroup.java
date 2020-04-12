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
    private double width;
    private double height;

    public ButtonGroup(List<String> buttonNames, double width, double height) {
        buttons = new ArrayList<>();
        this.width = width;
        this.height = height;
        createButtons(buttonNames, width, height);
    }

    private void createButtons(List<String> buttonNames, double width, double height) {
        for (String buttonName : buttonNames) {
            Button button = new Button(buttonName);
            button.setMinHeight(height);
            button.setMinWidth(width);
            buttons.add(button);
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
