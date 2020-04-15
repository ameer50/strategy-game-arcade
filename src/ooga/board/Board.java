package ooga.board;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ooga.ProcessCoordinateInterface;

public abstract class Board implements Serializable {

  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String BOTTOM_COLOR = "bottomColor";
  protected String bottomColor;
  protected Map<String, Pair<String, Integer>> pieceTypeMap;
  protected Map<String, List<Piece>> pieceColorMap;
  protected BiMap<Point2D, Piece> pieceLocationBiMap;
  protected int height;
  protected int width;
  protected boolean over;
  protected ProcessCoordinateInterface promoteAction;

  public Board(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, Pair<String, Integer>> pieceTypeMap) {
    height = Integer.parseInt(settings.get(HEIGHT));
    width = Integer.parseInt(settings.get(WIDTH));
    bottomColor = settings.get(BOTTOM_COLOR);
    over = false;

    pieceLocationBiMap = HashBiMap.create();
    this.pieceTypeMap = pieceTypeMap;
    initializePieces(locations);
  }

  /**
   Set up the board from the config file.
   **/
  private void initializePieces(Map<Point2D, String> locations) {
    int ID = 0;
    for (Point2D point: locations.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();

      String pieceId = locations.get(point);
      String pieceColor = pieceId.split("_")[0];
      String pieceName = pieceId.split("_")[1];
      Pair<String, Integer> pieceInfo = pieceTypeMap.get(pieceId);

      String movePattern = pieceInfo.getKey();
      int score = pieceInfo.getValue();
      Piece piece = new Piece(pieceName, movePattern, score, pieceColor, ID++);

      pieceLocationBiMap.put(new Point2D.Double(x, y), piece);
    }
  }

  public void print() {
    for(int i=0; i< height; i++){
      for(int j=0; j< width; j++){
        System.out.print(getPieceAt(i, j) + ", ");
      }
      System.out.println("");
    }
  }

  /**
   Get piece at the specified coordinates.
   @return the Piece object at x, y; null if nothing in the cell.
   **/
  public Piece getPieceAt(int i, int j) {
    if (isCellInBounds(i, j)) {
      return pieceLocationBiMap.get(new Point2D.Double(i, j));
    } else {
      return null;
    }
  }

  /**
   Get piece at the specified coordinates.
   @return the Piece object at x, y; null if nothing in the cell.
   **/
  public void putPieceAt(int i, int j, Piece input) {
    if (isCellInBounds(i, j)) {
      pieceLocationBiMap.forcePut(new Point2D.Double(i, j), input);
    }
  }

  public List<Point2D> getLocsOfColor(String color) {
    List pieceList = new ArrayList<>();
    for (Point2D point: pieceLocationBiMap.keySet()) {
      Piece piece = pieceLocationBiMap.get(point);
      if (piece.getColor().equals(color)) {
        pieceList.add(point);
      }
    }
    return pieceList;
  }

  public void placePiece(int i, int j, Piece piece) {
    pieceLocationBiMap.forcePut(new Point2D.Double(i, j), piece);
  }

  /**
   @param i potential x-coordinate.
   @param j potential y-coordinate.
   @return true if the cell coordinated are within the bounds of the board.
   **/
  public boolean isCellInBounds(int i, int j) { return i >= 0 && i < height && j >= 0 && j < width; }

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
   Execute the desired move
   @param endX new x position
   @param endY new y position
   @return score from completing this move
   **/
  public abstract int doMove(int startX, int startY, int endX, int endY, boolean undo);

  public abstract List<Point2D> getValidMoves(int i, int j);

  public int getScore(String color) {
    int score = 0;
    for (Piece piece: pieceLocationBiMap.values()) {
      int value = piece.getValue();
      int multiplier = 0;
      if (piece.getColor().equals(color)) {
        multiplier = 1;
      } else {
        multiplier = -1;
      }
      score += (value*multiplier);
    }
    return score;
  }

  public List<List<Integer>> getPossibleMoves(String color) {
    List<Point2D> possibleLocs = getLocsOfColor(color);
    return movesFromLocs(possibleLocs);
  }

  public List<List<Integer>> movesFromLocs(List<Point2D> locs) {
    List<List<Integer>> moveList = new ArrayList<>();
    for (Point2D fromPoint: locs) {
      int fromX = (int) fromPoint.getX();
      int fromY = (int) fromPoint.getY();
      List<Point2D> toPoints = getValidMoves(fromX, fromY);
      for (Point2D toPoint: toPoints) {
        moveList.add(Arrays.asList(fromX, fromY, (int) toPoint.getX(), (int) toPoint.getY()));
      }
    }
    return moveList;
  }

  public boolean isGameOver() {
    return over;
  }

  public Board getCopy() {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
      objOutputStream.writeObject(this);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
      return (Board) objInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}

