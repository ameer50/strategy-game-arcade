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

    public static final String DEFAULT_GAME = "Default Game";
    public static final int IMAGE_SIZE = 220;
    public static final String MENU_STYLE_SHEET = "MenuStyleSheet";
    public static final String POPUP_STYLE_SHEET = "PopupStyleSheet";
    public static final String MENU_STAGE_TITLE = "MenuStageTitle";
    public static final String DARK_MODE = "Dark Mode";
    public static final String DARK_MODE_STYLE = "darkmode";
    public static final int POPUP_WIDTH = 500;
    public static final int POPUP_HEIGHT = 600;
    public static final String SETTINGS_BUTTONS = "SettingsButtons";
    public static final String ONE_PLAYER = "One Player";
    public static final String TWO_PLAYER = "Two Player";
    public static final String PLAYER_ONE_COLOR_PROMPT = "Player One Color: ";
    public static final String PLAYERNAME = "playername";
    public static final String ENTER_PLAYER_NAMES = "Enter Player Name(s)";
    public static final String PLAYER_ONE = "Player 1";
    public static final String PLAYER_TWO = "Player 2";
    public static final String FILE_TEXT_FIELD = "file-text-field";
    public static final int PLAYER_TEXT_MAX_WIDTH = 200;
    public static final String VBOX = "vbox";
    public static final String NEXT = "Next";
    public static final String CUSTOM_GAME = "Custom Game";
    public static final String GO = "Go!";
    public static final String SELECT_BUTTON_STYLE = "GoButton";
    public static final String MENU_SCREEN_TITLE = "STRATEGY GAME ARCADE";
    public static final String LOGO_STYLE = "logo";
    public static final String LOGO_BOX_STYLE = "logobox";
    public static final String CHECKERS_ICON = "CheckersIcon";
    public static final String CHESS_ICON = "ChessIcon";
    public static final int BACKGROUND_OFFSET = 20;
    public static final String BACKGROUND_STYLE = "picture-background";
    public static final String HBOX = "hbox";
    public static final String CHESS = "Chess";
    public static final String CHECKERS = "Checkers";
    public static final String CONNECT_FOUR = "ConnectFour";
    public static final String OTHELLO = "Othello";
    public static final String CUSTOM = "Custom";
    public static final String BUTTONS_STYLE = "buttons";
    public static final String ASSIGNED_FILE_PATH = "resources/%s/default%s.json";
    public static final String MENU_DARK_SHEET = "MenuDarkSheet";
    public static final String POPUP_DARK_SHEET = "PopupDarkSheet";
    public static final String COLOR_1 = "Color1";
    public static final String COLOR_2 = "Color2";
    public static final String CPU_DIFFICULTY = "CPU Difficulty: ";
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
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
    private String strategyType;

    public MenuScreen(Stage stage){
        this.stage = stage;
        this.menuVBox = new VBox();
        this.isDarkMode = false;
        menuStyle = res.getString(MENU_STYLE_SHEET);
        popupStyle = res.getString(POPUP_STYLE_SHEET);
        startView();
        stage.show();
    }

    private void startView() {
        root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(res.getString(MENU_STAGE_TITLE));
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
        darkButton = new Button(DARK_MODE);
        darkButton.getStyleClass().add(DARK_MODE_STYLE);
        darkButtonBox.getChildren().add(darkButton);
        darkButtonBox.setAlignment(Pos.TOP_RIGHT);
        root.setTop(darkButtonBox);
        setDarkModeListener();
    }

    private void setUpPlayerPopUp() {
        myPopupScreen.getNewPopup();
        myPopupScreen.getStage().show();
        ButtonGroup playerOption = new ButtonGroup(List.of(ONE_PLAYER, TWO_PLAYER));
        playerOption.addStyle(res.getString(SETTINGS_BUTTONS));
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
        myPopupScreen.getStage().show();
        ButtonGroup colorOption = new ButtonGroup(List.of(res.getString(gameChoice + COLOR_1), res.getString(gameChoice + COLOR_2)));
        colorOption.addStyle(res.getString(SETTINGS_BUTTONS));
        myPopupScreen.addButtonGroup(colorOption);
        VBox textFieldBox = myPopupScreen.getPopupBox();
        Text enterColorText = new Text(PLAYER_ONE_COLOR_PROMPT);
        enterColorText.getStyleClass().add(PLAYERNAME);
        if (!isOnePlayer) textFieldBox.getChildren().add(0, enterColorText);

        Button next = new Button(NEXT);
        next.getStyleClass().add(res.getString(SETTINGS_BUTTONS));
        next.setDisable(true);
        setUpColorButtonActions(colorOption, next);
        Text nameText = new Text(ENTER_PLAYER_NAMES);
        nameText.getStyleClass().add(PLAYERNAME);
        TextField playerOneText = makeTextField(PLAYER_ONE);
        TextField playerTwoText = makeTextField(PLAYER_TWO);
        textFieldBox.getChildren().addAll(nameText, playerOneText);
        setUpNextAction(next, playerOneText, playerTwoText, textFieldBox);
        textFieldBox.setAlignment(Pos.CENTER);
        textFieldBox.getStyleClass().add(VBOX);
    }

    private void setUpColorButtonActions(ButtonGroup colorOption, Button next){
        for (Button b: colorOption.getButtons()) {
            b.setOnAction(e -> {
                b.setDisable(true);
                Button otherButton = colorOption.getButtons().get(colorOption.getButtons().indexOf(b) ^ 1);
                otherButton.setDisable(false);
                playerOneColor = b.getText();
                playerTwoColor = otherButton.getText();
                next.setDisable(false);
            });
        }
    }

    private void setUpNextAction(Button next, TextField playerOneText, TextField playerTwoText, VBox textFieldBox){
        if (!isOnePlayer) {
            textFieldBox.getChildren().add(playerTwoText);
            next.setOnAction(e -> {
                assignTextIfEmpty(playerOneText, PLAYER_ONE);
                assignTextIfEmpty(playerTwoText, PLAYER_TWO);
                playerOneName = playerOneText.getText();
                playerTwoName = playerTwoText.getText();
                setUpLoadGamePopUp();
            });
        }
        else {
            next.setOnAction(e -> {
                assignTextIfEmpty(playerOneText, PLAYER_ONE);
                playerOneName = playerOneText.getText();
                setUpCPUDifficultyPopUp();
            });
        }
        textFieldBox.getChildren().addAll(next);
    }

    private TextField makeTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(MenuScreen.PLAYER_TEXT_MAX_WIDTH);
        textField.getStyleClass().add(MenuScreen.FILE_TEXT_FIELD);
        return textField;
    }

    private void setUpCPUDifficultyPopUp() {
        myPopupScreen.getNewPopup();
        myPopupScreen.getStage().show();

        Text difficultText = new Text(CPU_DIFFICULTY);
        difficultText.getStyleClass().add(PLAYERNAME);

        ButtonGroup cpuDifficulty = new ButtonGroup(List.of(EASY, MEDIUM, HARD));

        cpuDifficulty.addStyle(res.getString(SETTINGS_BUTTONS));
        myPopupScreen.addButtonGroup(cpuDifficulty);
        VBox vBox = myPopupScreen.getPopupBox();
        vBox.getChildren().add(0, difficultText);

        Button nextButton = new Button(NEXT);
        nextButton.getStyleClass().add(res.getString(SELECT_BUTTON_STYLE));
        nextButton.setDisable(true);

        for (Button b: cpuDifficulty.getButtons()) {
            b.setOnAction((newEvent) -> {
                for(Button button: cpuDifficulty.getButtons()){
                    button.setDisable(false);
                }
                b.setDisable(true);
                nextButton.setDisable(false);
                setStrategyType(res.getString(b.getText()));
            });
        }

        nextButton.setOnAction(e -> {
            setUpLoadGamePopUp();
        });

        vBox.getStyleClass().add(VBOX);
        vBox.getChildren().add(nextButton);
    }

    private void setUpLoadGamePopUp() {
        myPopupScreen.getNewPopup();
        myPopupScreen.getStage().show();
        ButtonGroup loadGameOption = new ButtonGroup(List.of(DEFAULT_GAME, CUSTOM_GAME));

        loadGameOption.addStyle(res.getString(SETTINGS_BUTTONS));
        myPopupScreen.addButtonGroup(loadGameOption);
        VBox vBox = myPopupScreen.getPopupBox();

        Button goButton = new Button(GO);
        goButton.getStyleClass().add(res.getString(SELECT_BUTTON_STYLE));
        goButton.setDisable(true);

        for (Button b: loadGameOption.getButtons()) {
            b.setOnAction((newEvent) -> {
                b.setDisable(true);
                Button otherButton = loadGameOption.getButtons().get(loadGameOption.getButtons().indexOf(b) ^ 1);
                otherButton.setDisable(false);
                assignFile(b.getText());
                goButton.setDisable(false);
            });
        }

        goButton.setOnAction(e -> {
            myPopupScreen.getStage().close();
            goAction.handle(e);
        });

        vBox.getStyleClass().add(VBOX);
        vBox.getChildren().add(goButton);
    }

    private void arrangeLogo() {
        Text logo = new Text();
        logo.setText(MENU_SCREEN_TITLE);
        logo.getStyleClass().add(LOGO_STYLE);
        logo.setFill(Color.AZURE);
        HBox logoBox = new HBox();
        logoBox.getChildren().add(logo);
        logoBox.getStyleClass().add(LOGO_BOX_STYLE);
        menuVBox.getChildren().add(logoBox);
    }

    private void arrangeMenuImages() {
        HBox hBox = new HBox();
        int[] dimensions = {IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE};
        int i = 0;
        for (String s: List.of(CHECKERS_ICON, CHESS_ICON)) {
            StackPane stack = new StackPane();
            int width = dimensions[i++];
            int height = dimensions[i++];
            Rectangle background = new Rectangle(width + BACKGROUND_OFFSET, height + BACKGROUND_OFFSET);
            background.getStyleClass().add(BACKGROUND_STYLE);
            ImageView picture = new ImageView(new Image(res.getString(s), width, height, false, true));

            stack.getChildren().addAll(background, picture);

            hBox.getChildren().add(stack);
        }
        hBox.getStyleClass().add(HBOX);
        menuVBox.getChildren().add(hBox);
    }

    private void arrangeButtons(){
        buttons = new ButtonGroup(List.of(CHESS, CHECKERS, CONNECT_FOUR, OTHELLO, CUSTOM));
        VBox vbox = new VBox();
        for (Button b : buttons.getButtons()) {
            b.getStyleClass().add(BUTTONS_STYLE);
            vbox.getChildren().add(b);
        }
        vbox.getStyleClass().add(VBOX);
        menuVBox.getChildren().add(vbox);
    }

    private void assignFile(String choice) {
        if (choice.equals(DEFAULT_GAME)) {
            this.fileChoice = String.format(ASSIGNED_FILE_PATH, gameChoice, playerOneColor);
        } else {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                fileChoice = file.getAbsolutePath();
            }
        }
    }

    private void setDarkModeListener(){
        darkButton.setOnAction(event -> {
            toggleMenuDarkMode();
        });
    }

    private void setStrategyType(String type){
        strategyType = type;
    }

    private void assignTextIfEmpty(TextField tf, String text){
        if(tf.getText().equals("")){
            tf.setText(text);
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
        menuStyle = isDarkMode ? res.getString(MENU_DARK_SHEET) : res.getString(MENU_STYLE_SHEET);
        popupStyle = isDarkMode ? res.getString(POPUP_DARK_SHEET) : res.getString(POPUP_STYLE_SHEET);
        scene.getStylesheets().add(menuStyle);
    }

    public void setGameButtonListener(EventHandler<ActionEvent> e) {
        for (Button b: buttons.getButtons()) {
            b.setOnAction(event -> {
                gameChoice = b.getText();
                this.goAction = e;
                myPopupScreen = new Popup(POPUP_WIDTH, POPUP_HEIGHT, popupStyle);
                setUpPlayerPopUp();
            });
        }
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }
    public String getStrategyType() {
        return strategyType;
    }
}
