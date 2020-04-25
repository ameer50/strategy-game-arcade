package ooga.view;

import javafx.scene.layout.StackPane;
import ooga.controller.ProcessCoordinateInterface;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.util.*;

public class BoardView implements BoardViewInterface, Iterable<CellView> {

    public static final String CELL_COLOR_2 = "cellcolor2";
    public static final String CELL_COLOR_1 = "cellcolor1";
    private CellView[][] cellArray;
    private StackPane[] cellList;

    private static final int BOARD_X_OFFSET = 35;
    private static final int BOARD_Y_OFFSET = 35;
    private static final int PIECE_SPACE = 6;
    private static final double BOARD_WIDTH = 600;

    private List<String> firstColorSequence;
    private List<String> secondColorSequence;

    private int numCols;
    private int numRows;

    private double cellSize;
    private double cellSpan;

    private Map<Point2D, String> pieceLocations;
    private Point2D selectedLocation;
    private List<CellView> icons;

    public BoardView(int numRows, int numCols, Map<Point2D, String> locations) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.pieceLocations = locations;

        cellArray = new CellView[numRows][numCols];
        cellList = new CellView[numRows * numCols + 2];
        cellSize = BOARD_WIDTH / numCols;
        cellSpan = cellSize + PIECE_SPACE;

        initialize();
    }

    public void initialize() {
        createBoardColors();
        createCells();
        setUpPieces();
    }

    public CellView getCellAt(int x, int y) {
        if (isInBounds(x, y)) {
            return cellArray[x][y];
        }
        // special case that checks to return icon views located in indices not on the board itself
        int cellListIndex = x * numCols + y;
        if (cellListIndex < cellList.length && cellListIndex >= 0) {
            return (CellView) cellList[cellListIndex];
        }
        return null;
    }

    public CellView getCellAt(Point2D location) {
        return getCellAt((int) location.getX(), (int) location.getY());
    }

    public void createBoardColors() {
        firstColorSequence = new ArrayList<>();
        for (int i = 0; i < numCols; i++) {
            if (i % 2 == 0) firstColorSequence.add(CELL_COLOR_2);
            else firstColorSequence.add(CELL_COLOR_1);
        }
        secondColorSequence = new ArrayList<>(firstColorSequence);
        Collections.reverse(secondColorSequence);
    }

    private void createCells() {
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String color = (i % 2 == 0) ? secondColorSequence.get(j) : firstColorSequence.get(j);
                CellView cell = cellArray[i][j] = new CellView(new Point2D.Double(i, j), cellSize, cellSize, color);
                cell.setLayoutX(BOARD_X_OFFSET + (cellSpan * j));
                cell.setLayoutY(BOARD_Y_OFFSET + (cellSpan * i));
                cellList[index] = cell;
                index++;
            }
        }
        createIcons();
        setNoBorderFunction();
    }

    private void createIcons() {
        CellView playerOneIcon = new IconView(new Point2D.Double(numRows, 0), cellSize / 2,
                cellSize / 2, CELL_COLOR_1);
        CellView playerTwoIcon = new IconView(new Point2D.Double(numRows, 1), cellSize / 2,
                cellSize / 2, CELL_COLOR_1);
        cellList[cellList.length - 2] = playerOneIcon;
        cellList[cellList.length - 1] = playerTwoIcon;
        icons = List.of(playerOneIcon, playerTwoIcon);
    }

    private void setNoBorderFunction() {
        for (CellView cell : this) {
            cell.setNoBorderFunction(coordinate -> {
                for (CellView c : this) {
                    c.toggleNoBorder();
                }
            });
        }
    }

    private void setUpPieces() {
        for (Point2D point : pieceLocations.keySet()) {
            this.getCellAt(point).setPieceView(new PieceView(pieceLocations.get(point)));
        }
    }

    public List<CellView> getIcons() {
        return icons;
    }

    public void arrangePlayerIcons(String icon, String playerOneColor, String playerTwoColor) {
        createPlayerIcon(icon, playerOneColor, 0);
        createPlayerIcon(icon, playerTwoColor, 1);
    }

    private void createPlayerIcon(String icon, String playerColor, int index) {
        IconView playerIcon = (IconView) getCellAt(numRows, index);
        String playerIconName = String.format("%s_%s", playerColor, icon);

        playerIcon.setIconName(playerIconName);
        playerIcon.setPieceView(new PieceView(playerIconName));
    }

    public StackPane[] getCells() {
        return cellList;
    }

    public void highlightValidMoves(List<Point2D> validMoves) {
        for (Point2D point : validMoves) {
            this.getCellAt(point).toggleYellow();
        }
    }

    public void doMove(Move m) {
        CellView initCell = getCellAt(m.getStartLocation());
        CellView finalCell = getCellAt(m.getEndLocation());
        finalCell.setPieceView(initCell.getPieceView());
        initCell.setPieceView(null);
    }

    public void replenishIcon(Move m) {
        CellView initCell = getCellAt(m.getStartLocation());
        if (m.isPieceGenerated()) {
            initCell.setPieceView(new PieceView(((IconView) initCell).getIconName()));
        }
    }

    public void setOnPieceClicked(ProcessCoordinateInterface function) {
        for (CellView cell : this) {
            cell.setPieceClicked(function);
        }
    }

    public void setOnMoveClicked(ProcessCoordinateInterface function) {
        for (CellView cell : this) {
            cell.setMoveClicked(function);
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setSelectedLocation(Point2D coordinate) {
        selectedLocation = coordinate;
        this.getCellAt(coordinate).toggleRed();
    }

    public Point2D getSelectedLocation() {
        return selectedLocation;
    }

    @Override
    public Iterator<CellView> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < cellList.length;
            }

            @Override
            public CellView next() {
                return (CellView) cellList[i++];
            }
        };
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < numRows && y >= 0 && y < numCols;
    }
}
