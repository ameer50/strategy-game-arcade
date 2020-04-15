package ooga.view;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import ooga.history.Move;
import ooga.strategy.Player;

import java.util.List;

public class DashboardView {


    private VBox display;
    private IntegerProperty whiteScore;
    private IntegerProperty blackScore;
    private HBox scores;
    private GridPane auxiliaryButtons;
    private HBox bottom;
    private Text activePlayerText;
    boolean undoState;
    private ListView<Move> history;
    private EventHandler<ActionEvent> undoMoveFunction;
    private EventHandler<ActionEvent> redoMoveFunction;
    private EventHandler<ActionEvent> quitFunction;

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
        HBox whiteScoreBox = new HBox();
        HBox blackScoreBox = new HBox();
        Text whiteText = new Text("White: ");
        Text blackText = new Text("Black: ");

        whiteScore = new SimpleIntegerProperty();
        blackScore  = new SimpleIntegerProperty();
        Text whiteScoreText = new Text(whiteScore.toString());
        whiteScoreText.textProperty().bind(whiteScore.asString());
        Text blackScoreText = new Text(blackScore.toString());
        blackScoreText.textProperty().bind(blackScore.asString());

        whiteScoreBox.getChildren().addAll(whiteText, whiteScoreText);
        blackScoreBox.getChildren().addAll(blackText, blackScoreText);

        scores = new HBox();
        scores.getChildren().addAll(whiteScoreBox, blackScoreBox);
        applyStyle(scores, "scoreshbox");
    }

    public void bindScores(Player white, Player black){
        whiteScore.bind(white.getScore());
        blackScore.bind(black.getScore());
    }

    private void createAuxiliaryButtons() {
        auxiliaryButtons = new GridPane();
        ButtonGroup buttons = new ButtonGroup(List.of("Undo", "Redo", "Save Game", "Quit"), 115, 35, "auxbuttons");
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


    public void setActivePlayerText(Player activePlayer) {
        this.activePlayerText.setText(activePlayer.getName());
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


    private void buttonEvent(Button b, EventHandler<ActionEvent> event){
        b.setOnAction(e -> {
            event.handle(e);
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

}
