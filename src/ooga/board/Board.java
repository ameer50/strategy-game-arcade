package ooga.board;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public abstract class Board{
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  protected Piece[][] myGrid;
  protected Map<String, Pair<String, Double>> pieceMapping;
  protected int myHeight;
  protected int myWidth;
  protected int startX;
  protected int startY;


  public Board(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
    myHeight = Integer.parseInt(settings.get(HEIGHT));
    myWidth = Integer.parseInt(settings.get(WIDTH));
    myGrid = new Piece[myHeight][myWidth];
    pieceMapping = pieces;
    initStartingPieces(locs);
  }
  /**
   Check board to see if the game has been completed and a winner has been found
   @return if there is a winner
   **/
  public abstract boolean checkWon();

  public void print(){
    for(int i = 0; i < myHeight; i++){
      for(int j = 0; j < myWidth; j++){
        System.out.print(getPieceAt(i, j) + ", ");
      }
      System.out.println("");
    }
  }
  /**
   Find valid moves from selected cell
   @param i y coordinate of the cell
   @param j x coordinate of the cell
   @return potential moves of the piece at cell
   **/
  public abstract List<Point2D> getValidMoves(int i, int j);

  /**
   Get piece at the specified coordinates
   @return piece object at x, y; null if empty cell
   **/
  public Piece getPieceAt(int i, int j){
    if(isValidCell(i, j)){
      return myGrid[i][j];
    }
    else{
      return null;
    }
  }

  /**
   Execute the desired move
   @param endX new x position
   @param endY new y position
   @return score from completing this move
   **/
  public abstract Pair<Point2D, Double> doMove( int endX, int endY);

  /**
   Set up board from config file
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

  /**
   Check if coordinates are valid on board
   @param x potential x coord
   @param y potential y coord
   @return true if valid coordinate
   **/
  public boolean isValidCell(int x, int y){
    return x >= 0 && x < myHeight && y >= 0 && y < myWidth;
  }
}