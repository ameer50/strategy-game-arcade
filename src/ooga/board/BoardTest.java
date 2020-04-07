package ooga.board;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class BoardTest {
  public static void main(String[] args){
    Map<String, String> settings = new HashMap<>();
    settings.put("height", "5");
    settings.put("width", "5");

    Map<Point2D.Double, String> locs = new HashMap<>();
    Point2D.Double p1 = new Point2D.Double(0, 0);
    locs.put(p1, "up 5");
    Board board = new ChessBoard(settings, locs);
    board.print();
  }

}
