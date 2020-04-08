package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class ChessBoard extends Board{
  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
    super(settings, locs, pieces);
  }

  @Override
  public boolean checkWon() {
    return false;
  }

  @Override
  public List<Point2D> getValidMoves(int x, int y){
    Piece piece = myGrid[x][y];
    if(piece == null){
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0].toLowerCase();
    int moveDist = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class);
      Object returnVal = methodToCall.invoke(this, x, y, moveDist);
      return ((List<Point2D>)returnVal);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      System.out.println("Error handling method " + moveType);
    }
    return null;
  }

  @Override
  public double doMove(int startX, int startY, int endX, int endY) {
    Piece thisPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);
    double score = 0;
    if(hitPiece == null){
      score = 0;
    }
    else{
      score = hitPiece.getValue();
    }

    myGrid[startX][startY] = null;
    myGrid[endX][endY] = thisPiece;
    return score;
  }


  private List<Point2D> up(int x, int y, int dist){
    System.out.println("up called with distance " + dist);
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x - inc;
      Point2D newPoint = findPoint(newX, y);
      if(newPoint != null) {
        ret.add(newPoint);
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> down(int x, int y, int dist){
    System.out.println("down called with distance " + dist);
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x + inc;
      Point2D newPoint = findPoint(newX, y);
      if(newPoint != null) {
        ret.add(newPoint);
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> right(int x, int y, int dist){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y + inc;
      Point2D newPoint = findPoint(x, newY);
      if(newPoint != null) {
        ret.add(newPoint);
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }
  private Point2D findPoint(int x, int y){
    Point2D ret;
    if(!isValidCell(x, y)){
      return null;
    }
    if(getPieceAt(x, y) != null){
      return null;
    }
    ret = new Point2D.Double(x, y);
    return ret;
  }
}
