package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChessBoard extends Board{
  public ChessBoard(Map<String, String> settings, Map<Point2D.Double, String> locs, Map<String, String> pieces){
    super(settings, locs, pieces);
  }

  @Override
  public boolean checkWon() {
    return false;
  }

  @Override
  public List<String> getValidMoves(int x, int y){
    Piece piece = myGrid[y][x];
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
    Piece piece = getPieceAt(startX, startY);
    if(piece == null){
      return 0;
    }
    return -1;
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
