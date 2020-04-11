package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    private String gameSelected;

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


    public String getGameType(){
        return gameSelected;
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public void settingsPopUp(EventHandler<ActionEvent> e) {
        Stage settingsStage = new Stage();
        settingsStage.setHeight(500);
        settingsStage.setWidth(500);
        Group settingsRoot = new Group();
        Scene settingsScene = new Scene(settingsRoot);
        settingsScene.getStylesheets().add(res.getString("MenuStyleSheet"));
        settingsStage.setScene(settingsScene);
        settingsStage.show();
        setUpPopUp(settingsStage, settingsRoot, e);
    }

    private void setUpPopUp(Stage settingsStage, Group settingsRoot, EventHandler<ActionEvent> event) {
        ButtonGroup colorGroup = new ButtonGroup(List.of("White", "Black"), 20, 50);
        ButtonGroup fileGroup = new ButtonGroup(List.of("Default settings", "Load custom XML"), 20, 50);
        VBox vbox = new VBox();
        settingsRoot.getChildren().add(vbox);

        HBox hboxColors = new HBox();
        for (Button b : colorGroup.getButtons()) {
            hboxColors.getChildren().add(b);
        }
        //hboxColors.setLayoutX(200);
        //hboxColors.setLayoutY(50);
        hboxColors.setAlignment(Pos.CENTER);
        hboxColors.getStyleClass().add("hbox");

        HBox hboxFile = new HBox();
        for (Button b : fileGroup.getButtons()) {
            hboxFile.getChildren().add(b);
        }
        //hboxFile.setLayoutX(200);
        //hboxFile.setLayoutY(150);
        hboxFile.setAlignment(Pos.CENTER);
        hboxFile.getStyleClass().add("hbox");

        Button goButton = new Button("Go!");
        //goButton.setLayoutX(200);
        //goButton.setLayoutY(250);
        HBox hboxGo = new HBox(goButton);
        hboxGo.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(hboxColors, hboxFile, hboxGo);
        vbox.setAlignment(Pos.CENTER);
        System.out.println(vbox.getBoundsInLocal().getWidth());
        vbox.setLayoutX(250 - vbox.getBoundsInLocal().getWidth() / 2);
        vbox.setLayoutY(50);

        goButton.setOnAction(e -> {
            settingsStage.close();
            event.handle(e);
        });
    }

    private void arrangeMenuImages(){
        GridPane gridPane = new GridPane();
        ImageView chess = new ImageView(res.getString("CheckersIcon"));
        ImageView chess1 = new ImageView(res.getString("ChessIcon"));
        ImageView chess2 = new ImageView(res.getString("OthelloIcon"));
        chess.setFitHeight(140);
        chess.setFitWidth(250);
        chess1.setFitHeight(250);
        chess1.setFitWidth(250);
        chess2.setFitHeight(200);
        chess2.setFitWidth(200);
        gridPane.add(chess, 0, 0);
        gridPane.add(chess1, 1, 0);
        gridPane.add(chess2, 3, 0);
        gridPane.setHgap(40);
        gridPane.setLayoutX(160);
        gridPane.setLayoutY(190);
        root.getChildren().add(gridPane);
    }

}
