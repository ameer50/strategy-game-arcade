package ooga.board;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ooga.ProcessCoordinateInterface;
import ooga.controller.CopyUtility;
import ooga.custom.MoveNode;
import ooga.history.Move;

public abstract class Board implements Serializable {

  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String BOTTOM_COLOR = "bottomColor";
  protected Map<String, Pair<String, Integer>> pieceTypeMap;
  protected Map<String, MoveNode> pieceMoves;
  protected Map<String, Long> pieceScores;
  protected BiMap<Point2D, Piece> pieceBiMap;
  protected Map<String, String> settings;
  protected int height;
  protected int width;
  protected String bottomColor;
  protected boolean over;
  protected ProcessCoordinateInterface promoteAction;
  protected ProcessCoordinateInterface captureAction;

  public Board(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, Pair<String, Integer>> pieceTypeMap) {
    width = Integer.parseInt(settings.get(WIDTH));
    height = Integer.parseInt(settings.get(HEIGHT));
    bottomColor = settings.get(BOTTOM_COLOR);
    over = false;

    pieceBiMap = HashBiMap.create();
    this.settings = settings;
    this.pieceTypeMap = pieceTypeMap;
    this.pieceMoves = null;
    initializePieces(locations);
  }

  public Board(int width, int height, Map<Point2D, String> locations,
      Map<String, MoveNode> pieceMoves, Map<String, Long> pieceScores) {
    this.width = width;
    this.height = height;
    /* TODO: replace with value in JSON */
    bottomColor = "White";
    over = false;

    pieceBiMap = HashBiMap.create();
    this.settings = null;
    this.pieceTypeMap = null;
    this.pieceMoves = pieceMoves;
    this.pieceScores = pieceScores;
    initializePieces(locations);
  }

  /**
   Set up the board from the configuration file (XML or JSON).
   **/
  private void initializePieces(Map<Point2D, String> locations) {
    int ID = 0;
    for (Point2D point: locations.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();

      String pieceStr = locations.get(point);
      String[] pieceArr = pieceStr.split("_");
      String pieceColor = pieceArr[0];
      String pieceName = pieceArr[1];

      if (settings != null) {
        Pair<String, Integer> pieceInfo = pieceTypeMap.get(pieceStr);
        int score = pieceInfo.getValue();
        String pattern = pieceInfo.getKey();
        Piece piece = new Piece(pieceName, pattern, score, pieceColor, ID++);
        pieceBiMap.put(new Point2D.Double(x, y), piece);
      } else {
        Piece piece = new Piece(pieceName, pieceMoves.get(pieceName),
            Math.toIntExact(pieceScores.get(pieceName)), pieceColor, ID++);
        pieceBiMap.put(new Point2D.Double(x, y), piece);
      }
    }
  }

  public void print() {
    for (int i=0; i<height; i++) {
      for (int j=0; j<width; j++) {
        System.out.print(String.format("%s, ", getPieceAt(i, j)));
      }
      System.out.println("");
    }
  }

  /**
   Get the piece at the specified coordinates.
   @return the Piece object at x, y; null if no piece in the cell.
   **/
  public Piece getPieceAt(int i, int j) {
    if (isCellInBounds(i, j)) {
      return pieceBiMap.get(new Point2D.Double(i, j));
    } else {
      return null;
    }
  }

  public Piece getPieceAt(Point2D location) {
    if (isCellInBounds((int) location.getX(), (int) location.getY())) {
      return pieceBiMap.get(location);
    } else {
      return null;
    }
  }

  /**
   Put the piece at the specified coordinates.
   **/
  public void putPieceAt(Point2D location, Piece piece) {
    if (isCellInBounds(location)) {
      pieceBiMap.forcePut(location, piece);
    }
  }

  public void placePiece(int i, int j, Piece piece) {
    pieceBiMap.forcePut(new Point2D.Double(i, j), piece);
  }

  /**
   * Return a list of piece locations for pieces of a particular color.
   * @param color the color of the pieces whose locations to be returned.
   * @return a list of locations.
   */
  public List<Point2D> getPointsOfColor(String color) {
    List locList = new ArrayList<>();
    for (Point2D point: pieceBiMap.keySet()) {
      Piece piece = pieceBiMap.get(point);
      if (piece.getColor().equals(color)) {
        locList.add(point);
      }
    }
    return locList;
  }

