package ooga.view;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ooga.strategy.HumanPlayer;
import ooga.strategy.Player;

import java.util.List;

public class RightView {


    private VBox display;
    private DoubleProperty whiteScore;
    private DoubleProperty blackScore;
    private VBox scores;
    private HBox auxiliaryButtons;
    private HBox bottom;
    private Text activePlayerText;

    public RightView(){
        display = new VBox();

        createDisplay();
        createScoreBoxes();
        createAuxiliaryButtons();
        createBottom();

        display.getChildren().addAll(scores, auxiliaryButtons, bottom);
    }

    private void createDisplay(){
        display.setLayoutX(800);
        display.setLayoutY(100);
        display.setMinHeight(650);
        display.setMinWidth(200);
        display.getStyleClass().add("display");

    }

    private void createScoreBoxes() {
        HBox whiteScoreBox = new HBox();
        HBox blackScoreBox = new HBox();
        Text whiteText = new Text("White: ");
        Text blackText = new Text("Black: ");

        whiteScore = new SimpleDoubleProperty();
        blackScore  = new SimpleDoubleProperty();
        Text whiteScoreText = new Text(whiteScore.toString());
        whiteScoreText.textProperty().bind(whiteScore.asString());
        Text blackScoreText = new Text(blackScore.toString());
        blackScoreText.textProperty().bind(blackScore.asString());

        whiteScoreBox.getChildren().addAll(whiteText, whiteScoreText);
        blackScoreBox.getChildren().addAll(blackText, blackScoreText);

        scores = new VBox();
        scores.getChildren().addAll(whiteScoreBox, blackScoreBox);
    }

    public void bindScores(Player white, Player black){
        whiteScore.bind(white.getScore());
        blackScore.bind(black.getScore());
    }

    private void createAuxiliaryButtons() {
        auxiliaryButtons = new HBox();
        ButtonGroup buttons = new ButtonGroup(List.of("Save Game", "Load Game", "Undo Move", "Redo Move"), 20, 50);
        auxiliaryButtons.getChildren().addAll(buttons.getButtons());
    }

    private void createBottom() {
        bottom = new HBox();
        Text turnText = new Text("Turn: ");
        activePlayerText = new Text();
        Button quitButton = new Button("Quit");
        bottom.getChildren().addAll(turnText, activePlayerText, quitButton);
    }

    public void setActivePlayerText(Player activePlayer) {
        this.activePlayerText.setText(activePlayer.getName());
    }

    public VBox getDisplay(){
        return display;
    }
}
