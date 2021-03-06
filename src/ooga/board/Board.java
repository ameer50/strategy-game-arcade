package ooga.board;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ooga.controller.ProcessCoordinateInterface;
import ooga.history.Move;
import ooga.utility.CopyUtility;
import ooga.view.SetUpError;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Board implements Serializable {

    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String BOTTOM_COLOR = "bottomColor";
    public static final String COPY_BOARD_ERROR = "Copy (Preset Board) went wrong";
    protected Map<String, String> settings;
    public static final String ICON = "icon";
    protected Map<String, String> pieceMovePatterns;
    protected Map<String, Integer> pieceScores;
    protected BiMap<Point2D, Piece> pieceBiMap;
    protected int height;
    protected int width;
    protected String bottomColor;
    protected String iconType;
    protected boolean over;
    protected ProcessCoordinateInterface promoteAction;
    protected ProcessCoordinateInterface captureAction;

    public Board(Map<String, String> settings, Map<Point2D, String> locations,
                 Map<String, String> pieceMovePatterns, Map<String, Integer> pieceScores) {
        width = Integer.parseInt(settings.get(WIDTH));
        height = Integer.parseInt(settings.get(HEIGHT));
        bottomColor = settings.get(BOTTOM_COLOR);
        iconType = settings.get(ICON);
        over = false;

        pieceBiMap = HashBiMap.create();
        this.settings = settings;
        this.pieceMovePatterns = pieceMovePatterns;
        this.pieceScores = pieceScores;
        initializePieces(locations);
    }

    /**
     * Set up the board with the configuration file (JSON).
     **/
    private void initializePieces(Map<Point2D, String> locations) {
        for (Point2D point : locations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();

            String pieceStr = locations.get(point);
            String[] pieceArr = pieceStr.split("_");
            String pieceColor = pieceArr[0];
            String pieceName = pieceArr[1];

            int score = pieceScores.get(pieceName);
            String pattern = pieceMovePatterns.get(pieceName);
            Piece piece = new Piece(pieceName, pattern, score, pieceColor);
            pieceBiMap.put(new Point2D.Double(x, y), piece);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                builder.append(getPieceAt(i, j));
                if (j != width - 1) {
                    builder.append(", ");
                }
            }
            if (i != height - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public void print() {
        System.out.println(this);
    }

    /**
     * Get the piece at the specified coordinates.
     *
     * @return the Piece object at x, y; null if no piece in the cell.
     **/
    public Piece getPieceAt(int i, int j) {
        if (isCellInBounds(i, j)) {
            return pieceBiMap.get(new Point2D.Double(i, j));
        } else {
            return null;
        }
    }

    public void removePieceAt(Point2D location) {
        if (pieceBiMap.containsKey(location)) {
            pieceBiMap.remove(location);
        }
    }

    public List<Point2D> getPieceLocations() {
        ArrayList<Point2D> pieces = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point2D point = new Point2D.Double(i, j);
                Piece piece = getPieceAt(point);
                if (piece != null) {
                    pieces.add(point);
                }
            }
        }
        return pieces;
    }

    public Piece getPieceAt(Point2D location) {
        if (isCellInBounds((int) location.getX(), (int) location.getY())) {
            return pieceBiMap.get(location);
        } else {
            return null;
        }
    }

    /**
     * Put the piece at the specified coordinates.
     **/
    public void putPieceAt(Point2D location, Piece piece) {
        if (isCellInBounds(location)) {
            pieceBiMap.forcePut(location, piece);
        }
    }

    public void placePieceAt(Point2D coordinate, Piece piece) {
        pieceBiMap.forcePut(coordinate, piece);
    }

    /**
     * Return a list of piece locations for pieces of a particular color.
     *
     * @param color the color of the pieces whose locations to be returned.
     * @return a list of locations.
     */
    public List<Point2D> getPointsOfColor(String color) {
        List locList = new ArrayList<>();
        for (Point2D point : pieceBiMap.keySet()) {
            Piece piece = pieceBiMap.get(point);
            if (piece.getColor().equals(color)) {
                locList.add(point);
            }
        }
        return locList;
    }

    /**
     * @param i the potential x-coordinate.
     * @param j the potential y-coordinate.
     * @return true if the cell coordinates are within the bounds of the board.
     **/
    public boolean isCellInBounds(int i, int j) {
        return i >= 0 && i < height && j >= 0 && j < width;
    }

    public boolean isCellInBounds(Point2D location) {
        return isCellInBounds((int) location.getX(), (int) location.getY());
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Check the board to see if the game has been completed and a winner has been found.
     *
     * @return true if there was a winner.
     **/
    public abstract String checkWon();

    /**
     * Execute the desired move, represented by a Move object.
     *
     * @param move the object which will be used to operate on a piece
     * @return the score from completing the move
     **/
    public abstract void doMove(Move move);

    public abstract List<Point2D> getValidMoves(Point2D coordinate);

    /**
     * @param color the color of the team whose board score is desired
     * @return the score for the team
     */
    public int getScore(String color) {
        int score = 0;
        for (Piece piece : pieceBiMap.values()) {
            if (piece == null) {
                continue;
            }
            int value = piece.getValue();
            int multiplier = (piece.getColor().equals(color)) ? 1 : -1;
            score += (value * multiplier);
        }
        return score;
    }

    /**
     * @param color the color of the team whose moves are desired
     * @return a list of Move objects
     */
    public List<Move> getPossibleMoves(String color) {
        List<Move> moves = new ArrayList<>();
        List<Point2D> starts = new ArrayList<>(pieceBiMap.keySet());
        for (Point2D start : starts) {
            if (pieceBiMap.get(start) != null && pieceBiMap.get(start).getColor().equals(color)) {
                for (Point2D end : getValidMoves(start)) {
                    moves.add(new Move(start, end));
                }
            }
        }
        return moves;
    }

    private List<List<Integer>> movesFromPoints(List<Point2D> points) {
        List<List<Integer>> moveList = new ArrayList<>();
        for (Point2D startPoint : points) {
            int startX = (int) startPoint.getX();
            int startY = (int) startPoint.getY();

            List<Point2D> endPoints = getValidMoves(startPoint);
            for (Point2D endPoint : endPoints) {
                moveList.add(Arrays.asList(startX, startY, (int) endPoint.getX(), (int) endPoint.getY()));
            }
        }
        return moveList;
    }

    public Board getCopy() {
        CopyUtility utility = new CopyUtility();
        Map<String, String> settingsCopy = (Map<String, String>) utility.getSerializedCopy(settings);
        Map<String, String> pieceMovePatternsCopy = (Map<String, String>) utility.getSerializedCopy(pieceMovePatterns);
        Map<String, Integer> pieceScoresCopy = (Map<String, Integer>) utility.getSerializedCopy(pieceScores);

        Map<Point2D, String> locationsCopy = new HashMap<>();
        for (Point2D point : pieceBiMap.keySet()) {
            Piece piece = pieceBiMap.get(point);
            if (piece != null) {
                locationsCopy.put((Point2D) point.clone(), piece.getFullName());
            }
        }

        try {
            Constructor<? extends Board> constructor = this.getClass().getDeclaredConstructor(Map.class, Map.class,
                    Map.class, Map.class);
            Board copy = constructor.newInstance(settingsCopy, locationsCopy, pieceMovePatternsCopy, pieceScoresCopy);
            return copy;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SetUpError(COPY_BOARD_ERROR);
        }
    }

    public void setOnPiecePromoted(ProcessCoordinateInterface promoteAction) {
        this.promoteAction = promoteAction;
    }

    public void setOnPieceCaptured(ProcessCoordinateInterface captureAction) {
        this.captureAction = captureAction;
    }

    public boolean isGameOver() {
        return over;
    }

    public Map<String, String> getPieceMovePatterns() {
        return Map.copyOf(pieceMovePatterns);
    }

    public Map<String, Integer> getPieceScores() {
        return Map.copyOf(pieceScores);
    }

    protected boolean isFull() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (getPieceAt(i, j) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addPlayerIcons(String playerOneColor, String playerTwoColor) {
        pieceBiMap.forcePut(new Point2D.Double(height, 0), new Piece(iconType, "", 0, playerOneColor));
        pieceBiMap.forcePut(new Point2D.Double(height, 1), new Piece(iconType, "", 0, playerTwoColor));
    }
}

