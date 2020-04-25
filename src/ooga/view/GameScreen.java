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

    public static final String GAME_STYLE_SHEET = "GameStyleSheet";
    public static final String GAME_STAGE_TITLE = "GameStageTitle";
    public static final String GAME_DARK_SHEET = "GameDarkSheet";
    public static final String STYLE_SHEET = "StyleSheet";
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;
    private Stage stage;
    private Scene scene;
    private BoardView boardView;
    private DashboardView dashboardView;
    private String gameStyle;
    private boolean isDarkMode;

    public GameScreen(Stage stage, int numRows, int numCols, Map<Point2D, String> locations) {
        this.stage = stage;
        this.gameStyle = res.getString(GAME_STYLE_SHEET);
        this.isDarkMode = false;
        initializeView(numRows, numCols, locations);
        stage.show();
    }

    private void initializeView(int width, int height, Map<Point2D, String> locations) {
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        BorderPane root = new BorderPane();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(res.getString(GAME_STAGE_TITLE));
        scene.getStylesheets().add(gameStyle);

        Pane canvas = new Pane();
        boardView = new BoardView(width, height, locations);
        canvas.getChildren().addAll(boardView.getCells());
        canvas.getChildren().removeAll(boardView.getIcons());

        dashboardView = new DashboardView();
        root.getChildren().addAll(canvas, dashboardView.getDisplayBox());
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public DashboardView getDashboardView() {
        return dashboardView;
    }

    public void toggleGameDarkMode() {
        scene.getStylesheets().remove(gameStyle);
        gameStyle = gameStyle.equals(res.getString(GAME_STYLE_SHEET)) ? res.getString(GAME_DARK_SHEET) : res.getString(GAME_STYLE_SHEET);
        isDarkMode = !isDarkMode;
        scene.getStylesheets().add(gameStyle);
    }

    public void enableGameCSS(String cssStyle) {
        if (!isDarkMode) {
            scene.getStylesheets().remove(gameStyle);
            gameStyle = res.getString(cssStyle + STYLE_SHEET);
            scene.getStylesheets().add(gameStyle);
        }
    }
}
