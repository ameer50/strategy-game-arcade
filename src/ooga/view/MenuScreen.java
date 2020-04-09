package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class MenuScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private ButtonGroup buttons;
    private List<String> buttonNames;
    private String gameSelected;
    private VBox buttonArrange;

    public MenuScreen(Stage stage){

        this.stage = stage;
        startView();
        stage.show();

    }

    private void startView() {
        root = new BorderPane();


        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("MenuStageTitle"));
        scene.getStylesheets().add(res.getString("MenuStyleSheet"));

        buttonNames = Arrays.asList(new String[]{"Chess", "Checkers"});
        buttons = new ButtonGroup(buttonNames);
        buttonArrange = buttons.getButtons();
        buttonArrange.setLayoutX(600);
        buttonArrange.setLayoutY(400);
        root.getChildren().add(buttonArrange);

    }

    public void buttonListener(EventHandler<ActionEvent> e){

        for(Button b: buttons.getButtonList()){
            b.setOnAction(event -> {
                gameSelected = b.getText();
                e.handle(event);
            });
        }

    }


    public String getGameType(){
        return gameSelected;
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }


}
