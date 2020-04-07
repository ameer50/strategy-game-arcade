package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class ChessBoard extends Board{
  public ChessBoard(Map<String, String> settings, Map<Point2D.Double, String> locs, Map<String, Pair<String, Double>> pieces){
    super(settings, locs, pieces);
  }

  @Override
  public boolean checkWon() {
    return false;
  }

  @Override
  public List<String> getValidMoves(int x, int y){
    Piece piece = myGrid[y][x];
    if(piece == null){
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0];
    int moveDist = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class);
      Object returnVal = methodToCall.invoke(this, x, y, moveDist);
      return ((List<String>)returnVal);
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

    myGrid[startY][startX] = null;
    myGrid[endY][endX] = thisPiece;
    return score;
  }


  private List<String> up(int x, int y, int dist){
    System.out.println("up called with distance " + dist);
    List<String> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y - inc;
      if(!isValidCell(x, newY)){
        return ret;
      }
      if(getPieceAt(x, newY) != null){
        return ret;
      }
      String add = "(" + x + ", " + newY + ")";
      ret.add(add);
      inc++;
    }
    return ret;
  }
}
