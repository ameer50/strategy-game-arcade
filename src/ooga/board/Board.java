package ooga.board;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public abstract class Board{
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  private Piece[][] myGrid;
  private int myHeight;
  private int myWidth;


  public Board(Map<String, String> settings, Map<Point2D.Double, String> locs){
    myHeight = Integer.parseInt(settings.get(HEIGHT));
    myWidth = Integer.parseInt(settings.get(WIDTH));
    myGrid = new Piece[myHeight][myWidth];
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
  public abstract List<String> getValidMoves(int x, int y);

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
   @param x x position of origin of move
   @param y y position of origin of move
   @param move move to be executed through reflection
   @throws NoSuchMethodException if unknown move type is requested
   @return score from completing this move
   **/
  public abstract double doMove(int x, int y, String move) throws NoSuchMethodException;

  /**
   Set up board from config file
   **/
  private void initStartingPieces(Map<Point2D.Double, String> locs){
    for(Point2D.Double point: locs.keySet()){
      int x = (int) point.getX();
      int y = (int) point.getY();
      String movePattern = locs.get(point);
      Piece piece = new Piece(movePattern);
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