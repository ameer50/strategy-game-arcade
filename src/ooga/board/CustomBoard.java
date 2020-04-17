package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import ooga.history.Move;

public class CustomBoard extends Board {

  public CustomBoard(Map<String, String> settings,
      Map<Point2D, String> locations,
      Map<String, Pair<String, Integer>> pieceTypeMap) {
    super(settings, locations, pieceTypeMap);
  }

  @Override
  public String checkWon() {
    return null;
  }

  @Override
  public void doMove(Move move) {
  }

  @Override
  public List<Point2D> getValidMoves(int i, int j) {
    // TODO: Remove duplication.
    Piece piece = getPieceAt(i, j);
    if (piece == null) return null;

    String pattern = piece.getMovePattern();
    String[] patternArr = pattern.split(" ");
    String moveStr = patternArr[0].toLowerCase();
    List<Integer> moveInts = new ArrayList<>();
    for (int inc=1; inc<patternArr.length; inc++) {
      moveInts.add(Integer.parseInt(patternArr[inc]));
    }
    return generateMoves(i, j, moveStr, moveInts);
  }

  private List<Point2D> generateMoves(int i, int j, String moveStr, List<Integer> moveInts) {

    return null;
  }
}
