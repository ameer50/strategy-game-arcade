package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class ChessArrangementView implements ArrangementView {

    public static final int BOARD_XOFFSET = 35;
    public static final int BOARD_YOFFSET = 35;
    public static final double CELL_YOFFSET = 0.05;
    private static final double PIECE_WIDTH_RATIO = 0.5;
    public static final int CELL_XOFFSET = 4;
    private double PIECE_DELTAX;
    private double PIECE_DELTAY;
    private double PIECE_OFFSETX;
    private double PIECE_OFFSETY;
    private PieceView[][] arrangement;
    private List<ImageView> pieceImages;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private String playerChoice; // either 'Black' or 'White'
    private double pieceHeight;
    private double pieceWidth;
    private Map<Point2D, String> pieceLocations;

    public ChessArrangementView(int rows, int cols, double cellSideLength, String playerChoice, Map<Point2D, String> locs){
        arrangement = new PieceView[rows][cols];
        pieceImages = new ArrayList<>();
        this.playerChoice = playerChoice;
        this.pieceWidth = cellSideLength*PIECE_WIDTH_RATIO;
        this.pieceHeight = cellSideLength;
        System.out.println("cl " + cellSideLength);
        PIECE_OFFSETX = BOARD_XOFFSET + ((cellSideLength)/ CELL_XOFFSET);
        PIECE_OFFSETY = BOARD_YOFFSET + cellSideLength* CELL_YOFFSET;
        PIECE_DELTAX = cellSideLength;
        PIECE_DELTAY = cellSideLength;
        this.pieceLocations = locs;
        initialize();
    }

    @Override
    public void initialize() {
        for (Point2D point : pieceLocations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            arrangement[x][y] = new PieceView(PIECE_OFFSETX + PIECE_DELTAX * y, PIECE_OFFSETY + PIECE_DELTAY * x, pieceWidth, pieceHeight, res.getString(pieceLocations.get(point)));
            pieceImages.add(arrangement[x][y].getIVShape());
        }
    }

    @Override
    public ImageView[] gamePieces() {
        return pieceImages.toArray(new ImageView[0]);
    }

    public PieceView pieceAt(int x, int y) {
        return arrangement[x][y];
    }

}
