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
    private String gameStyle;

    public GameScreen(Stage stage, int width, int height, Map<Point2D, String> locations) {
        this.stage = stage;
        this.gameStyle = res.getString("GameStyleSheet");
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
        scene.getStylesheets().add(gameStyle);

        Pane canvas = new Pane();
        boardView = new BoardView(width, height, locations);
        canvas.getChildren().addAll(boardView.getCells());
        canvas.getChildren().removeAll(boardView.getIcons());
        root.getChildren().addAll(canvas);

        dashboardView = new DashboardView();
        root.getChildren().add(dashboardView.getDisplay());
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public DashboardView getDashboardView() { return dashboardView;}

    public void toggleGameDarkMode(){
        scene.getStylesheets().remove(gameStyle);
        gameStyle = (gameStyle.equals(res.getString("GameStyleSheet"))) ? res.getString("GameDarkSheet") : res.getString("GameStyleSheet");
        scene.getStylesheets().add(gameStyle);
    }



}
