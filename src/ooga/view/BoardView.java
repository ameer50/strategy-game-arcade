package ooga.view;

import java.awt.Image;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ooga.CellClickedInterface;
import java.awt.geom.Point2D;
import java.util.*;

public class BoardView implements BoardViewInterface, Iterable<CellView> {

    private CellView[][] cellArray;
    private StackPane[] cellList;
    private static final int BOARD_XOFFSET = 35;
    private static final int BOARD_YOFFSET = 35;
    private static final int PIECE_SPACE = 6;
    private static final double BOARD_WIDTH = 600;
    private static final double BOARD_HEIGHT = 600;
    private List<String> colorSequence1;
    private List<String> colorSequence2;
    private int rowNum;
    private int colNum;

    private double cellSize;
    private double cellSpan;

    private String playerChoice;
    private Map<Point2D, String> pieceLocations;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private Point2D selectedLocation;
    private static final int ANIM_DURATION = 20;

    public BoardView (int rows, int cols, String playerChoice, Map<Point2D, String> locs, BorderPane root) {
        rowNum = rows;
        colNum = cols;
        cellArray = new CellView[rows][cols];
        cellList = new CellView[rows * cols];
        //FIXME: is this data duplication?
        cellSize = (BOARD_WIDTH) / rows;
        cellSpan = cellSize + PIECE_SPACE;

        this.playerChoice = playerChoice;
        this.pieceLocations = locs;

        initialize();
    }

    public void initialize() {
        checkeredColor();
        fillCellStructures();
        setUpPieces();
    }

    public CellView getCellAt(int x, int y) {
        return cellArray[x][y];
    }

    public void checkeredColor() {
        colorSequence1 = new ArrayList<>();
        for (int i = 0; i < rowNum; i++){
            if (i % 2 == 0) colorSequence1.add("cellcolor1");
            else colorSequence1.add("cellcolor2");
        }
        colorSequence2 = new ArrayList<>(colorSequence1);
        Collections.reverse(colorSequence2);
    }

    private void fillCellStructures() {
        int index = 0;
        for (int i = 0; i < rowNum; i++) {
            for(int j = 0; j < colNum; j++) {
                String color = (i % 2 == 0) ? colorSequence2.get(j) : colorSequence1.get(j);
                cellArray[i][j] = new CellView(i, j, (BOARD_XOFFSET + (cellSpan * j)),
                    (BOARD_YOFFSET + (cellSpan * i)),
                    cellSize, cellSize, color);
                cellList[index] = cellArray[i][j];
                index++;
                cellArray[i][j].setNoBorderFunction((a, b) -> {
                    for (int x = 0; x < rowNum; x++) {
                        for (int y = 0; y < colNum; y++) {
                            cellArray[x][y].toggleNoBorder();
                        }
                    }
                });
            }
        }
    }

    private void setUpPieces() {
        for (Point2D point : pieceLocations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            this.getCellAt(x, y).setPiece(new PieceView(res.getString(pieceLocations.get(point))));
        }
    }

    public StackPane[] getCells() {
        return cellList;
    }

    public void highlightValidMoves(List<Point2D> validMoves) {
        if (validMoves == null){
            return;
        }
        for (Point2D point : validMoves) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            this.getCellAt(x,y).toggleYellow();
        }
    }

    public void movePiece(int finalX, int finalY) {
        int initX = (int) selectedLocation.getX();
        int initY = (int) selectedLocation.getY();
        CellView initCell = this.getCellAt(initX, initY);
        CellView finalCell = this.getCellAt(finalX, finalY);
        PieceView piece = initCell.getPiece();
        finalCell.setPiece(piece);

//        TranslateTransition tr = new TranslateTransition(Duration.millis(ANIM_DURATION), piece.getImage());
//        tr.setFromX(tr.getFromX());
//        tr.setFromY(tr.getFromY());
//        tr.setByX(getDeltaX()*(finalY-initY));
//        tr.setByY(getDeltaY()*(finalX-initX));
//        tr.play();

        initCell.setPiece(null);
    }

    public void setOnPieceClicked(CellClickedInterface clicked) {
        for (int i = 0; i < rowNum; i++){
            for (int j = 0; j < colNum; j++){
                this.getCellAt(i, j).setPieceClicked(clicked);
            }
        }
    }

    public void setOnMoveClicked(CellClickedInterface clicked) {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                this.getCellAt(i, j).setMoveClicked(clicked);
            }
        }
    }

    public int getRowNum(){ return rowNum; }

    public int getColNum(){ return colNum; }

    public double getCellSpan(){ return cellSpan; }

    public void setSelectedLocation(int x, int y) { selectedLocation = new Point2D.Double(x, y); }

    public Point2D getSelectedLocation() { return selectedLocation; }

    @Override
    public Iterator<CellView> iterator() {
        return new Iterator<>() {
            private int i = 0;
            private int j = 0;

            @Override
            public boolean hasNext() {
                return !(i == cellArray.length - 1 && j == cellArray[i].length - 1);
            }

            @Override
            public CellView next() {
                if (j <= cellArray[i].length - 1) return cellArray[i][j++];
                i++;
                j = 0;
                return cellArray[i][j];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove cell from board.");
            }
        };
    }
}
