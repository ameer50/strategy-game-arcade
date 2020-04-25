package ooga.board;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javafx.util.Pair;
import ooga.history.Move;
import ooga.view.SetUpError;

public class CheckersBoard extends Board implements Serializable {

  private static ResourceBundle moveConstantMap;
  public Map<Point2D, Set<Point2D>> killPaths = new HashMap<>();

  public CheckersBoard(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, String> movePatterns,
      Map<String, Integer> scores) {
    super(settings, locations, movePatterns, scores);
    try {
      moveConstantMap = new PropertyResourceBundle(new FileInputStream("src/properties/checkersMoveConstants.properties"));
    } catch (IOException e) {
      throw new SetUpError("Could not find resource bundle");
    }
  }

  /**
   * Checks the current state of the board to see if a player has won
   * @return
   */
  @Override
  public String checkWon() {
    String result = checkOneColor();
    String result2 = checkTrapped();
    if (result != null) {
      return result;
    } else {
      return result2;
    }
  }

  /**
   * Helper method for CheckWon to see if only one color is remaining on the board
   * @return
   */
  public String checkOneColor() {
    int numRed = 0;
    int numBlack = 0;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (getPieceAt(i, j) != null) {
          if (getPieceAt(i, j).getColor().equals("Red")) {
            numRed++;
          } else if (getPieceAt(i, j).getColor().equals("Black")) {
            numBlack++;
          }
        }
      }
    }
    if (numBlack == 0) {
      return "Red";
    } else if (numRed == 0) {
      return "Black";
    }
    return null;
  }

  /**
   * Helper method to check if a piece has been cornered.
   * @return
   */
  public String checkTrapped() {
    int numRed = 0;
    int numBlack = 0;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (getPieceAt(i, j) != null) {
          List<Point2D> temp = getValidMoves(new Point2D.Double(i, j));
          if (getPieceAt(i, j).getColor().equals("Red")) {
            numRed += temp.size();
          } else if (getPieceAt(i, j).getColor().equals("Black")) {
            numBlack += temp.size();
          }
        }
      }
    }
    if (numBlack == 0) {
      return "Red";
    } else if (numRed == 0) {
      return "Black";
    }
    return null;
  }

  /**
   * Get different move directions based on piece color and position on the board
   * @param moveType
   * @return
   */
  public List<Integer> getMConsts(String moveType) {
    try {
      List<String> rawMoveData = Arrays.asList(moveConstantMap.getString(moveType).split(","));
      List<Integer> moveDataParsed = new ArrayList<Integer>();
      for (int i = 0; i < rawMoveData.size(); i++) {
        moveDataParsed.add(Integer.parseInt(rawMoveData.get(i)));
      }
      return moveDataParsed;
    } catch (Exception e) {
      throw new SetUpError("Error in JSON file");
    }
  }

  /**
   * Gets  all the valid moves for a given piece on the board
   * @param coordinate
   * @return
   */
  @Override
  public List<Point2D> getValidMoves(Point2D coordinate) {
    Piece piece = getPieceAt(coordinate);
    if (piece == null) {
      return new ArrayList<>();
    }
    killPaths.clear();
    String movPat = piece.getMovePattern();
    if (movPat.equals("KING 1")) {
      return new ArrayList<>(king(coordinate));
    } else if ((piece.getColor()).equals(bottomColor)) {
      return new ArrayList<>(p(coordinate, "up"));
    } else if (!(piece.getColor()).equals(bottomColor)) {
      return new ArrayList<>(p(coordinate, "down"));
    }
    return new ArrayList<>();
  }

  /**
   * Executes a given move
   * @param m
   */
  public void doMove(Move m) {
    int x_f = (int) m.getEndLocation().getX();
    Piece currPiece = getPieceAt(m.getStartLocation());
    m.setPiece(currPiece);

    removePieceAt(m.getStartLocation());
    placePieceAt(m.getEndLocation(), currPiece);

    if (killPaths.containsKey(m.getEndLocation())) {
      for (Point2D point : killPaths.get(m.getEndLocation())) {
        if (getPieceAt(point) != null) {
          m.addCapturedPiece(getPieceAt(point), point);
        }
        removePieceAt(point);
      }
    }

    if (currPiece.getType().equals("Coin") && ((currPiece.getColor().equals(bottomColor) && x_f == 0) || (
        !(currPiece.getColor().equals(bottomColor)) && x_f == height - 1))) {
      Piece promotedPiece = new Piece("Monarch", "KING 1", pieceScores.get("King"),
          currPiece.getColor());

      m.addConvertedPiece(new Pair<>(currPiece, promotedPiece), m.getEndLocation());
    }

    for (Point2D location : m.getCapturedPiecesAndLocations().keySet()) {
      if (location != null) {
        removePieceAt(location);
      }
    }
  }

  /**
   * Gets given moves that are classified as nonKill
   * @param coordinate
   * @param moveDirs
   * @return
   */
  private Point2D nonKill(Point2D coordinate, List<Integer> moveDirs) {
    int x = (int) coordinate.getX();
    int y = (int) coordinate.getY();
    if (isCellInBounds(x + moveDirs.get(0), y + moveDirs.get(1)) && (
        getPieceAt(x + moveDirs.get(0), y + moveDirs.get(1)) == null)) {
      return new Point2D.Double(x + moveDirs.get(0), y + moveDirs.get(1));
    }
    return null;
  }

  /**
   * Gets all the moves that a piece can do that are a kill
   * @param coordinate
   * @param currentPath
   * @param moveDirs
   * @return
   */
  private Point2D kill(Point2D coordinate, Set<Point2D> currentPath, List<Integer> moveDirs) {
    int x = (int) coordinate.getX();
    int y = (int) coordinate.getY();
    Piece temp1 = getPieceAt(x + moveDirs.get(0), y + moveDirs.get(1));
    Piece temp2 = getPieceAt(x + moveDirs.get(2), y + moveDirs.get(3));
    boolean killConditions =
        isCellInBounds(x + moveDirs.get(0), y + moveDirs.get(1)) && isCellInBounds(
            x + moveDirs.get(2), y + moveDirs.get(3)) && temp1 != null && temp2 == null
            && isOppColor(getPieceAt(x, y), temp1);
    if (!killConditions) {
      return null;
    } else {
      Point2D ret = new Point2D.Double(x + moveDirs.get(2), y + moveDirs.get(3));
      if (!killPaths.containsKey(ret)) {
        Set<Point2D> killPath = new HashSet<>();
        killPath.addAll(currentPath);
        killPath.add(new Point2D.Double(x + moveDirs.get(0), y + moveDirs.get(1)));
        killPaths.put(ret, killPath);
      } else if (killPaths.containsKey(ret)) {
        killPaths.get(ret).add(new Point2D.Double(x + moveDirs.get(0), y + moveDirs.get(1)));
      }
      return ret;
    }
  }

  private Set<Point2D> p(Point2D coordinate, String uord) {
    Set<Point2D> nonKills = pNoKills(coordinate, uord);
    Set<Point2D> kills = pKills(coordinate, uord);
    kills.addAll(nonKills);
    return kills;
  }

  private Set<Point2D> pNoKills(Point2D coordinate, String uord) {
    Point2D p1 = nonKill(coordinate, getMConsts(uord + "_left"));
    Point2D p2 = nonKill(coordinate, getMConsts(uord + "_right"));
    Set<Point2D> ret = new HashSet<>();
    ret.add(p1);
    ret.add(p2);
    ret.remove(null);
    return ret;
  }

  private Set<Point2D> pKills(Point2D coordinate, String uord) {
    Point2D p3 = kill(coordinate, new HashSet<>(), getMConsts(uord + "_left_kill"));
    Point2D p4 = kill(coordinate, new HashSet<>(), getMConsts(uord + "_right_kill"));
    Set<Point2D> ret = new HashSet<>();
    ret.add(p3);
    ret.add(p4);
    Piece p = getPieceAt(coordinate);
    getNextStepsP(p3, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()), uord);
    getNextStepsP(p4, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()), uord);
    ret.remove(null);
    return ret;
  }

  private void getNextStepsP(Point2D start, Set<Point2D> ret, Piece p, String uord) {
    if (start == null) {
      return;
    }
    ret.add(start);
    putPieceAt(start, p);
    getNextStepsP(kill(start, killPaths.get(start), getMConsts(uord + "_left_kill")), ret, p, uord);
    getNextStepsP(kill(start, killPaths.get(start), getMConsts(uord + "_right_kill")), ret, p, uord);
    removePieceAt(start);
    //putPieceAt(start, null);
  }

  private void getNextStepsKing(Point2D start, Set<Point2D> ret, Piece p) {
    if (start == null) {
      return;
    }
    ret.add(start);
    putPieceAt(start, p);
    getNextStepsP(kill(start, killPaths.get(start), getMConsts("up_left_kill")), ret, p, "up");
    getNextStepsP(kill(start, killPaths.get(start), getMConsts("up_right_kill")), ret, p, "up");
    getNextStepsP(kill(start, killPaths.get(start), getMConsts("down_left_kill")), ret, p, "down");
    getNextStepsP(kill(start, killPaths.get(start), getMConsts("down_right_kill")), ret, p, "down");
    removePieceAt(start);
    //putPieceAt(start, null);
  }

  private Set<Point2D> king(Point2D coordinate) {
    Set<Point2D> kingNoKills = pNoKills(coordinate, "up");
    kingNoKills.addAll(pNoKills(coordinate, "down"));
    Point2D p3 = kill(coordinate, new HashSet<>(), getMConsts("up_left_kill"));
    Point2D p4 = kill(coordinate, new HashSet<>(), getMConsts("up_right_kill"));
    Point2D p5 = kill(coordinate, new HashSet<>(), getMConsts("down_left_kill"));
    Point2D p6 = kill(coordinate, new HashSet<>(), getMConsts("down_right_kill"));
    Set<Point2D> ret = new HashSet<>();
    ret.addAll(kingNoKills);
    ret.add(p3);
    ret.add(p4);
    ret.add(p5);
    ret.add(p6);
    Piece p = getPieceAt(coordinate);
    Point2D[] pArray = {p3, p4, p5, p6};

    for (Point2D point : pArray) {
      getNextStepsKing(point, ret,
          new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
    }
    ret.remove(null);
    return ret;
  }

  private boolean isOppColor(Piece currPiece, Piece oppPiece) {
    if (currPiece == null || oppPiece == null) {
      return true;
    } else {
      return !(oppPiece.getColor().equals(currPiece.getColor()));
    }
  }
  
}
