package ooga.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    public static final String DEFAULT_XML = "Default Game";
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
    private String playerOneColor;
    private String playerTwoColor;
    private EventHandler<ActionEvent> goAction;
    private String selectedColor;
    private String playerOneName;
    private String playerTwoName;
    private String fileChoice;
    private boolean isOnePlayer;


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
        System.out.println("enter");
        for (Button b: buttons.getButtons()) {
            System.out.println("buttons exist");
            b.setOnAction(event -> {
                System.out.println("button clicked");
                gameChoice = b.getText();
                this.goAction = e;
                settingsPopUp();
            });
        }
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public void settingsPopUp() {
        Stage settingsStage = new Stage();
        settingsStage.setHeight(500);
        settingsStage.setWidth(500);
        createNewPopUpScene(settingsStage);

        setUpPlayerPopUp(settingsStage);
    }

    private BorderPane createNewPopUpScene(Stage settingsStage) {
        BorderPane root = new BorderPane();
        Scene newScene = new Scene(root);
        newScene.getStylesheets().add(res.getString("MenuStyleSheet"));
        settingsStage.setScene(newScene);
        settingsStage.show();
        return root;
    }

    private void setUpPlayerPopUp(Stage settingsStage) {
        BorderPane root = createNewPopUpScene(settingsStage);
        VBox vbox = new VBox();
        ButtonGroup playerOption = new ButtonGroup(List.of("One Player", "Two Player"), 20, 40, res.getString("SettingsButtons"));
        for (Button b: playerOption.getButtons()) {
            vbox.getChildren().add(b);
        }
        //root.getChildren().add(vbox);
        playerOption.getButtons().get(0).setOnAction(e -> {
            isOnePlayer = true;
            setUpColorPopUp(settingsStage);
        });
        playerOption.getButtons().get(1).setOnAction(e -> {
            isOnePlayer = false;
            setUpColorPopUp(settingsStage);
        });
        vbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        root.setCenter(vbox);
    }

    private void setUpColorPopUp(Stage settingsStage) {
        BorderPane root = createNewPopUpScene(settingsStage);
        VBox vbox = new VBox();
        List<String> possibleColors = new ArrayList<>();
        possibleColors.add("White");
        possibleColors.add("Black");
        ButtonGroup colorOption = new ButtonGroup(possibleColors, 20, 40, res.getString("SettingsButtons"));

        for (Button b: colorOption.getButtons()) {
            b.setOnAction(e -> {
                b.setDisable(true);
                playerOneColor = b.getText();
                possibleColors.remove(playerOneColor);
                playerTwoColor = possibleColors.get(0);
            });
            vbox.getChildren().add(b);
        }
        VBox textFieldBox = new VBox();

        Text nameText = new Text();
        nameText.setText("Enter Player Names");
        nameText.setFill(Color.AZURE);

        TextField playerOneText = new TextField("Player One");
        playerOneText.setMaxWidth(100);

        textFieldBox.getChildren().addAll(nameText, playerOneText);
        textFieldBox.setAlignment(Pos.CENTER);

        TextField playerTwoText = new TextField();
        playerTwoText.setMaxWidth(100);
        if (!isOnePlayer) {
            playerTwoText.setText("Player Two");
            textFieldBox.getChildren().add(playerTwoText);
        }

        Button next = new Button("Next");
        next.getStyleClass().add(res.getString("SettingsButtons"));
        next.setOnAction(e -> {
            playerOneName = playerOneText.getText();
            playerTwoName = playerTwoText.getText();
            setUpLoadGamePopUp(settingsStage);
        });

        vbox.getChildren().addAll(textFieldBox, next);
        //root.getChildren().add(vbox);
        vbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        root.setCenter(vbox);
    }

    private void setUpLoadGamePopUp(Stage settingsStage) {
        BorderPane root = createNewPopUpScene(settingsStage);
        VBox vbox = new VBox();
        ButtonGroup loadGameGroup = new ButtonGroup(List.of("Default Game", "Custom Game"), 20, 40, res.getString("SettingsButtons"));
        for (Button b: loadGameGroup.getButtons()) {
            b.setOnAction((newEvent) -> {
                assignXMLFile(b.getText());
                b.setDisable(true);
            });
            vbox.getChildren().add(b);
        }

        Button goButton = new Button("Go!");
        goButton.getStyleClass().add(res.getString("SettingsButtons"));
        goButton.setOnAction(e -> {
            settingsStage.close();
            goAction.handle(e);
        });

        vbox.getChildren().add(goButton);
        //root.getChildren().add(vbox);
        vbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        root.setCenter(vbox);
    }

