package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class GameScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private Map<String, String> nameDim;
    private Map<Point2D, String> pieceLocations;
    private BoardView board;


    public GameScreen(Stage stage, Map<String, String> nameDim, Map<Point2D, String> pieceLocations){
        this.stage = stage;
        this.nameDim = nameDim;
        this.pieceLocations = pieceLocations;
        startView();
        stage.show();
    }

    private void startView(){
        root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));

        Pane canvas = new Pane();
        board = new BoardView(Integer.parseInt(nameDim.get("width")), Integer.parseInt(nameDim.get("height")),
                "Black", pieceLocations, root);
        canvas.getChildren().addAll(board.getCells());
        root.getChildren().addAll(canvas);

        root.getChildren().addAll(board.getPieces());
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public BoardView getBoard() {
        return board;
    }
}
