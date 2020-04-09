package ooga.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import ooga.controller.CellClickedInterface;

import java.awt.geom.Point2D;
import java.util.List;
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

        board = new BoardView(Integer.parseInt(nameDim.get("width")), Integer.parseInt(nameDim.get("height")), "Black", pieceLocations);
        canvas.getChildren().addAll(board.getCells());
        root.getChildren().addAll(canvas);

        //ArrangementView ar = new ChessArrangementView(Integer.parseInt(nameDim.get("width")), Integer.parseInt(nameDim.get("height")), board.getCellSideLength(), "Black", pieceLocations);
        root.getChildren().addAll(board.getPieces());
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }

    public void onPieceClicked(CellClickedInterface clicked){
        for(int i =0; i< board.getBoardDimension(); i++){
            for(int j =0; j < board.getBoardDimension(); j++){
                board.getCell(i, j).setPieceClickedFunction(clicked);
            }
        }
    }

    public void onMoveClicked(CellClickedInterface clicked){
        for(int i =0; i< board.getBoardDimension(); i++){
            for(int j =0; j < board.getBoardDimension(); j++){
                board.getCell(i, j).setMoveClickedFunction(clicked);
            }
        }
    }

    public void movePiece(int final_x, int final_y, Pair<Point2D, Double> p) {
        int init_x = (int) p.getKey().getX();
        int init_y = (int) p.getKey().getY();
        CellView initCell = board.getCell(init_x, init_y);
        CellView finalCell = board.getCell(final_x, final_y);
        if (finalCell.getPiece() != null) {
            root.getChildren().remove(finalCell.getPiece().getIVShape());
        }
        // update final cell in grid
        finalCell.setPiece(initCell.getPiece());
        // update piece image
        finalCell.getPiece().setX(board.getPieceOffsetX() + board.getPieceDeltaX() * final_y);
        finalCell.getPiece().setY(board.getPieceOffsetY() + board.getPieceDeltaY() * final_x);
        initCell.setPiece(null);
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