//    private void setUpPopUp(Stage settingsStage, StackPane settingsRoot, EventHandler<ActionEvent> event) {
//        GridPane pane = setUpButtonPane();
//
//        Label prompt = new Label(POPUP_PROMPT);
//        prompt.getStyleClass().add("prompt");
//
//        HBox promptBox = new HBox();
//        promptBox.getChildren().add(prompt);
//        promptBox.setLayoutX(PROMPT_X);
//        promptBox.setLayoutY(PROMPT_Y);
//        settingsRoot.getChildren().addAll(promptBox, pane);
//
//        Button goButton = new Button("GO");
//        goButton.getStyleClass().add(res.getString("SettingsButtons"));
//        pane.add(goButton, 1,3);
//        pane.getStyleClass().add("gpane");
//
//        goButton.setOnAction(e -> {
//            settingsStage.close();
//            event.handle(e);
//        });
//    }

//    private GridPane setUpButtonPane() {
//        GridPane pane = new GridPane();
//        List buttonLabels = List.of("White", "Black", DEFAULT_XML, CUSTOM_XML, AI_OPPONENT, HUMAN_OPPONENT);
//        ButtonGroup buttonGroup = new ButtonGroup(buttonLabels, BUTTON_WIDTH, BUTTON_HEIGHT,
//            res.getString("SettingsButtons"));
//
//        int[] positionIndices = {0, 0, 2, 0, 0, 1, 2, 1, 0, 2, 2, 2};
//        int i = 0;
//        for (Button button: buttonGroup.getButtons()) {
//            if (i==0 || i==2) {
//                button.setOnAction((newEvent) -> {
//                    assignColorChoice(button.getText());
//                    button.setDisable(true);
//                });
//            } else if (i==4 | i==6) {
//                button.setOnAction((newEvent) -> {
//                    assignXMLFile(button.getText());
//                    button.setDisable(true);
//                });
//            } else {
//                button.setOnAction((newEvent) -> {
//                    assignAIOpponent(button.getText());
//                    button.setDisable(true);
//                });
//            }
//            pane.add(button, positionIndices[i++], positionIndices[i++]);
//            GridPane.setHalignment(button, HPos.CENTER);
//            GridPane.setValignment(button, VPos.CENTER);
//        }
//        return pane;
//    }

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
        playerOneColor = choice;
        System.out.println(playerOneColor); // ***
    }

    private void assignXMLFile(String choice) {
        if (choice.equals(DEFAULT_XML)) {
            this.fileChoice = String.format("resources/%s/default%s.xml", gameChoice, playerOneColor);
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
            this.isOnePlayer = false;
        } else {
            this.isOnePlayer = true;
        }
    }

    public String getGameChoice() {
        return gameChoice;
    }

    public String getFileChoice(){
        return fileChoice;
    }

    public boolean getIsGameOnePlayer() { return isOnePlayer; }

    public String getPlayerOneColor(){ return playerOneColor;}

    public String getPlayerTwoColor(){ return playerTwoColor;}

    public String getPlayerOneName() { return playerOneName; }

    public String getPlayerTwoName() {
        return playerTwoName;
    }
}
