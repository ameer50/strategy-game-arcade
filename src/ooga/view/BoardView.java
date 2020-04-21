package ooga.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ooga.ProcessCoordinateInterface;
import ooga.controller.Controller;
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
    private int width;
    private int height;

    private double cellSize;
    private double cellSpan;

    private Map<Point2D, String> pieceLocations;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private Point2D selectedLocation;
    private static final int ANIM_DURATION = 20;
    private List<CellView> icons;

    public BoardView(int width, int height, Map<Point2D, String> locations) {
        this.width = width;
        this.height = height;
        cellArray = new CellView[width][height];
        cellList = new CellView[width * height + 2];

        cellSize = BOARD_WIDTH/width;
        cellSpan = cellSize+PIECE_SPACE;
        this.pieceLocations = locations;
        initialize();
    }

    public void initialize() {
        checkeredColor();
        fillCells();
        setUpPieces();
    }

    public CellView getCellAt(int x, int y) {

        if (inBounds(x,y)) {
            return cellArray[x][y];
        }

        // special case that checks to return icon views located in indices not on the board itself
        if ((x*width + y) <= cellList.length - 1 && (x*width + y) >= 0){
            return (CellView) cellList[x * width + y];
        }
        return null;
    }

    public CellView getCellAt(Point2D location) {
        return getCellAt((int) location.getX(), (int) location.getY());
    }

    public void checkeredColor() {
        colorSequence1 = new ArrayList<>();
        for (int i = 0; i < width; i++){
            if (i % 2 == 0) colorSequence1.add("cellcolor2");
            else colorSequence1.add("cellcolor1");
        }
        colorSequence2 = new ArrayList<>(colorSequence1);
        Collections.reverse(colorSequence2);
    }

    private void fillCells() {
        int index = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                String color = (i % 2 == 0) ? colorSequence2.get(j) : colorSequence1.get(j);
                cellArray[i][j] = new CellView(new Point2D.Double(i,j), (BOARD_XOFFSET+(cellSpan*j)),
                    (BOARD_YOFFSET+(cellSpan*i)), cellSize, cellSize, color);
                cellList[index] = cellArray[i][j];
                index++;
            }
        }
        CellView playerOneIcon = new IconView(new Point2D.Double(width, 0), 0, 0, cellSize / 2, cellSize / 2, "cellcolor1");
        CellView playerTwoIcon = new IconView(new Point2D.Double(width, 1), 0, 0, cellSize / 2, cellSize / 2, "cellcolor1");
        cellList[index] = playerOneIcon;
        cellList[index+1] = playerTwoIcon;
        icons = List.of(playerOneIcon, playerTwoIcon);

        for (CellView cell : this) {
            cell.setNoBorderFunction((coordinate) -> {
                for (CellView c : this) {
                    c.toggleNoBorder();
                }
            });
        }
    }

    private void setUpPieces() {
        for (Point2D point : pieceLocations.keySet()) {
            this.getCellAt(point).setPiece(new PieceView(pieceLocations.get(point)));
        }
    }

    public List<CellView> getIcons() {
        return icons;
    }

    public void arrangePlayerIcons(String icon, String playerOneColor, String playerTwoColor) {
        createplayerIcon(icon, playerOneColor, 0);
        createplayerIcon(icon, playerTwoColor, 1);
    }

    private void createplayerIcon(String icon, String playerColor, int y){
        IconView playerIcon = (IconView) getCellAt(width, y);
        String playerIconName = String.format("%s_%s", playerColor, icon);

        playerIcon.setIconName(playerIconName);
        playerIcon.setPiece(new PieceView(playerIconName));
    }

    public StackPane[] getCells() {
        return cellList;
    }

    public void highlightValidMoves(List<Point2D> validMoves) {
        if (validMoves == null){
            return;
        }
        for (Point2D point : validMoves) {
            this.getCellAt(point).toggleYellow();
        }
    }

    public void doMove(Move m) {
        CellView initCell = getCellAt(m.getStartLocation());
        CellView finalCell = getCellAt(m.getEndLocation());
        PieceView initCellPiece = initCell.getPiece();
        finalCell.setPiece(initCellPiece);

        initCell.setPiece(null);
    }

    public void replenishIcon(Move m) {
        CellView initCell = getCellAt(m.getStartLocation());
        if (m.isPieceGenerated()) {
            initCell.setPiece(new PieceView(((IconView) initCell).getIconName()));
        }
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

    public int getWidth(){ return width; }

    public int getHeight(){ return height; }

    public void setSelectedLocation(Point2D coordinate) {
        selectedLocation = coordinate;
        this.getCellAt(coordinate).toggleRed();
    }

    public Point2D getSelectedLocation() { return selectedLocation; }

    @Override
    public Iterator<CellView> iterator() {
        return new Iterator<>() {
            private int i = 0;
            @Override
            public boolean hasNext() {  return i < cellList.length;  }

            @Override
            public CellView next() { return (CellView) cellList[i++]; }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove cell from board.");
            }
        };
    }

    private boolean inBounds(int x, int y){
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
