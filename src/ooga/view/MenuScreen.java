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

        buttonNames = Arrays.asList(new String[]{"Chess", "Checkers", "Othello"});
        buttons = new ButtonGroup(buttonNames);
        buttonArrange = buttons.getButtons();
        buttonArrange.setLayoutX(600);
        buttonArrange.setLayoutY(550);
        buttonArrange.getStyleClass().add("vbox");
        root.getChildren().add(buttonArrange);

        arrangeMenuImages();

    }

    public void buttonListener(EventHandler<ActionEvent> e) {
        for (Button b: buttons.getButtonList()) {
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
        Group settingsRoot = new Group();
        Scene settingsScene = setUpPopUp(settingsStage, settingsRoot, e);
        settingsStage.setScene(settingsScene);
        settingsStage.show();
    }

    private Scene setUpPopUp(Stage settingsStage, Group settingsRoot, EventHandler<ActionEvent> event) {
        ButtonGroup colorGroup = new ButtonGroup(List.of("White", "Black"));
        ButtonGroup fileGroup = new ButtonGroup(List.of("Default settings", "Load custom XML"));
        HBox hbox = new HBox();
        hbox.getChildren().addAll(colorGroup.getButtons(), fileGroup.getButtons());
        Button goButton = new Button("Go!");
        settingsRoot.getChildren().addAll(hbox, goButton);
        goButton.setOnAction(e -> {
            settingsStage.close();
            event.handle(e);
        });
        return new Scene(settingsRoot, 400, 400);
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
