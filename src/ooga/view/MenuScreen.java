package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class MenuScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private final FileChooser fileChooser = new FileChooser();
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private ButtonGroup buttons;
    private String gameSelected;
    private String colorChoice;
    private String fileName;

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

        buttons = new ButtonGroup(List.of("Chess", "Checkers", "Othello"), 250, 60);
        VBox vbox = new VBox();
        for (Button b : buttons.getButtons()) {
            vbox.getChildren().add(b);
        }
        root.getChildren().add(vbox);
        System.out.println(vbox.getWidth());
        vbox.setLayoutX(STAGE_WIDTH / 2 - vbox.getWidth() / 2);
        vbox.setLayoutY(550);
        vbox.getStyleClass().add("vbox");

        arrangeMenuImages();

    }

    public void buttonListener(EventHandler<ActionEvent> e) {
        for (Button b: buttons.getButtons()) {
            b.setOnAction(event -> {
                gameSelected = b.getText();
                settingsPopUp(e);
                // e.handle(event);
            });
        }
    }

    public String getFileType(){
        return fileName;
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public void settingsPopUp(EventHandler<ActionEvent> e) {
        Stage settingsStage = new Stage();
        settingsStage.setHeight(500);
        settingsStage.setWidth(500);
        StackPane settingsRoot = new StackPane();
        Scene settingsScene = new Scene(settingsRoot);
        settingsScene.getStylesheets().add(res.getString("MenuStyleSheet"));
        settingsStage.setScene(settingsScene);
        settingsStage.show();
        setUpPopUp(settingsStage, settingsRoot, e);
    }

    private void setUpPopUp(Stage settingsStage, StackPane settingsRoot, EventHandler<ActionEvent> event) {

        GridPane pane = new GridPane();
        ButtonGroup colorGroup = new ButtonGroup(List.of("White", "Black", "Default Game", "Load XML File"), 20, 40);

        int[] position = {0, 0, 2, 0, 0, 1, 2, 1};
        int i = 0;
        for(Button b: colorGroup.getButtons()){
            if(i == 0 || i == 2){
                b.setOnAction((newEvent) -> {
                    setColorChoice(b.getText());
                    b.setDisable(true);
                });
            }else{
                b.setOnAction((newEvent) -> {
                    assignXMLFile(b.getText());
                    b.setDisable(true);
                });

            }
            b.getStyleClass().add(res.getString("SettingsButtons"));

            pane.add(b, position[i++], position[i++]);
            GridPane.setHalignment(b, HPos.CENTER);
            GridPane.setValignment(b, VPos.CENTER);
        }
        Button goButton = new Button("Go!");
        goButton.getStyleClass().add(res.getString("SettingsButtons"));
        pane.add(goButton, 1,2);

        pane.getStyleClass().add("gpane");
        settingsRoot.getChildren().add(pane);


        goButton.setOnAction(e -> {
            settingsStage.close();
            event.handle(e);
        });
    }

    private void arrangeMenuImages() {
        GridPane gridPane = new GridPane();

        int[] dimensions = {250, 140, 250, 250, 200, 200};
        int i = 0;
        int colIndex = 0;
        for(String s: List.of("CheckersIcon", "ChessIcon", "OthelloIcon" )){
            ImageView picture = new ImageView(new Image(res.getString(s), dimensions[i++], dimensions[i++], false, false));
            gridPane.add(picture, colIndex, 0);
            colIndex+=2;
        }

        gridPane.setHgap(40);
        gridPane.setLayoutX(160);
        gridPane.setLayoutY(190);

        root.getChildren().add(gridPane);
    }

    private void setColorChoice(String color) {
        colorChoice = color;
        System.out.println(colorChoice);
    }

    private void assignXMLFile(String choice) {
        if (choice.equals("Default Game")) {
            this.fileName = String.format("%s%s%s%s%s", "resources/", gameSelected,"/default", colorChoice , ".xml");;
        } else {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                fileName = file.getAbsolutePath();
            }
        }

    }

    public String getGameSelected() {
        return gameSelected;
    }
}
