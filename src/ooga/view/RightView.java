package ooga.view;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ooga.strategy.HumanPlayer;
import ooga.strategy.Player;

public class RightView {


    private VBox display;
    private DoubleProperty whiteScore;
    private DoubleProperty blackScore;
    private HBox scores;



    public RightView(){

        display = new VBox();

        createDisplay();
        createScoreBoxes();

        display.getChildren().addAll(scores);


    }

    private void createDisplay(){
        display.setLayoutX(750);
        display.setLayoutY(35);
        display.setMinHeight(650);
        display.setMinWidth(200);
        display.getStyleClass().add("display");

    }

    private void createScoreBoxes(){
        whiteScore = new SimpleDoubleProperty();
        blackScore  = new SimpleDoubleProperty();
        Text white = new Text("White: ");
        Text black = new Text("Black: ");
        Text whiteScoreText = new Text(whiteScore.toString());
        whiteScoreText.textProperty().bind(whiteScore.asString());
        Text blackScoreText = new Text(blackScore.toString());
        blackScoreText.textProperty().bind(blackScore.asString());
        scores = new HBox();
        scores.getChildren().addAll(white, black, whiteScoreText, blackScoreText);

    }


    public VBox getDisplay(){
        return display;
    }



    public void bindScores(Player white, Player black){
        whiteScore.bind(white.getScore());
        blackScore.bind(black.getScore());
    }
}
