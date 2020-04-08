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

    private Stage stage;
    private Scene scene;
    private Map<String, String> nameDim;
    private Map<Point2D, String> pieceLocations;

    public GameScreen(Stage stage, Map<String, String> nameDim, Map<Point2D, String> pieceLocations){
        this.stage = stage;
        this.nameDim = nameDim;
        this.pieceLocations = pieceLocations;
        startView();
        stage.show();

    }

    private void startView(){
        BorderPane root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));

        Pane canvas = new Pane();
//        Rectangle rectangle = new Rectangle(100,100);
//        canvas.setPrefSize(100, 100);
//        rectangle.relocate(470,470);
//        rectangle.getStyleClass().add("cellcolor1");
//        HBox rt = new HBox();
//        rt.getChildren().addAll(rectangle);
//        rt.setLayoutX(470);
//        rt.setLayoutY(470);
//        rt.getStyleClass().add("yellowborder");
//
//        rt.setOnMouseClicked( ( e ) ->
//        {
//            rt.getStyleClass().add("blackborder");
//        } );

        //rt2.getStyleClass().add("yellowborder");

        BoardView board = new BoardView(Integer.parseInt(nameDim.get("height")));
        canvas.getChildren().addAll(board.getCells());
        board.getCell(0, 0).toggleRed();
        root.getChildren().addAll(canvas);




        //BoardView theBoard = new BoardView("ChessBoard");
        //root.getChildren().add(theBoard.getBoardView());
        ArrangementView ar = new ChessArrangementView(Integer.parseInt(nameDim.get("height")), 45, 75, "Black");
        root.getChildren().addAll(ar.gamePieces());





    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }
}
