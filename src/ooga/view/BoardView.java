package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ooga.CellClickedInterface;
import java.awt.geom.Point2D;
import java.util.*;

public class BoardView implements BoardViewInterface {

    private CellView[][] cellArray;
    private HBox[] cellList;
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
    //TODO: what are the differences b/w these?
    public static final double CELL_YOFFSET = 0.05;
    public static final int CELL_XOFFSET = 4;
    //TODO: are these too necessary?
    private static final double PIECE_WIDTH_RATIO = 0.5;
    private double PIECE_DELTAX;
    private double PIECE_DELTAY;
    private double PIECE_XOFFSET;
    private double PIECE_YOFFSET;
    private List<ImageView> pieceImages;
    private String playerChoice;
    private double pieceWidth;
    private double pieceHeight;
    private Map<Point2D, String> pieceLocations;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private BorderPane root;
    private Point2D selectedLocation;
    private static final int ANIM_DURATION = 20;

    public BoardView(int rows, int cols, String playerChoice, Map<Point2D, String> locs,
        BorderPane root) {
        unitWidth = rows;
        unitHeight = cols;
        cellArray = new CellView[rows][cols];
        cellList = new CellView[rows*cols];
        //FIXME: is this data duplication?
        cellSize = (BOARD_WIDTH)/rows;
        cellSpan = cellSize + PIECE_SPACE;
        pieceImages = new ArrayList<>();
        this.pieceWidth = cellSpan*PIECE_WIDTH_RATIO;
        this.pieceHeight = cellSpan;
        PIECE_XOFFSET = BOARD_XOFFSET+((cellSpan)/CELL_XOFFSET);
        PIECE_YOFFSET = BOARD_YOFFSET+cellSpan*CELL_YOFFSET;

        this.playerChoice = playerChoice;
        PIECE_DELTAX = PIECE_DELTAY = cellSpan;
        this.pieceLocations = locs;
        this.root = root;

        initialize();
    }

    public void initialize() {
        checkeredColor();
        fillCellStructures();
        setUpPieces();
    }

    public void checkeredColor() {
        colorSequence1 = new ArrayList<>();
        for (int i=0; i< unitWidth; i++){
            if (i % 2 == 0) colorSequence1.add("cellcolor1");
            else colorSequence1.add("cellcolor2");
        }
        colorSequence2 = new ArrayList<>(colorSequence1);
        Collections.reverse(colorSequence2);
    }

    private void fillCellStructures() {
        int index = 0;
        for (int i=0; i< unitWidth; i++) {
            for(int j=0; j< unitHeight; j++) {
                String color;
                if (i % 2 == 0) {
                    color = colorSequence2.get(j);
                } else {
                    color = colorSequence1.get(j);
                }
                cellArray[i][j] = new CellView(i, j, (BOARD_XOFFSET + (cellSpan * j)),
                    (BOARD_YOFFSET + (cellSpan * i)),
                    cellSize, cellSize, color);
                cellList[index] = cellArray[i][j];
                index++;
                cellArray[i][j].setNoBorderFunction((a, b) -> {
                    for (int x = 0; x < unitWidth; x++) {
                        for (int y = 0; y < unitHeight; y++) {
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
            cellArray[x][y].setPiece(new PieceView(PIECE_XOFFSET+PIECE_DELTAX*y,
                PIECE_YOFFSET+PIECE_DELTAY*x, pieceWidth, pieceHeight,
                res.getString(pieceLocations.get(point))));
            pieceImages.add(cellArray[x][y].getPiece().getImage());
        }
    }

    public HBox[] getCells() {
        return cellList;
    }

    public void highlightValidMoves(List<Point2D> validMoves) {
        if (validMoves == null){
            return;
        }
        for (Point2D point : validMoves) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            getCell(x,y).toggleYellow();
        }
    }

    public void movePiece(int fromX, int fromY, int toX, int toY) {
        CellView initCell = getCell(fromX, fromY);
        CellView finalCell = getCell(toX, toY);
        PieceView piece = initCell.getPiece();
        if (finalCell.getPiece() != null) {
            root.getChildren().remove(finalCell.getPiece().getImage());
            System.out.println("removed cell!");
            // FIXME: A long chain of calls here...
        }
        finalCell.setPiece(piece);
//        TranslateTransition tr = new TranslateTransition(Duration.millis(ANIM_DURATION), piece.getImage());
//        tr.setFromX(tr.getFromX());
//        tr.setFromY(tr.getFromY());
//        tr.setByX(getDeltaX()*(finalY-initY));
//        tr.setByY(getDeltaY()*(finalX-initX));
//        tr.play();
        ImageView image = piece.getImage();
        image.setX(image.getX() + getDeltaX()*(toY-fromY));
        image.setY(image.getY() + getDeltaY()*(toX-fromX));
        initCell.setPiece(null);
    }

    public void setOnPieceClicked(CellClickedInterface clicked) {
        for (int i=0; i< unitWidth; i++){
            for (int j=0; j< unitHeight; j++){
                getCell(i, j).setPieceClicked(clicked);
            }
        }
    }

    public void setOnMoveClicked(CellClickedInterface clicked) {
        for (int i=0; i< unitWidth; i++) {
            for (int j=0; j< unitHeight; j++) {
                this.getCell(i, j).setMoveClicked(clicked);
            }
        }
    }

    public CellView getCell(int row, int col){ return cellArray[row][col]; }
    public ImageView[] getPieces() { return pieceImages.toArray(new ImageView[0]); }
    public int getUnitWidth(){ return unitWidth; }
    public int getUnitHeight(){ return unitHeight; }
    public double getCellSpan(){ return cellSpan; }
    public void setSelectedLocation(int x, int y) { selectedLocation = new Point2D.Double(x, y); }
    public Point2D getSelectedLocation() { return selectedLocation; }
    public double getPieceOffsetX() {
        return PIECE_XOFFSET;
    }
    public double getPieceOffsetY() {
        return PIECE_YOFFSET;
    }
    public double getDeltaX() {
        return PIECE_DELTAX;
    }
    public double getDeltaY() {
        return PIECE_DELTAY;
    }
}
