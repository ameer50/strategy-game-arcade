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

    public static final String DEFAULT_XML = "Default Game";
    public static final int IMAGE_SIZE = 220;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private final FileChooser fileChooser = new FileChooser();
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1000;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private ButtonGroup buttons;
    private String gameChoice;
    private String playerOneColor;
    private String playerTwoColor;
    private EventHandler<ActionEvent> goAction;
    private String playerOneName;
    private String playerTwoName;
    private String fileChoice;
    private boolean isOnePlayer;
    private Popup myPopupScreen;
    private VBox menuVBox;
    private String menuStyle;
    private String popupStyle;
    private Button darkButton;
    private boolean isDarkMode;


    public MenuScreen(Stage stage){
        this.stage = stage;
        this.menuVBox = new VBox();
        this.isDarkMode = false;
        menuStyle = res.getString("MenuStyleSheet");
        popupStyle = res.getString("PopupStyleSheet");
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
        scene.getStylesheets().add(menuStyle);

        arrangeLogo();
        arrangeMenuImages();
        arrangeButtons();

        menuVBox.setAlignment(Pos.CENTER);
        root.setCenter(menuVBox);

        arrangeDarkModeButton();


    }

    private void arrangeDarkModeButton() {
        VBox darkButtonBox = new VBox();
        darkButton = new Button("Dark Mode");
        darkButton.getStyleClass().add("darkmode");
        darkButtonBox.getChildren().add(darkButton);
        darkButtonBox.setAlignment(Pos.TOP_RIGHT);
        root.setTop(darkButtonBox);
        listenDarkModeButton();
    }

    public void setButtonListener(EventHandler<ActionEvent> e) {
        for (Button b: buttons.getButtons()) {
            b.setOnAction(event -> {
                gameChoice = b.getText();
                this.goAction = e;

                myPopupScreen = new Popup(500, 600, popupStyle);
                setUpPlayerPopUp();

            });
        }
    }


    private void setUpPlayerPopUp() {
        myPopupScreen.getNewPopup();
        ButtonGroup playerOption = new ButtonGroup(List.of("One Player", "Two Player"));
        playerOption.addStyle(res.getString("SettingsButtons"));
        myPopupScreen.addButtonGroup(playerOption);

        playerOption.getButtons().get(0).setOnAction(e -> {
            isOnePlayer = true;
            setUpColorPopUp();
        });
        playerOption.getButtons().get(1).setOnAction(e -> {
            isOnePlayer = false;
            setUpColorPopUp();
        });
    }

    private void setUpColorPopUp() {

        myPopupScreen.getNewPopup();
        ButtonGroup colorOption = new ButtonGroup(List.of("White", "Black"));

        colorOption.addStyle(res.getString("SettingsButtons"));
        myPopupScreen.addButtonGroup(colorOption);
        VBox textFieldBox = myPopupScreen.getButtonBox();

        Text enterColorText = new Text();
        enterColorText.setText("Player One Color: ");
        enterColorText.getStyleClass().add("playername");

        if (!isOnePlayer) textFieldBox.getChildren().add(0, enterColorText);

        for (Button b: colorOption.getButtons()) {
            b.setOnAction(e -> {
                b.setDisable(true);
                Button otherButton = colorOption.getButtons().get(colorOption.getButtons().indexOf(b) ^ 1);
                otherButton.setDisable(false);
                playerOneColor = b.getText();
                playerTwoColor = otherButton.getText();

            });
        }

        Text nameText = new Text("Enter Player Name(s)");
        nameText.getStyleClass().add("playername");

        TextField playerOneText = new TextField();
        playerOneText.setPromptText("Player One");
        playerOneText.setMaxWidth(200);
        playerOneText.getStyleClass().add("file-text-field");

        textFieldBox.getChildren().addAll(nameText, playerOneText);
        textFieldBox.setAlignment(Pos.CENTER);
        textFieldBox.getStyleClass().add("vbox");

        TextField playerTwoText = new TextField();
        playerTwoText.setPromptText("Player Two");
        playerTwoText.getStyleClass().add("file-text-field");
        playerTwoText.setMaxWidth(200);

        if (!isOnePlayer) textFieldBox.getChildren().add(playerTwoText);

        Button next = new Button("Next");
        next.getStyleClass().add(res.getString("SettingsButtons"));
        next.setOnAction(e -> {
            playerOneName = playerOneText.getText();
            playerTwoName = playerTwoText.getText();
            setUpLoadGamePopUp();
        });
        textFieldBox.getChildren().addAll(next);
    }

    private void setUpLoadGamePopUp() {
        myPopupScreen.getNewPopup();
        ButtonGroup loadGameOption = new ButtonGroup(List.of("Default Game", "Custom Game"));

        loadGameOption.addStyle(res.getString("SettingsButtons"));
        myPopupScreen.addButtonGroup(loadGameOption);
        VBox vBox = myPopupScreen.getButtonBox();

        for (Button b: loadGameOption.getButtons()) {
            b.setOnAction((newEvent) -> {
                b.setDisable(true);
                Button otherButton = loadGameOption.getButtons().get(loadGameOption.getButtons().indexOf(b) ^ 1);
                otherButton.setDisable(false);
                assignXMLFile(b.getText());
            });
        }

        Button goButton = new Button("Go!");
        goButton.getStyleClass().add(res.getString("GoButton"));
        goButton.setOnAction(e -> {
            myPopupScreen.getStage().close();
            goAction.handle(e);
        });

        vBox.getStyleClass().add("vbox");
        vBox.getChildren().add(goButton);
    }

    private void arrangeLogo() {
        Text logo = new Text();
        logo.setId("banana");
        logo.setText("STRATEGY GAME ARCADE");
        logo.getStyleClass().add("logo");
        logo.setFill(Color.AZURE);
        HBox logoBox = new HBox();
        logoBox.getChildren().add(logo);
        logoBox.getStyleClass().add("logobox");
        menuVBox.getChildren().add(logoBox);
    }

    private void arrangeMenuImages() {
        HBox hBox = new HBox();

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

            hBox.getChildren().add(stack);
            colIndex += 2;
        }
        hBox.getStyleClass().add("hbox");
        menuVBox.getChildren().add(hBox);
    }

    private void arrangeButtons(){

        buttons = new ButtonGroup(List.of("Chess", "Checkers", "Othello"));

        VBox vbox = new VBox();
        for (Button b : buttons.getButtons()) {
            b.getStyleClass().add("buttons");
            vbox.getChildren().add(b);

        }
        //root.getChildren().add(vbox);
        //vbox.setLayoutX(STAGE_WIDTH/2 - vbox.getWidth()/2);
        //vbox.setLayoutY(VBOX_Y);
        vbox.getStyleClass().add("vbox");
        menuVBox.getChildren().add(vbox);
        //root.setCenter(vbox);
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

    public String getGameChoice() {
        return gameChoice;
    }

    public String getFileChoice() {
        return fileChoice;
    }

    public boolean getIsGameOnePlayer() { return isOnePlayer; }

    public String getPlayerOneColor(){ return playerOneColor;}

    public String getPlayerTwoColor(){ return playerTwoColor;}

    public String getPlayerOneName() { return playerOneName; }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void toggleMenuDarkMode(){
        isDarkMode = !isDarkMode;
        scene.getStylesheets().remove(menuStyle);
        menuStyle = isDarkMode ? res.getString("MenuDarkSheet") : res.getString("MenuStyleSheet");
        popupStyle = isDarkMode ? res.getString("PopupDarkSheet") : res.getString("PopupStyleSheet");
        scene.getStylesheets().add(menuStyle);

    }

    public boolean isDarkMode(){
        return isDarkMode;
    }

    private void listenDarkModeButton(){
        darkButton.setOnAction(event -> {
            toggleMenuDarkMode();
        });
    }

}