  /**
   @param i the potential x-coordinate.
   @param j the potential y-coordinate.
   @return true if the cell coordinates are within the bounds of the board.
   **/
  public boolean isCellInBounds(int i, int j) {
    return i >= 0 && i < height && j >= 0 && j < width;
  }

  public boolean isCellInBounds(Point2D location) {
    return isCellInBounds((int) location.getX(), (int) location.getY());
  }

  public int getHeight() { return height; }

  public int getWidth() { return width; }

  public void setOnPiecePromoted(ProcessCoordinateInterface promoteAction) {
    this.promoteAction = promoteAction;
  }

  /**
   Check the board to see if the game has been completed and a winner has been found.
   @return true if there was a winner.
   **/
  public abstract String checkWon();

  /**
   Execute the desired move, represented by a Move object.
   @param move the object which will be used to operate on a piece
   @return the score from completing the move
   **/
  public abstract void doMove(Move move);

  public abstract List<Point2D> getValidMoves(int i, int j);

  /**
   * @param color the color of the team whose board score is desired
   * @return the score for the team
   */
  public int getScore(String color) {
    int score = 0;
    for (Piece piece: pieceBiMap.values()) {
      if (piece == null) {
        continue;
      }
      int value = piece.getValue();
      int multiplier = (piece.getColor().equals(color)) ? 1 : -1;
      score += (value*multiplier);
    }
    return score;
  }

  /**
   * @param color the color of the team whose moves are desired
   * @return a nested List of integers representing moves as {startX, startY, endX, endY}
   */
  public List<List<Integer>> getPossibleMoves(String color) {
    List<Point2D> possiblePoints = getPointsOfColor(color);
    return movesFromPoints(possiblePoints);
  }

  private List<List<Integer>> movesFromPoints(List<Point2D> points) {
    List<List<Integer>> moveList = new ArrayList<>();
    for (Point2D startPoint: points) {
      int startX = (int) startPoint.getX();
      int startY = (int) startPoint.getY();

      List<Point2D> endPoints = getValidMoves(startX, startY);
      for (Point2D endPoint: endPoints) {
        moveList.add(Arrays.asList(startX, startY, (int) endPoint.getX(), (int) endPoint.getY()));
      }
    }
    return moveList;
  }

  public Board getCopy() {
    /* A test for a non-custom board */
    if ((settings != null) & (settings.size() != 0) ){
      return copyNotCustom();
    } else {
      return copyCustom();
    }
  }

  private Board copyNotCustom() {
    CopyUtility utility = new CopyUtility();
    Map<String, String> settingsCopy = (Map<String, String>) utility.getSerializedCopy(settings);
    Map<String, Pair<String, Integer>> pieceTypeMapCopy =
        (Map<String, Pair<String, Integer>>) utility.getSerializedCopy(pieceTypeMap);
    Map<Point2D, String> locationsCopy = new HashMap<>();
    /* Make sure that the copying mechanism below works. */
    for (Point2D point: pieceBiMap.keySet()) {
      Piece piece = pieceBiMap.get(point);
      if (piece != null) {
        locationsCopy.put((Point2D) point.clone(), piece.getFullName());
      }
    }

    try {
      Constructor<? extends Board> constructor =
          this.getClass().getDeclaredConstructor(Map.class, Map.class, Map.class);
      Board copy = constructor.newInstance(settingsCopy, locationsCopy, pieceTypeMapCopy);
      return copy;
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
        InvocationTargetException e) {
      // e.printStackTrace();
    }
    return null;
  }

  private Board copyCustom() {
    /* FIXME: implement */
    return null;
  }

  public boolean isGameOver() {
    return over;
  }

  public BiMap<Point2D, Piece> getPieceBiMap() {
    return pieceBiMap;
  }

  public void setOnPieceCaptured(ProcessCoordinateInterface captureAction) {
    this.captureAction = captureAction;
  }
}

