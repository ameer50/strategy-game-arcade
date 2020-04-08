package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ooga.controller.PieceClickedInterface;

import java.awt.geom.Point2D;
import java.util.List;
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
    private BoardView board;

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

        board = new BoardView(Integer.parseInt(nameDim.get("width")), Integer.parseInt(nameDim.get("height")), "Black", pieceLocations);
        canvas.getChildren().addAll(board.getCells());
        root.getChildren().addAll(canvas);

        //ArrangementView ar = new ChessArrangementView(Integer.parseInt(nameDim.get("width")), Integer.parseInt(nameDim.get("height")), board.getCellSideLength(), "Black", pieceLocations);
        root.getChildren().addAll(board.getPieces());
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public void onPieceClicked(PieceClickedInterface clicked){

        for(int i =0; i< board.getBoardDimension(); i++){
            for(int j =0; j < board.getBoardDimension(); j++){
                board.getCell(i, j).setClickedFunction(clicked);
            }
        }
    }

    public void highlightValidMoves(List<Point2D> pointPairs) {
        if (pointPairs == null){
            return;
        }
        for (Point2D point : pointPairs) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            board.getCell(x,y).toggleYellow();
        }
    }

}
