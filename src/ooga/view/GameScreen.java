package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
    private BoardView boardView;
    private DashboardView dashboardView;
    private int[] recentLocations;

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

        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));

        Pane canvas = new Pane();
        boardView = new BoardView(width, height, "Black", locations);
        canvas.getChildren().addAll(boardView.getCells());
        root.getChildren().addAll(canvas);

        dashboardView = new DashboardView();
        root.getChildren().add(dashboardView.getDisplay());
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public DashboardView getDashboardView() { return dashboardView;}

    public void setRecentLocation(int fromX, int fromY, int toX, int toY){
        recentLocations = new int[]{fromX, fromY, toX, toY};
    }

    public void getUndoState(){
        dashboardView.getUndoState();
    }
}
