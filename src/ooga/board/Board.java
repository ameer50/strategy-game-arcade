package ooga.board;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public abstract class Board{
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String BOTTOM_COLOR = "bottomColor";
  protected Piece[][] myGrid;
  protected Map<String, Pair<String, Double>> pieceMapping;
  protected int myHeight;
  protected int myWidth;
  protected String bottomColor;

  public Board(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, Pair<String, Double>> pieces){
    myHeight = Integer.parseInt(settings.get(HEIGHT));
    myWidth = Integer.parseInt(settings.get(WIDTH));
    bottomColor = settings.get(BOTTOM_COLOR);
    myGrid = new Piece[myHeight][myWidth];
    pieceMapping = pieces;
    initStartingPieces(locations);
  }

  /**
   Check the board to see if the game has been completed and a winner has been found.
   @return true if there was a winner.
   **/
  public abstract boolean checkWon();

  public void print() {
    for(int i=0; i<myHeight; i++){
      for(int j=0; j<myWidth; j++){
        System.out.print(getPieceAt(i, j) + ", ");
      }
      System.out.println("");
    }
  }

  /**
   Find valid the moves for a piece in the selected cell.
   @param i y-coordinate of the cell.
   @param j x-coordinate of the cell.
   @return potential moves of the piece at cell.
   **/
//  public abstract List<Point2D> exceptionMoves(int i, int j);

  /**
   Get piece at the specified coordinates.
   @return the Piece object at x, y; null if nothing in the cell.
   **/
  public Piece getPieceAt(int i, int j) {
    if (isValidCell(i, j)) {
      return myGrid[i][j];
    } else {
      return null;
    }
  }

  /**
   Execute the desired move
   @param endX new x position
   @param endY new y position
   @return score from completing this move
   **/
  public abstract double doMove(int startX, int startY, int endX, int endY);

  /**
   Set up the board from the config file.
   **/
  private void initStartingPieces(Map<Point2D, String> locs){
    for(Point2D point: locs.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();
      String pieceId = locs.get(point);
      String pieceColor = pieceId.split("_")[0];
      String pieceName = pieceId.split("_")[1];
      Pair<String, Double> pieceInfo = pieceMapping.get(pieceId);
      String movePattern = pieceInfo.getKey();
      double score = pieceInfo.getValue();
      Piece piece = new Piece(pieceName, movePattern, score, pieceColor);
      myGrid[x][y] = piece;
    }
  }

  public void updateCell(int x, int y, Piece piece){
    this.myGrid[x][y] = piece;
  }

  /**
   @param x potential x-coordinate.
   @param y potential y-coordinate.
   @return true if the cell coordinated are within the bounds of the board.
   **/
  public boolean isValidCell(int x, int y){
    return x >= 0 && x < myHeight && y >= 0 && y < myWidth;
  }

  public abstract List<Point2D> getValidMoves(int i, int j);

  public int getHeight() { return myHeight; }
  public int getWidth() { return myWidth; }
}