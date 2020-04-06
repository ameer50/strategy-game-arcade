package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
        PieceView rook = new PieceView(115, 100, 45, 75, res.getString("BlackRookImage"));
        PieceView rook2 = new PieceView(185, 100, 45, 75, res.getString("WhiteRookImage"));
        PieceView rook3 = new PieceView(115, 170, 45, 75, res.getString("WhiteRookImage"));
        BoardView theBoard = new BoardView("ChessBoard");
        root.getChildren().add(theBoard.getBoardView());
        root.getChildren().addAll(rook.getIVShape(), rook2.getIVShape(), rook3.getIVShape());
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
