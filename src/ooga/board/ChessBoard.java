package ooga.board;

import java.awt.geom.Point2D;
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
  public List<String> getValidMoves(int x, int y) {
    return null;
  }

  @Override
  public double doMove(int x, int y, String move) throws NoSuchMethodException {
    return 0;
  }

}
