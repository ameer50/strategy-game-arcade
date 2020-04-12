package ooga.board;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public abstract class Board{
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  protected Piece[][] pieceGrid;
  protected Map<String, Pair<String, Double>> pieceTypeMap;
  protected Map<Point2D, String> pieceLocations;
  protected int height;
  protected int width;
  protected Map<String, List<Piece>> pieceColorMap;

  public Board(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, Pair<String, Double>> pieceTypeMap) {
    height = Integer.parseInt(settings.get(HEIGHT));
    width = Integer.parseInt(settings.get(WIDTH));
    pieceGrid = new Piece[width][height];
    this.pieceTypeMap = pieceTypeMap;
    pieceLocations = locations;
    pieceColorMap = new HashMap<>();
    // FIXME: This is a data structure that is accessed in three different classes.
    initializePieces(locations);
  }

  /**
   Set up the board from the config file.
   **/
  private void initializePieces(Map<Point2D, String> locations) {
    for (Point2D point: locations.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();

      String pieceId = locations.get(point);
      String pieceColor = pieceId.split("_")[0];
      String pieceName = pieceId.split("_")[1];
      Pair<String, Double> pieceInfo = pieceTypeMap.get(pieceId);

      String movePattern = pieceInfo.getKey();
      double score = pieceInfo.getValue();

      Piece piece = new Piece(pieceName, movePattern, score, pieceColor);
      updatePieceColorMap(piece, pieceColor);
      pieceGrid[x][y] = piece;
    }
  }

  private void updatePieceColorMap(Piece piece, String color) {
    if (!pieceColorMap.keySet().contains(color)) {
      pieceColorMap.put(color, new ArrayList<>());
    } else {
      pieceColorMap.get(color).add(piece);
    }
  }
  /**
   Check the board to see if the game has been completed and a winner has been found.
   @return true if there was a winner.
   **/

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
      return pieceGrid[i][j];
    } else {
      return null;
    }
  }

  public List<Piece> getPiecesOfColor(String color) {
    if (pieceColorMap.keySet().contains(color)) {
      return List.copyOf(pieceColorMap.get(color));
    } else {
      return null;
    }
  }

  public abstract boolean checkWon();

  /**
   Execute the desired move
   @param endX new x position
   @param endY new y position
   @return score from completing this move
   **/
  public abstract double doMove(int startX, int startY, int endX, int endY);

  public abstract List<Point2D> getValidMoves(int i, int j, String color);

  public Map<Point2D, String> getPieceLocations() {
    return pieceLocations;
  }

  public void placePiece(int x, int y, Piece piece){
    this.pieceGrid[x][y] = piece;
  }

  /**
   @param x potential x-coordinate.
   @param y potential y-coordinate.
   @return true if the cell coordinated are within the bounds of the board.
   **/
  public boolean isCellInBounds(int x, int y) {
    return x >= 0 && x < height && y >= 0 && y < width; }

  public int getHeight() { return height; }
  public int getWidth() { return width; }
}