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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ooga.history.Move;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardView {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private VBox display;
    private Text playerOneName;
    private Text playerTwoName;
    private IntegerProperty playerOneScore;
    private IntegerProperty playerTwoScore;
    private HBox scores;
    private GridPane auxiliaryButtons;
    private HBox bottom;
    private Text activePlayerText;
    boolean undoState;
    private ListView<Move> history;
    private EventHandler<ActionEvent> undoMoveFunction;
    private EventHandler<ActionEvent> redoMoveFunction;
    private EventHandler<ActionEvent> quitFunction;
    private EventHandler<ActionEvent> saveFunction;
    private String newFileName;
    private String winner;

    public DashboardView(){
        display = new VBox();
        undoState = false;

        createDisplay();
        createScoreBoxes();
        createAuxiliaryButtons();
        createBottom();

        history = new ListView<>();

        history.getStyleClass().add("listview");
        history.setMinHeight(400);
        history.setMinWidth(300);
        HBox hbox = new HBox();
        hbox.getChildren().add(history);
        hbox.getStyleClass().add("hboxlist");

        display.getChildren().addAll(scores, hbox, bottom, auxiliaryButtons);
        display.getStyleClass().add("display");

    }

    private void createDisplay(){
        display.setLayoutX(900);
        display.setLayoutY(400);
    }

    private void createScoreBoxes() {
        HBox playerOneScoreBox = new HBox();
        HBox playerTwoScoreBox = new HBox();
        playerOneName = new Text();
        playerTwoName = new Text();

        playerOneScore = new SimpleIntegerProperty();
        playerTwoScore = new SimpleIntegerProperty();
        Text playerOneScoreText = new Text(playerOneScore.toString());
        playerOneScoreText.textProperty().bind(playerOneScore.asString());
        Text playerTwoScoreText = new Text(playerTwoScore.toString());
        playerTwoScoreText.textProperty().bind(playerTwoScore.asString());

        playerOneScoreBox.getChildren().addAll(playerOneName, playerOneScoreText);
        playerTwoScoreBox.getChildren().addAll(playerTwoName, playerTwoScoreText);

        scores = new HBox();
        scores.getChildren().addAll(playerOneScoreBox, playerTwoScoreBox);
        applyStyle(scores, "scoreshbox");
    }

    public void bindScores(IntegerProperty playerOneScore, IntegerProperty playerTwoScore){
        this.playerOneScore.bind(playerOneScore);
        this.playerTwoScore.bind(playerTwoScore);
    }

    private void createAuxiliaryButtons() {
        auxiliaryButtons = new GridPane();
        ButtonGroup buttons = new ButtonGroup(List.of("Undo", "Redo", "Save Game", "Quit"));
        buttons.addStyle(res.getString("AuxiliaryButton"));
        // 115 35
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(buttons.getButtons());

        int[] position = {0, 0, 1, 0, 0, 1, 1, 1};
        int i = 0;
        for(Button b: buttons.getButtons()){
            addGPaneElement(b, position[i++], position[i++]);
        }
        auxiliaryButtons.getStyleClass().add("gpane");
        auxiliaryButtons.setLayoutX(750);
        auxiliaryButtons.setLayoutY(750);

        buttons.getButtons().get(0).setOnAction(e -> {
            undoMoveFunction.handle(e);
        });
        buttons.getButtons().get(1).setOnAction(e -> {
            redoMoveFunction.handle(e);
        });

        buttons.getButtons().get(2).setOnAction(e -> {
            textFieldPopUp(saveFunction);

        });

        buttons.getButtons().get(3).setOnAction(e -> {
            quitFunction.handle(e);
        });
    }

    private void createBottom() {
        bottom = new HBox();
        Text turnText = new Text("Turn: ");
        activePlayerText = new Text();
        bottom.getChildren().addAll(turnText, activePlayerText);
        bottom.getStyleClass().add("scoreshbox");
    }

    public void setPlayerNames(String playerOneName, String playerTwoName) {
        this.playerOneName.setText(String.format("%s: ", playerOneName));
        this.playerTwoName.setText(String.format("%s: ", playerTwoName));
    }

    public void setActivePlayerText(String activePlayerName, String activePlayerColor) {
        this.activePlayerText.setText(String.format("%s (%s)", activePlayerName, activePlayerColor));
    }

    public VBox getDisplay(){
        return display;
    }

    private void addGPaneElement(Node b, int col, int row){
        auxiliaryButtons.add(b, col, row);
        GridPane.setHalignment(b, HPos.LEFT);
        GridPane.setValignment(b, VPos.CENTER);
    }

    private void applyStyle(Pane p, String style){
        for(Node a: p.getChildren()){
            a.getStyleClass().add(style);
        }
    }

    public void textFieldPopUp(EventHandler<ActionEvent> e) {
        Stage fileNameStage = new Stage();
        fileNameStage.setTitle(res.getString("FileEnterTitle"));
        fileNameStage.setHeight(500);
        fileNameStage.setWidth(500);
        Pane fileRoot = new Pane();
        Scene settingsScene = new Scene(fileRoot);
        settingsScene.getStylesheets().add(res.getString("PopupStyleSheet"));
        fileNameStage.setScene(settingsScene);
        fileNameStage.show();
        setUpTextFieldPopUp(fileNameStage, fileRoot, e);
    }

    private void setUpTextFieldPopUp(Stage settingsStage, Pane fileRoot, EventHandler<ActionEvent> event) {
        Text prefer = new Text();
        prefer.setText("Enter XML Filename:");
        prefer.getStyleClass().add("prefer");

        TextField textField = new TextField();
        textField.getStyleClass().add("file-text-field");
        VBox textFieldBox = new VBox();


        textFieldBox.getChildren().addAll(prefer, textField);
        textFieldBox.setLayoutX(125);
        textFieldBox.setLayoutY(150);

        Button goButton = new Button("Go!");
        goButton.getStyleClass().add(res.getString("SettingsButtons"));
        goButton.setLayoutX(220);
        goButton.setLayoutY(250);

        fileRoot.getChildren().addAll(textFieldBox, goButton);


        goButton.setOnAction(e -> {
            setNewFileName(textField.getText());
            settingsStage.close();
            event.handle(e);
        });
    }

    public void winnerPopUp() {
        Stage fileNameStage = new Stage();
        fileNameStage.setTitle("Winner!");
        fileNameStage.setHeight(500);
        fileNameStage.setWidth(500);
        BorderPane fileRoot = new BorderPane();
        Scene settingsScene = new Scene(fileRoot);
        settingsScene.getStylesheets().add(res.getString("PopupStyleSheet"));
        fileNameStage.setScene(settingsScene);
        fileNameStage.show();
        setUpWinnerPopUp(fileNameStage, fileRoot);
    }

    private void setUpWinnerPopUp(Stage settingsStage, BorderPane fileRoot) {
        Text prefer = new Text();
        prefer.setText("The winner is: " + winner);
        prefer.getStyleClass().add("prefer");

        VBox textFieldBox = new VBox();

        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add(res.getString("SettingsButtons"));
        textFieldBox.getChildren().addAll(prefer, quitButton);
        textFieldBox.getStyleClass().add("vbox");
        fileRoot.setCenter(textFieldBox);

        quitButton.setOnAction(e -> {
            settingsStage.close();
            quitFunction.handle(e);
        });
    }

    public boolean getUndoState(){
        return undoState;
    }

    public ListView<Move> getHistory() {
        return history;
    }

    public void setUndoMoveClicked(EventHandler<ActionEvent> move) {
        undoMoveFunction = move;
    }

    public void setRedoMoveClicked(EventHandler<ActionEvent> move) {
        redoMoveFunction = move;
    }

    public void setQuitClicked(EventHandler<ActionEvent> quit) {
        quitFunction = quit;
    }

    public void setSaveClicked(EventHandler<ActionEvent> save) {
        saveFunction = save;
    }
    public String getNewFileName(){
        return newFileName;
    }

    private void setNewFileName(String str){
        newFileName = "savedXML/" + str + ".xml";
    }
    
    public void setWinner(String winner){
        this.winner = winner;
    }
}
