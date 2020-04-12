package ooga.view;

import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import ooga.board.Board;
import org.w3c.dom.Text;

public class GameScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private BorderPane root;
    private Stage stage;
    private Scene scene;
    private BoardView boardView;
    private RightView rightView;

    public GameScreen(Stage stage, int width, int height, Map<Point2D, String> locations) {
        this.stage = stage;
        initializeView(width, height, locations);
        stage.show();
    }

    private void initializeView(int width, int height, Map<Point2D, String> locations){
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        root = new BorderPane();
        scene = new Scene(root);
        scene.getStylesheets().add(res.getString("GameStyleSheet"));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));

        Pane canvas = new Pane();
        boardView = new BoardView(width, height, "Black", locations);
        canvas.getChildren().addAll(boardView.getCells());
        root.getChildren().addAll(canvas);

        rightView = new RightView();
        root.getChildren().add(rightView.getConsole());
    }

    public BoardView getBoardView() {
        return boardView;
    }
}
