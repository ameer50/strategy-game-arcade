package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import ooga.board.Board;

public class GameScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private int width;
    private int height;
    private Map<Point2D, String> pieceLocations;
    private BoardView boardView;
    private Board board;


    public GameScreen(Stage stage, Board board, Map<Point2D, String> locations) {
        // Map<String, String> nameDim, Map<Point2D, String> pieceLocations
        this.board = board;
        this.stage = stage;
        this.height = board.getHeight();
        this.width = board.getWidth();
        this.pieceLocations = locations;
        initializeView();
        stage.show();
    }

    private void initializeView(){
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        root = new BorderPane();
        scene = new Scene(root);
        scene.getStylesheets().add(res.getString("GameStyleSheet"));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));

        Pane boardArea = new Pane();
        boardView = new BoardView(width, height, "Black", pieceLocations, root);
        // TODO: 'root' shouldn't have to be passed
        boardArea.getChildren().addAll(boardView.getCells());
        root.getChildren().add(boardArea);
        root.getChildren().addAll(boardView.getPieces());
    }

    public BoardView getBoardView() {
        return boardView;
    }
}
