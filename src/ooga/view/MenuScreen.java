package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.*;

public class MenuScreen {

    public static final int VBOX_Y = 600;
    public static final int POPUP_DIMENSIONS = 500;
    public static final String POPUP_PROMPT = "GAME PREFERENCE";
    public static final int PROMPT_X = 100;
    public static final int PROMPT_Y = 0;
    public static final int IMAGES_X = 325;
    public static final int IMAGES_Y = 220;
    public static final int IMAGE_GAP = 40;
    public static final int BUTTON_WIDTH = 20;
    public static final int BUTTON_HEIGHT = 40;
    public static final String DEFAULT_XML = "Default XML";
    public static final String CUSTOM_XML = "Load XML File";
    public static final String AI_OPPONENT = "AI Opponent";
    public static final String HUMAN_OPPONENT = "Human Opponent";
    public static final int IMAGE_SIZE = 220;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private final FileChooser fileChooser = new FileChooser();
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private ButtonGroup buttons;
    private String gameChoice;
    private String colorChoice;
    private String fileChoice;
    private boolean AIChoice;

    public MenuScreen(Stage stage){
        this.stage = stage;
        startView();
        stage.show();

    }

    private void startView() {
        root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(res.getString("MenuStageTitle"));
        scene.getStylesheets().add(res.getString("MenuStyleSheet"));

        buttons = new ButtonGroup(List.of("Chess", "Checkers", "Othello"), 250, 60, "button");
        VBox vbox = new VBox();
        for (Button b : buttons.getButtons()) {
            vbox.getChildren().add(b);
        }
        root.getChildren().add(vbox);
        // System.out.println(vbox.getWidth());
        vbox.setLayoutX(STAGE_WIDTH/2 - vbox.getWidth()/2);
        vbox.setLayoutY(VBOX_Y);
        vbox.getStyleClass().add("vbox");

        arrangeLogo();
        arrangeMenuImages();
    }

    public void setButtonListener(EventHandler<ActionEvent> e) {
        for (Button b: buttons.getButtons()) {
            b.setOnAction(event -> {
                gameChoice = b.getText();
                settingsPopUp(e);
                // e.handle(event);
            });
        }
    }

    public void settingsPopUp(EventHandler<ActionEvent> e) {
        Stage settingsStage = new Stage();
        settingsStage.setHeight(POPUP_DIMENSIONS);
        settingsStage.setWidth(POPUP_DIMENSIONS);
        StackPane settingsRoot = new StackPane();

        Scene settingsScene = new Scene(settingsRoot);
        settingsScene.getStylesheets().add(res.getString("MenuStyleSheet"));

        settingsStage.setScene(settingsScene);
        settingsStage.show();
        setUpPopUp(settingsStage, settingsRoot, e);
    }

    private void setUpPopUp(Stage settingsStage, StackPane settingsRoot, EventHandler<ActionEvent> event) {
        GridPane pane = setUpButtonPane();

        Label prompt = new Label(POPUP_PROMPT);
        prompt.getStyleClass().add("prompt");

        HBox promptBox = new HBox();
        promptBox.getChildren().add(prompt);
        promptBox.setLayoutX(PROMPT_X);
        promptBox.setLayoutY(PROMPT_Y);
        settingsRoot.getChildren().addAll(promptBox, pane);

        Button goButton = new Button("GO");
        goButton.getStyleClass().add(res.getString("SettingsButtons"));
        pane.add(goButton, 1,3);
        pane.getStyleClass().add("gpane");

        goButton.setOnAction(e -> {
            settingsStage.close();
            event.handle(e);
        });
    }

    private GridPane setUpButtonPane() {
        GridPane pane = new GridPane();
        List buttonLabels = List.of("White", "Black", DEFAULT_XML, CUSTOM_XML, AI_OPPONENT, HUMAN_OPPONENT);
        ButtonGroup buttonGroup = new ButtonGroup(buttonLabels, BUTTON_WIDTH, BUTTON_HEIGHT,
            res.getString("SettingsButtons"));

        int[] positionIndices = {0, 0, 2, 0, 0, 1, 2, 1, 0, 2, 2, 2};
        int i = 0;
        for (Button button: buttonGroup.getButtons()) {
            if (i==0 || i==2) {
                button.setOnAction((newEvent) -> {
                    assignColorChoice(button.getText());
                    button.setDisable(true);
                });
            } else if (i==4 | i==6) {
                button.setOnAction((newEvent) -> {
                    assignXMLFile(button.getText());
                    button.setDisable(true);
                });
            } else {
                button.setOnAction((newEvent) -> {
                    assignAIOpponent(button.getText());
                    button.setDisable(true);
                });
            }
            pane.add(button, positionIndices[i++], positionIndices[i++]);
            GridPane.setHalignment(button, HPos.CENTER);
            GridPane.setValignment(button, VPos.CENTER);
        }
        return pane;
    }

    private void arrangeLogo() {
        Text logo = new Text();
        logo.setText("STRATEGY GAME ARCADE");
        logo.getStyleClass().add("logo");
        logo.setFill(Color.AZURE);
        HBox logoBox = new HBox();
        logoBox.getChildren().add(logo);
        logoBox.setLayoutX(STAGE_WIDTH/2);
        logoBox.setLayoutY(150);
        logoBox.getStyleClass().add("logobox");
        root.getChildren().add(logoBox);
    }

    private void arrangeMenuImages() {
        GridPane grid = new GridPane();

        int[] dimensions = {IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE};
        int i = 0;
        int colIndex = 0;
        for (String s: List.of("CheckersIcon", "ChessIcon")) {
            StackPane stack = new StackPane();
            int width = dimensions[i++];
            int height = dimensions[i++];
            Rectangle background = new Rectangle(width+20, height+20);
            background.getStyleClass().add("picture-background");
            ImageView picture = new ImageView(new Image(res.getString(s), width, height, false, true));

            stack.getChildren().addAll(background, picture);

            grid.add(stack, colIndex, 0);
            colIndex += 2;
        }
        grid.setHgap(IMAGE_GAP);
        grid.setLayoutX(IMAGES_X);
        grid.setLayoutY(IMAGES_Y);
        root.getChildren().add(grid);
    }

    private void assignColorChoice(String choice) {
        colorChoice = choice;
        System.out.println(colorChoice); // ***
    }

    private void assignXMLFile(String choice) {
        if (choice.equals(DEFAULT_XML)) {
            this.fileChoice = String.format("resources/%s/default%s.xml", gameChoice, colorChoice);
        } else {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                fileChoice = file.getAbsolutePath();
                // TODO: Check that this works.
            }
        }
    }

    private void assignAIOpponent(String choice) {
        if (choice.equals(AI_OPPONENT)) {
            this.AIChoice = false;
        } else {
            this.AIChoice = true;
        }
    }

    public String getGameChoice() {
        return gameChoice;
    }

    public String getFileChoice(){
        return fileChoice;
    }

    public boolean getAIChoice() { return AIChoice; }
}
