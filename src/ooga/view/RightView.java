package ooga.view;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ooga.strategy.HumanPlayer;
import ooga.strategy.Player;

import java.util.List;

public class RightView {


    private VBox display;
    private IntegerProperty whiteScore;
    private IntegerProperty blackScore;
    private HBox scores;
    private GridPane auxiliaryButtons;
    private HBox bottom;
    private Text activePlayerText;
    boolean undoState;

    public RightView(){
        display = new VBox();
        undoState = false;

        createDisplay();
        createScoreBoxes();
        createAuxiliaryButtons();
        createBottom();

        display.getChildren().addAll(scores, bottom, auxiliaryButtons);
    }

    private void createDisplay(){
        display.setLayoutX(900);
        display.setLayoutY(600);
        display.setMinHeight(650);
        display.setMinWidth(200);
        display.getStyleClass().add("display");

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
        ButtonGroup buttons = new ButtonGroup(List.of("Undo", "Redo", "Save Game", "Load Game", "Quit"), 115, 35, "auxbuttons");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(buttons.getButtons());

        int[] position = {0, 0, 2, 0, 0, 1, 2, 1, 1, 2};
        int i = 0;
        for(Button b: buttons.getButtons()){
            addGPaneElement(b, position[i++], position[i++]);
            b.setOnAction((newEvent) -> {
                setUndo(true);
                b.setDisable(true);
            });
        }
        auxiliaryButtons.getStyleClass().add("gpane");
        auxiliaryButtons.setLayoutX(750);
        auxiliaryButtons.setLayoutY(750);



    }

    private void createBottom() {
        bottom = new HBox();
        Text turnText = new Text("Turn: ");
        activePlayerText = new Text();
        bottom.getChildren().addAll(turnText, activePlayerText);
        //addGPaneElement(bottom, 0, 2);
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

    private void setUndo(boolean state){
        undoState = state;
    }
    public boolean getUndoState(){
        return undoState;
    }
}
