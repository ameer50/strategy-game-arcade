package ooga.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Popup {

    private static final String vboxStyle = "vbox";
    private Stage stage;
    private Scene scene;
    private String stylesheet;
    private VBox popupBox;
    private BorderPane root;

    public Popup(int stageWidth, int stageHeight, String stylesheet){
        stage = new Stage();
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        root = new BorderPane();
        this.stylesheet = stylesheet;
    }

    public void getNewPopup(){
        root = new BorderPane();
        popupBox = new VBox();
        root.setCenter(popupBox);
        popupBox.setAlignment(Pos.CENTER);
        scene = new Scene(root);
        scene.getStylesheets().add(stylesheet);
        stage.setScene(scene);
    }

    public Stage getStage(){
        return stage;
    }

    public void addButtonGroup(ButtonGroup buttons){
        VBox buttonBox = new VBox();
        for (Button b: buttons.getButtons()) {
            buttonBox.getChildren().add(b);
        }
        buttonBox.getStyleClass().add(vboxStyle);
        popupBox.getChildren().add(buttonBox);
    }

    public VBox getPopupBox(){
        return popupBox;
    }

    public void closePopup(){
        stage.close();
    }

    public void setPopupStageTitle(String title){
        stage.setTitle(title);
    }
}
