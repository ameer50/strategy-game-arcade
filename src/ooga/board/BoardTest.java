package ooga.board;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

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

    Map<String, Pair<String, Double>> pieces = new HashMap<>();
    pieces.put("rook", new Pair<String, Double>("up -1", 5.0));
    pieces.put("pawn", new Pair<String, Double>("up 1", 1.0));
    Board board = new ChessBoard(settings, locs, pieces);

    System.out.println("Starting board config:");
    board.print();
    System.out.println("");


    System.out.println("Moving the rook @(0, 4) to (3, 4)");
    double score = board.doMove(0, 4, 3, 4);
    board.print();
    System.out.println("Scored " + score + " points");
    System.out.println("");

    List<String> moves = board.getValidMoves(3, 4);
    System.out.println("Rook @(3, 4) has move pattern up -1.");
    System.out.println("Valid moves for (3, 4):");
    for(String move: moves){
      System.out.println(move);
    }
    System.out.println("");

    System.out.println("Moving the rook @(3, 4) to (0, 0), killing the pawn (obv illegal move)");
    score = board.doMove(3, 4, 0, 0);
    board.print();
    System.out.println("Scored " + score + " points");
    System.out.println("");

    System.out.println("Moving the rook @(0, 0) to (0, 4) to verify valid moves still works after killing piece");
    score = board.doMove(0, 0, 0, 4);
    board.print();
    System.out.println("");

    for(String move: moves){
      System.out.println(move);
    }
  }

}
