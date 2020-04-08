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
    for(int y = 0; y < myHeight; y++){
      for(int x = 0; x < myWidth; x++){
        System.out.print(getPieceAt(x, y) + ", ");
      }
      System.out.println("");
    }
  }
  /**
   Find valid moves from selected cell
   @param x x coordinate of the cell
   @param y y coordinate of the cell
   @return potential moves of the piece at cell x,y
   **/
  public abstract List<Point2D> getValidMoves(int x, int y);

  /**
   Get piece at the specified coordinates
   @return piece object at x, y; null if empty cell
   **/
  public Piece getPieceAt(int x, int y){
    if(isValidCell(x, y)){
      return myGrid[y][x];
    }
    else{
      return null;
    }
  }

  /**
   Execute the desired move
   @param startX x position of origin of move
   @param startY y position of origin of move
   @param endX new x position
   @param endY new y position
   @return score from completing this move
   **/
  public abstract double doMove(int startX, int startY, int endX, int endY);

  /**
   Set up board from config file
   **/
  private void initStartingPieces(Map<Point2D, String> locs){
    for(Point2D point: locs.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();
      String pieceName = locs.get(point);
      Pair<String, Double> pieceInfo = pieceMapping.get(pieceName);
      String movePattern = pieceInfo.getKey();
      double score = pieceInfo.getValue();
      Piece piece = new Piece(pieceName, movePattern, score);
      myGrid[y][x] = piece;
    }
  }

  /**
   Check if coordinates are valid on board
   @param x potential x coord
   @param y potential y coord
   @return true if valid coordinate
   **/
  public boolean isValidCell(int x, int y){
    return y >= 0 && y < myHeight && x >= 0 && x < myWidth;
  }
}