package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ChessBoard extends Board{
  public ChessBoard(Map<String, String> settings, Map<Point2D.Double, String> locs){
    super(settings, locs);
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
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class);
      methodToCall.invoke(this, moveDist);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      //System.out.println("Error handling method " + moveType);
    }
    return null;
  }

  @Override
  public double doMove(int x, int y, String move){
    return 0;
  }

  private void up(int dist){
    System.out.println("up called with distance " + dist);
  }
}
