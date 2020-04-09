package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class ChessBoard extends Board{
  public static final String KING_STRING = "King";
  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
    super(settings, locs, pieces);
  }

  @Override
  public boolean checkWon() {
    System.out.println("Running checkWon");
    double kingI = 0;
    double kingJ = 0;
    for(int i = 0; i < myHeight; i++){
      for(int j = 0; j < myWidth; j++){
        Piece p = getPieceAt(i, j);
        if(p == null){
          continue;
        }
        if(p.toString().equals(KING_STRING)){
          kingI = i;
          kingJ = j;
        }
      }
    }
    System.out.println("kingI = " + kingI);
    System.out.println("kingJ = " + kingJ);
    return false;
  }

  @Override
  public List<Point2D> getValidMoves(int x, int y){
    Piece piece = myGrid[x][y];
    startX = x;
    startY = y;
    if(piece == null){
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0].toLowerCase();
    int moveDist = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class, piece.getClass());
      Object returnVal = methodToCall.invoke(this, x, y, moveDist, piece);
      return ((List<Point2D>)returnVal);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      //e.printStackTrace();
      System.out.println("Error handling method " + moveType);
    }
    return null;
  }

  @Override
  public Pair<Point2D, Double> doMove(int endX, int endY) {
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
    //return score;
    return new Pair<>(new Point2D.Double(startX, startY), score);
  }

  private List<Point2D> lateral(int x, int y, int dist, Piece piece){
    List<Point2D> up = up(x, y, dist, piece);
    List<Point2D> down = down(x, y, dist, piece);
    List<Point2D> left = left(x, y, dist, piece);
    List<Point2D> right = right(x, y, dist, piece);
    List<Point2D> combined = new ArrayList<>(up);
    combined.addAll(down);
    combined.addAll(left);
    combined.addAll(right);
    return combined;
  }
  private List<Point2D> up(int x, int y, int dist, Piece piece){
    System.out.println("up called with distance " + dist);
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x - inc;
      Point2D newPoint = findPoint(newX, y, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(newX, y) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> down(int x, int y, int dist, Piece piece){
    System.out.println("down called with distance " + dist);
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x + inc;
      Point2D newPoint = findPoint(newX, y, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(newX, y) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> right(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y + inc;
      Point2D newPoint = findPoint(x, newY, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(x, newY) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> left(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y - inc;
      Point2D newPoint = findPoint(x, newY, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(x, newY) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private Point2D findPoint(int x, int y, Piece thisPiece){
    Point2D ret;
    if(!isValidCell(x, y)){
      return null;
    }

    Piece thatPiece = getPieceAt(x, y);
    if(thatPiece != null && thisPiece.isOnSameTeam(thatPiece)){
      return null;
    }

    ret = new Point2D.Double(x, y);
    return ret;
  }
}
