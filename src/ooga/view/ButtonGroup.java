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
    private HBox box;
    private List<HBox> hboxes;
    private VBox vbox;
    private List<Button> buttons;
    private List<String> buttonNames;

    public ButtonGroup(List<String> buttonNames) {
        vbox = new VBox();
        vbox.getStyleClass().add("buttonvbox");
        buttons = new ArrayList<>();
        this.buttonNames = buttonNames;
        createButtons();
        addElementsToVBox();

    }

    private void createButtons() {
        for (String buttonName : buttonNames) {
            Button temp = new Button(buttonName);
            temp.setMinHeight(60);
            temp.setMinWidth(250);
            buttons.add(temp);
        }
    }

    private void addElementsToVBox() {
        for (Button button : buttons) {
            vbox.getChildren().add(button);
        }
    }

    public VBox getButtons() {
        return vbox;
    }

    public List<Button> getButtonList() {
        return buttons;
    }


}
