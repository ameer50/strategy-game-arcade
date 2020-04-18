package ooga.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TextFieldGroup {

    private static ResourceBundle myResources =
            ResourceBundle.getBundle("resources", Locale.getDefault());
    private List<Text> texts;
    private String style;

    public TextFieldGroup(List<String> buttonNames, String style) {
        texts = new ArrayList<>();
        this.style = style;
    }

    private void createButtons(String textName, String textFieldName, int textFieldWidth) {

        VBox textFieldBox = new VBox();

        Text text = new Text();
        text.setText(textName);
        text.getStyleClass().add("playername");

        TextField textField = new TextField();
        textField.setPromptText(textFieldName);
        textField.setMinWidth(textFieldWidth);
        textField.getStyleClass().add("file-text-field");

        textFieldBox.getChildren().addAll(text, textField);
        textFieldBox.setAlignment(Pos.CENTER);
        textFieldBox.getStyleClass().add("vbox");


    }

    public List<Text> getTexts() {
        return texts;
    }
}
