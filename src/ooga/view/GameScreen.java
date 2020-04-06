package ooga.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class GameScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;

    private Stage stage;
    private Scene scene;

    public GameScreen(Stage stage){
        this.stage = stage;
        startView();
        stage.show();

    }

    private void startView(){
        BorderPane root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        Piece rook = new Piece(430, 410, 40, 70, res.getString("BlackRookImage"));
        Piece rook2 = new Piece(430, 410, 40, 70, res.getString("RookImage"));
        BoardView theBoard = new BoardView("ChessBoard");
        root.getChildren().add(theBoard.getBoardView());
        root.getChildren().add(rook.getIVShape());
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));
        System.out.println(scene.getStylesheets());
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }
}
