package ooga.board;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardTest {
  public static void main(String[] args){
    Map<String, String> settings = new HashMap<>();
    settings.put("height", "5");
    settings.put("width", "5");

    Map<Point2D.Double, String> locs = new HashMap<>();
    Point2D.Double p1 = new Point2D.Double(0, 4);
    Point2D.Double p2 = new Point2D.Double(0, 0);
    locs.put(p1, "rook");
    locs.put(p2, "pawn");

    Map<String, String> pieces = new HashMap<>();
    pieces.put("rook", "up -1");
    pieces.put("pawn", "up 1");
    Board board = new ChessBoard(settings, locs, pieces);

    System.out.println("Starting board config:");
    board.print();
    System.out.println("");
    List<String> moves = board.getValidMoves(0, 0);
    System.out.println("Piece @ (0, 4) has move pattern up 1.");
    System.out.println("Valid moves for (0, 0):");
    for(String move: moves){
      System.out.println(move);
    }
  }

}
