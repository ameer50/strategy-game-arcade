package ooga.view;

import javafx.scene.layout.StackPane;
import ooga.ProcessCoordinateInterface;
import ooga.history.Move;

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
    private int unitWidth;
    private int unitHeight;

    private double cellSize;
    private double cellSpan;

    private String colorChoice;
    private Map<Point2D, String> pieceLocations;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private Point2D selectedLocation;
    private static final int ANIM_DURATION = 20;

    public BoardView(int width, int height, String colorChoice, Map<Point2D, String> locations) {
        unitWidth = width;
        unitHeight = height;
        cellArray = new CellView[width][height];
        cellList = new CellView[width * height];

        cellSize = BOARD_WIDTH/width;
        cellSpan = cellSize+PIECE_SPACE;
        this.colorChoice = colorChoice;
        this.pieceLocations = locations;
        initialize();
    }

    public void initialize() {
        checkeredColor();
        fillCells();
        setUpPieces();
    }

    public CellView getCellAt(int x, int y) {
        return cellArray[x][y];
    }

    public CellView getCellAt(Point2D location) {
        return cellArray[(int) location.getX()][(int) location.getY()];
    }

    public void checkeredColor() {
        colorSequence1 = new ArrayList<>();
        for (int i = 0; i < unitWidth; i++){
            if (i % 2 == 0) colorSequence1.add("cellcolor1");
            else colorSequence1.add("cellcolor2");
        }
        colorSequence2 = new ArrayList<>(colorSequence1);
        Collections.reverse(colorSequence2);
    }

    private void fillCells() {
        int index = 0;
        for (int i=0; i < unitWidth; i++) {
            for (int j=0 ; j < unitHeight; j++) {
                String color = (i % 2 == 0) ? colorSequence2.get(j) : colorSequence1.get(j);
                cellArray[i][j] = new CellView(i, j, (BOARD_XOFFSET+(cellSpan*j)),
                    (BOARD_YOFFSET+(cellSpan*i)), cellSize, cellSize, color);
                cellList[index] = cellArray[i][j];
                index++;
                cellArray[i][j].setNoBorderFunction((a, b) -> {
                    for (CellView c: this) {
                        c.toggleNoBorder();
                    }
                });
            }
        }
    }

    private void setUpPieces() {
        for (Point2D point : pieceLocations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            this.getCellAt(x, y).setPiece(new PieceView(pieceLocations.get(point)));
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

    public void doMove(Move m) {
        CellView initCell = getCellAt(m.getStartLocation());
        CellView finalCell = getCellAt(m.getEndLocation());
        PieceView initCellPiece = initCell.getPiece();
        PieceView finalCellPiece = finalCell.getPiece();

        for (Point2D pieceLocation: m.getCapturedPiecesAndLocations().values()) {
            this.getCellAt(pieceLocation).setPiece(null);
        }

        // only sets the final cell if it is empty, or a piece is being captured (thus avoids the issue of promotion
        // and the image preemptively changing)
        if (finalCellPiece == null || !finalCellPiece.getColor().equals(initCellPiece.getColor())) {
            finalCell.setPiece(initCellPiece);
        }

//        TranslateTransition tr = new TranslateTransition(Duration.millis(ANIM_DURATION), piece.getImage());
//        tr.setFromX(tr.getFromX());
//        tr.setFromY(tr.getFromY());
//        tr.setByX(getDeltaX()*(finalY-initY));
//        tr.setByY(getDeltaY()*(finalX-initX));
//        tr.play();

        initCell.setPiece(null);
    }

    public void setOnPieceClicked(ProcessCoordinateInterface clicked) {
        for (CellView cell: this) {
            cell.setPieceClicked(clicked);
        }
    }

    public void setOnMoveClicked(ProcessCoordinateInterface clicked) {
        for (CellView cell: this) {
            cell.setMoveClicked(clicked);
        }
    }

    public int getUnitWidth(){ return unitWidth; }

    public int getUnitHeight(){ return unitHeight; }

    public double getCellSpan(){ return cellSpan; }

    public void setSelectedLocation(int x, int y) {
        selectedLocation = new Point2D.Double(x, y);
        this.getCellAt(x, y).toggleRed();
    }

    public Point2D getSelectedLocation() { return selectedLocation; }

    @Override
    public Iterator<CellView> iterator() {
        return new Iterator<>() {
            private int i = 0;
            private int j = 0;

            @Override
            public boolean hasNext() {
                return !(i == cellArray.length - 1 && j == cellArray[i].length);
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
