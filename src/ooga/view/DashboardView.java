package ooga.view;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ooga.history.Move;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardView {

    public static final int DISPLAY_LAYOUT_X = 900;
    public static final int DISPLAY_LAYOUT_Y = 400;
    public static final String POPUP_STYLE_SHEET = "PopupStyleSheet";
    public static final String DISPLAY_STYLE = "display";
    public static final String SCORES_STYLE = "scoreshbox";
    public static final int HISTORY_MIN_HEIGHT = 400;
    public static final int HISTORY_MIN_WIDTH = 300;
    public static final String HISTORY_STYLE = "listview";
    public static final String HISTORY_BOX_STYLE = "hboxlist";
    public static final String UNDO = "Undo";
    public static final String REDO = "Redo";
    public static final String SAVE_GAME = "Save Game";
    public static final String RETURN_TO_MENU = "Return to Menu";
    public static final String NEW_WINDOW = "New Window";
    public static final String AUXILIARY_BUTTON_STYLE = "AuxiliaryButton";
    public static final String GPANE_STYLE = "gpane";
    public static final String TURN = "Turn: ";
    public static final String ACTIVE_PLAYER_BOX_STYLE = "scoreshbox";
    public static final String FILE_ENTER_TITLE = "FileEnterTitle";
    public static final int FILE_STAGE_HEIGHT = 500;
    public static final int FILE_STAGE_WIDTH = 500;
    public static final String ENTER_XML_FILENAME = "Enter XML Filename:";
    public static final String SAVE_FILE_STYLE = "savefile";
    public static final int FILE_SAVE_TEXT_FIELD_MAX_WIDTH = 200;
    public static final String FILE_TEXT_FIELD_STYLE = "file-text-field";
    public static final String GO = "Go!";
    public static final String SETTINGS_BUTTONS = "SettingsButtons";
    public static final String WINNER = "Winner!";
    public static final int WINNER_POP_UP_HEIGHT = 500;
    public static final int WINNER_POP_UP_WIDTH = 500;
    public static final String WINNER_TEXT = "The winner is: ";
    public static final String PREFER = "prefer";
    public static final String QUIT = "Quit";
    public static final String VBOX = "vbox";
    public static final String SAVED_XML_PATH = "savedXML/%s.xml";
    public static final String POPUP_DARK_SHEET = "PopupDarkSheet";
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private VBox displayBox;
    private Text playerOneName;
    private Text playerTwoName;
    private IntegerProperty playerOneScore;
    private IntegerProperty playerTwoScore;
    private Text playerOneScoreText;
    private Text playerTwoScoreText;
    private HBox scoresBox;
    private ButtonGroup auxiliaryButtons;
    private GridPane auxiliaryButtonPane;
    private HBox activePlayerBox;
    private Text activePlayerText;
    private ListView<Move> history;
    private HBox historyBox;
    private EventHandler<ActionEvent> undoMoveFunction;
    private EventHandler<ActionEvent> redoMoveFunction;
    private EventHandler<ActionEvent> quitFunction;
    private EventHandler<ActionEvent> saveFunction;
    private EventHandler<ActionEvent> newWindowFunction;
    private String newFileName;
    private String winner;
    private Button undoButton;
    private Button redoButton;
    private String popupStyle;
    private Popup saveGamePopupScreen;

    public DashboardView() {
        displayBox = new VBox();
        popupStyle = res.getString(POPUP_STYLE_SHEET);
        displayBox.setLayoutX(DISPLAY_LAYOUT_X);
        displayBox.setLayoutY(DISPLAY_LAYOUT_Y);

        createScoreBoxes();
        createHistoryBox();
        createActivePlayerBox();
        createAuxiliaryButtonPane();

        displayBox.getChildren().addAll(scoresBox, historyBox, activePlayerBox, auxiliaryButtonPane);
        displayBox.getStyleClass().add(DISPLAY_STYLE);
    }

    private void createScoreBoxes() {
        HBox playerOneScoreBox = new HBox();
        HBox playerTwoScoreBox = new HBox();
        playerOneName = new Text();
        playerTwoName = new Text();

        playerOneScore = new SimpleIntegerProperty();
        playerTwoScore = new SimpleIntegerProperty();
        playerOneScoreText = new Text(playerOneScore.toString());
        playerOneScoreText.textProperty().bind(playerOneScore.asString());
        playerTwoScoreText = new Text(playerTwoScore.toString());
        playerTwoScoreText.textProperty().bind(playerTwoScore.asString());

        playerOneScoreBox.getChildren().addAll(playerOneName, playerOneScoreText);
        playerTwoScoreBox.getChildren().addAll(playerTwoName, playerTwoScoreText);

        scoresBox = new HBox();
        applyStyle(playerOneScoreBox, SCORES_STYLE);
        applyStyle(playerTwoScoreBox, SCORES_STYLE);
        scoresBox.getChildren().addAll(playerOneScoreBox, playerTwoScoreBox);
        applyStyle(scoresBox, SCORES_STYLE);
    }

    private void createHistoryBox() {
        history = new ListView<>();
        history.getStyleClass().add(HISTORY_STYLE);
        history.setMinHeight(HISTORY_MIN_HEIGHT);
        history.setMinWidth(HISTORY_MIN_WIDTH);
        historyBox = new HBox();
        historyBox.getChildren().add(history);
        historyBox.getStyleClass().add(HISTORY_BOX_STYLE);
    }

    public Text getPlayerOneScoreText() {
        return playerOneScoreText;
    }

    public Text getPlayerTwoScoreText() {
        return playerTwoScoreText;
    }

    public void bindScores(IntegerProperty playerOneScore, IntegerProperty playerTwoScore){
        this.playerOneScore.bind(playerOneScore);
        this.playerTwoScore.bind(playerTwoScore);
    }

    private void createAuxiliaryButtonPane() {
        auxiliaryButtonPane = new GridPane();
        auxiliaryButtons = new ButtonGroup(List.of(UNDO, REDO, SAVE_GAME, RETURN_TO_MENU, NEW_WINDOW));
        auxiliaryButtons.addStyle(res.getString(AUXILIARY_BUTTON_STYLE));
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(auxiliaryButtons.getButtons());

        int[] position = {0, 0, 1, 0, 0, 1, 1, 1, 0, 2};
        int i = 0;
        for(Button b: auxiliaryButtons.getButtons()){
            addGPaneElement(b, position[i++], position[i++]);
        }
        auxiliaryButtonPane.getStyleClass().add(GPANE_STYLE);
        auxiliaryButtonPane.setAlignment(Pos.CENTER);

        setUpUndoRedoButtons();
        setUpSaveGameButton();
        setUpQuitGameButton();
        setUpNewWindowButton();
    }

    private void setUpUndoRedoButtons() {
        undoButton = auxiliaryButtons.getButtons().get(0);
        undoButton.setDisable(true);
        redoButton = auxiliaryButtons.getButtons().get(1);
        redoButton.setDisable(true);

        undoButton.setOnAction(e -> {
            undoMoveFunction.handle(e);
        });

        redoButton.setOnAction(e -> {
            redoMoveFunction.handle(e);
        });
    }

    private void setUpSaveGameButton() {
        Button saveGame = auxiliaryButtons.getButtons().get(2);
        saveGame.setOnAction(e -> {
            setUpSaveFileStage(saveFunction);
        });
    }

    private void setUpQuitGameButton() {
        Button quitGame = auxiliaryButtons.getButtons().get(3);
        quitGame.setOnAction(e -> {
            quitFunction.handle(e);
        });
    }

    private void setUpNewWindowButton() {
        Button newWindow = auxiliaryButtons.getButtons().get(4);
        newWindow.setOnAction(e -> {
            newWindowFunction.handle(e);
        });
    }

    private void createActivePlayerBox() {
        activePlayerBox = new HBox();
        Text turnText = new Text(TURN);
        activePlayerText = new Text();
        activePlayerBox.getChildren().addAll(turnText, activePlayerText);
        activePlayerBox.getStyleClass().add(ACTIVE_PLAYER_BOX_STYLE);
    }

    public void setPlayerNames(String playerOneName, String playerTwoName) {
        this.playerOneName.setText(String.format("%s: ", playerOneName));
        this.playerTwoName.setText(String.format("%s: ", playerTwoName));
    }

    public void setActivePlayerText(String activePlayerName, String activePlayerColor) {
        this.activePlayerText.setText(String.format("%s (%s)", activePlayerName, activePlayerColor));
    }

    public VBox getDisplayBox(){
        return displayBox;
    }

    private void addGPaneElement(Node b, int col, int row){
        auxiliaryButtonPane.add(b, col, row);
        GridPane.setHalignment(b, HPos.LEFT);
        GridPane.setValignment(b, VPos.CENTER);
    }

    private void applyStyle(Pane p, String style){
        for(Node a: p.getChildren()){
            a.getStyleClass().add(style);
        }
    }

    public void setUpSaveFileStage(EventHandler<ActionEvent> e) {
        Stage fileNameStage = new Stage();
        fileNameStage.setTitle(res.getString(FILE_ENTER_TITLE));
        fileNameStage.setHeight(FILE_STAGE_HEIGHT);
        fileNameStage.setWidth(FILE_STAGE_WIDTH);
        BorderPane fileRoot = new BorderPane();
        Scene settingsScene = new Scene(fileRoot);
        settingsScene.getStylesheets().add(popupStyle);
        fileNameStage.setScene(settingsScene);
        fileNameStage.show();
        createSaveFilePopUp(fileNameStage, fileRoot, e);
    }

    private void createSaveFilePopUp(Stage settingsStage, BorderPane fileRoot, EventHandler<ActionEvent> event) {
        Text prefer = new Text();
        prefer.setText(ENTER_XML_FILENAME);
        prefer.getStyleClass().add(SAVE_FILE_STYLE);

        TextField textField = new TextField();
        textField.setMaxWidth(FILE_SAVE_TEXT_FIELD_MAX_WIDTH);
        textField.getStyleClass().add(FILE_TEXT_FIELD_STYLE);
        VBox textFieldBox = new VBox();

        Button goButton = new Button(GO);
        goButton.getStyleClass().add(res.getString(SETTINGS_BUTTONS));

        textFieldBox.getChildren().addAll(prefer, textField, goButton);
        textFieldBox.setAlignment(Pos.CENTER);
        //fileRoot.setCenter(textFieldBox);

        goButton.setOnAction(e -> {
            setNewFileName(textField.getText());
            saveGamePopupScreen.closePopup();
            event.handle(e);
        });

    }

    public void setUpWinnerStage() {
        Stage winnerPopUpStage = new Stage();
        winnerPopUpStage.setTitle(WINNER);
        winnerPopUpStage.setHeight(WINNER_POP_UP_HEIGHT);
        winnerPopUpStage.setWidth(WINNER_POP_UP_WIDTH);
        BorderPane pane = new BorderPane();
        Scene settingsScene = new Scene(pane);
        settingsScene.getStylesheets().add(popupStyle);
        winnerPopUpStage.setScene(settingsScene);
        winnerPopUpStage.show();
        createWinnerPopUp(winnerPopUpStage, pane);
    }

    private void createWinnerPopUp(Stage settingsStage, BorderPane fileRoot) {
        Text prefer = new Text();
        prefer.setText(WINNER_TEXT + winner);
        prefer.getStyleClass().add(PREFER);

        VBox textFieldBox = new VBox();

        Button quitButton = new Button(QUIT);
        quitButton.getStyleClass().add(res.getString(SETTINGS_BUTTONS));
        textFieldBox.getChildren().addAll(prefer, quitButton);

        textFieldBox.getStyleClass().add(VBOX);
        fileRoot.setCenter(textFieldBox);

        quitButton.setOnAction(e -> {
            settingsStage.close();
            quitFunction.handle(e);
        });
    }

    public ListView<Move> getHistoryDisplay() {
        return history;
    }

    public void setUndoMoveClicked(EventHandler<ActionEvent> move) {
        undoMoveFunction = move;
    }

    public void setRedoMoveClicked(EventHandler<ActionEvent> move) {
        redoMoveFunction = move;
    }

    public void setUndoRedoButtonsDisabled(boolean undoDisabled, boolean redoDisabled) {
        undoButton.setDisable(undoDisabled);
        redoButton.setDisable(redoDisabled);
    }

    public void setQuitClicked(EventHandler<ActionEvent> quit) {
        quitFunction = quit;
    }

    public void setNewWindowClicked(EventHandler<ActionEvent> newWindow) {
        newWindowFunction = newWindow;
    }


    public void setSaveClicked(EventHandler<ActionEvent> save) {
        saveFunction = save;
    }

    public String getNewFileName(){
        return newFileName;
    }

    private void setNewFileName(String str){
        newFileName = String.format(SAVED_XML_PATH, str);
    }
    
    public void setWinner(String winner){
        this.winner = winner;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }

    public void toggleDarkMode(){
        popupStyle = (popupStyle.equals(res.getString(POPUP_STYLE_SHEET))) ? res.getString(POPUP_DARK_SHEET) : res.getString(POPUP_STYLE_SHEET);
    }

    public void addIcons(List<CellView> icons) {
        HBox iconBox = new HBox();
        iconBox.getChildren().addAll(icons);
        iconBox.setAlignment(Pos.CENTER);
        displayBox.getChildren().add(0, iconBox);
    }
}
