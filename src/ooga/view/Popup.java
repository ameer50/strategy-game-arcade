package ooga.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Popup {

    Stage stage;
    Scene scene;
    String stylesheet;
    VBox buttonBox;


    private BorderPane root;
    public Popup(int stageWidth, int stageHeight, String stylesheet ){
        stage = new Stage();
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        root = new BorderPane();
        this.stylesheet = stylesheet;
    }


    public void getNewPopup(){
        root = new BorderPane();
        scene = new Scene(root);
        scene.getStylesheets().add(stylesheet);
        stage.setScene(scene);
        stage.show();
        //return root;

    }

    public Stage getStage(){
        return stage;
    }

    public ButtonGroup buttonsToDisplay(List<String> buttonNames, String buttonStyle, int layoutX, int layoutY, String vboxStyle){
        buttonBox = new VBox();
        ButtonGroup buttons = new ButtonGroup(buttonNames, buttonStyle);

        for (Button b: buttons.getButtons()) {
            b.getStyleClass().add(buttonStyle);
            buttonBox.getChildren().add(b);
        }

        root.getChildren().add(buttonBox);
        buttonBox.setLayoutX(layoutX);
        buttonBox.setLayoutY(layoutY);
        buttonBox.getStyleClass().add(vboxStyle);

        return buttons;
    }

    public VBox getButtonBox(){
        return  buttonBox;
    }





}
