package ooga.board;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.util.Pair;

public class ChessBoard extends Board {

  public static final String KING = "King";
  public static final String PAWN = "Pawn";
  public static final String KNIGHT = "Knight";
  public static final String BLACK = "Black";
  public static final String WHITE = "White";

  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
      Pair<String, java.lang.Double>> pieces) {
    super(settings, locations, pieces);
    System.out.println(pieceColorMap);
  }

  @Override
  public List<Point2D> getValidMoves(int i, int j, String color) {
    Piece piece = getPieceAt(i, j);
    if (piece == null) {
      return null;
    }
    if (pieceColorMap.get(color).contains(piece)) {
      String movePattern = piece.getMovePattern();
      String moveType = movePattern.split(" ")[0].toLowerCase();
      int moveDistance = Integer.parseInt(movePattern.split(" ")[1]);
      try {
        Method moveMethod = this.getClass()
            .getDeclaredMethod(moveType, int.class, int.class, int.class,
                piece.getClass());
        Object ret = moveMethod.invoke(this, i, j, moveDistance, piece);
        return (List<Point2D>) ret;
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        System.out.println("Error: " + moveType);
      }
    }
    return null;
  }

  @Override
  public double doMove(int startX, int startY, int endX, int endY) {
    Piece currPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);
    currPiece.move();
    double score = 0;
    if (hitPiece != null) {
      score = hitPiece.getValue();
      removePiece(hitPiece);
      // TODO: In the future, will we do more than just returning the score?
    }
    pieceLocationBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);
    return score;
  }

  private void removePiece(Piece piece) {
    pieceColorMap.get(piece.getColor()).remove(piece);
  }

  @Override
  public boolean checkWon() {
    // a) If not in check return 'null'. If not in check, checkPieces.size() is 0.
    // b) Move king. Does king.validMoves have point not in allPossibleMoves. If yes, return FALSE. If not, keep going.
    // c) If, when the king moves, it kills an opposing piece, we need to make sure that the new square isn't newly
    // accessible to opposing pieces. If we have a safe move return FALSE.
    // d) At this point, if there are multiple checking pieces, return color.
    // e) Kill threatening piece. Is piece.xy in valid moves of our team?
    // f) Knights and Pawns cannot be blocked.
    // g) Block threatening piece. What is the path from threatening to king?
    // If same x and higher y, it is moving upwards. If lower y, moving downwards.
    // If same y and higher x, it is moving left. Otherwise moving right.
    // If different x and y, and the difference between their x and our x = their y and our y, it is diagonal.
    // If diff x and y and those differences aren't the same, it's a knight and we can't block.
    // I need every move the other team can make and every piece holding in check.
    Integer[] coords = locateKings();
    Integer blackKingI = coords[0];
    Integer blackKingJ = coords[1];
    Integer whiteKingI = coords[2];
    Integer whiteKingJ = coords[3];
    if (whiteKingI == null || whiteKingJ == null) {
      System.out.println("NULL");
      return false;
    }
    Pair<List<Point2D>, List<Point2D>> blackMoves = getMovesAndCheckPieces(whiteKingI, whiteKingJ,
        WHITE, true);
    //a) not in check -> false
    List<Point2D> checkPieces = blackMoves.getValue();
    if (checkPieces.size() == 0) {
      System.out.println("NO CHECK");
      return false;
    }
    //b) safe moves
    List<Point2D> opponentMoves = blackMoves.getKey();
    List<Point2D> kingMoves = getValidMoves(whiteKingI, whiteKingJ, "White");
    List<Point2D> safeMoves = getSafeKingMoves(kingMoves, opponentMoves);

    //c) in safe spots, check if there is currently a piece here. if so, check if the spot is newly accessible by opposing team. if so, remove the spot.
    System.out.println("safe spots");
    List<Point2D> hiddenDangerMoves = new ArrayList<>();
    for (Point2D p : safeMoves) {
      int x = (int) p.getX();
      int y = (int) p.getY();
      if (isSpotInDanger(x, y, whiteKingI, whiteKingJ)) {
        hiddenDangerMoves.add(p);
      }
    }

    for (Point2D p : hiddenDangerMoves) {
      safeMoves.remove(p);
    }
    for (Point2D p : safeMoves) {
      System.out.println("p = " + p);
    }
    //king is safe if after all that, there are still safe moves. return false
    if (safeMoves.size() != 0) {
      System.out.println("CHECK BUT SAFE MOVES");
      return false;
    }
    System.out.println("Past safe moves");
    //at this point the king can't move anywhere.
    //d) if there are multiple pieces holding king in check, it's dead
    if (checkPieces.size() > 1) {
      System.out.println("Dead, multiple checkers and no safe moves");
      return true;
    }
    //e) there is only one piece holding the king in check. the king can't escape check. can we kill the piece?
    Pair<List<Point2D>, List<Point2D>> ourMoveData = getMovesAndCheckPieces(whiteKingI, whiteKingJ,
        BLACK, false);
    List<Point2D> ourMoves = ourMoveData.getKey();
    Point2D threatLoc = checkPieces.get(0);
    if (ourMoves.contains(threatLoc)) {
      System.out.println("CAN KILL THREAT");
      return false;
    }
    //f) knights and pawns can't be blocked
    int i = (int) threatLoc.getX();
    int j = (int) threatLoc.getY();
    Piece threat = getPieceAt(i, j);
    if (threat.toString().equals(KNIGHT) || threat.toString().equals(PAWN)) {
      System.out.println("CANT BLOCK KNIGHT OR PAWN, DEAD");
      return true;
    }
    //h) there is one blockable piece threatening king. king can't move. piece can't be killed. can we block the piece?
    //get path
    List<Point2D> path = getPath(i, j, whiteKingI, whiteKingJ);
    for (Point2D p : path) {
      if (ourMoves.contains(p)) {
        System.out.println("CAN BLOCK");
        return false;
      }
    }
    System.out.println("CHECKMATE");
    return true;
  }

  private Integer[] locateKings() {
    Integer blackKingI = null;
    Integer blackKingJ = null;
    Integer whiteKingI = null;
    Integer whiteKingJ = null;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece p = getPieceAt(i, j);
        if (p == null) {
          continue;
        }
        if (p.toString().equals(KING)) {
          if (p.getColor().equals(BLACK)) {
            blackKingI = i;
            blackKingJ = j;
          } else {
            whiteKingI = i;
            whiteKingJ = j;
          }
        }
      }
    }
    Integer[] ret = {blackKingI, blackKingJ, whiteKingI, whiteKingJ};
    return ret;
  }

  private Pair<List<Point2D>, List<Point2D>> getMovesAndCheckPieces(int kingI, int kingJ,
      String targetColor, boolean ignoreTheirKing) {
    List<Point2D> allPossibleMoves = new ArrayList<>();
    List<Point2D> checkPieces = new ArrayList<>();
    Point2D kingPoint = new Point2D.Double(kingI, kingJ);
    Piece storedKing = getPieceAt(kingI, kingJ);
    System.out.println("storedKing = " + storedKing);
    if (ignoreTheirKing) {
      pieceLocationBiMap.forcePut(new Double(kingI, kingJ), null);
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMoves(i, j, "White");
        if ((i == kingI && j == kingJ) || thisPiece == null || thisPiece.getColor()
            .equals(targetColor) || (!ignoreTheirKing && thisPiece.toString().equals(
            KING))) {
          continue;
        }
        if (thisPieceMoves.contains(kingPoint)) {
          checkPieces.add(new Point2D.Double(i, j));
        }
        allPossibleMoves.addAll(thisPieceMoves);
      }
    }
    if (ignoreTheirKing) {
      pieceLocationBiMap.forcePut(new Double(kingI, kingJ), storedKing);
    }
    if (allPossibleMoves.size() == 0 && checkPieces.size() == 0) {
      return null;
    }
    Pair<List<Point2D>, List<Point2D>> ret = new Pair<>(allPossibleMoves, checkPieces);
    return ret;
  }

  private List<Point2D> getSafeKingMoves(List<Point2D> kingMoves, List<Point2D> oppMoves) {
    List<Point2D> safePoints = new ArrayList<>();
    for (Point2D kingMove : kingMoves) {
      if (!oppMoves.contains(kingMove)) {
        safePoints.add(kingMove);
      }
    }
    return safePoints;
  }

  // Used to see if killing a piece could keep king in check.
  // Ignore current position of king.
  private boolean isSpotInDanger(int potentialI, int potentialJ, int kingI, int kingJ) {
    Point2D potentialPoint = new Point2D.Double(potentialI, potentialJ);
    Piece storedPiece = getPieceAt(potentialI, potentialJ);
    Piece storedKing = getPieceAt(kingI, kingJ);
    if (storedPiece == null) {
      return false;
    }
    pieceLocationBiMap.forcePut(new Double(kingI, kingJ), null);
    pieceLocationBiMap.forcePut(new Double(potentialI, potentialJ), null);
    System.out.println("Potentials: " + potentialI + ", " + potentialJ);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMoves(i, j, thisPiece.getColor());
        if ((i == potentialI && j == potentialJ) || thisPiece == null || !storedPiece.getColor()
            .equals(thisPiece.getColor())) {
          continue;
        }
        if (thisPieceMoves.contains(potentialPoint)) {
          pieceLocationBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
          pieceLocationBiMap.forcePut(new Double(kingI, kingJ), storedKing);
          System.out.println(thisPiece + " at " + i + ", " + j);
          return true;
        }
      }
    }
    pieceLocationBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
    pieceLocationBiMap.forcePut(new Double(kingI, kingJ), storedKing);
    return false;
  }

  private List<Point2D> getPath(int threatI, int threatJ, int kingI, int kingJ) {
    List<Point2D> path = new ArrayList<>();
    //lateral movement in same row
    if (threatI == kingI) {
      if (threatJ < kingJ) {
        for (int j = threatJ; j < kingJ; j++) {
          Point2D pointOnPath = new Point2D.Double(threatI, j);
          path.add(pointOnPath);
        }
        return path;
      }
      if (threatJ > kingJ) {
        for (int j = kingJ; j < threatJ; j++) {
          Point2D pointOnPath = new Point2D.Double(threatI, j);
          path.add(pointOnPath);
        }
        return path;
      }
    }
    //lateral movement in same column
    if (threatJ == kingJ) {
      if (threatI < kingI) {
        for (int i = threatI; i < kingI; i++) {
          Point2D pointOnPath = new Point2D.Double(i, threatJ);
          path.add(pointOnPath);
        }
        return path;
      }
      if (threatI > kingI) {
        for (int i = kingI; i < threatI; i++) {
          Point2D pointOnPath = new Point2D.Double(i, threatJ);
          path.add(pointOnPath);
        }
        return path;
      }
    }
    //add diagonals
    return null;
  }

  private List<Point2D> lateral(int x, int y, int dist, Piece piece) {
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

  private List<Point2D> pawn(int i, int j, int dist, Piece piece) {
    List<Point2D> ret = new ArrayList<>();
    int inc;
    if (piece.getColor().equals(bottomColor)) {
      inc = -1;
    } else {
      inc = 1;
    }
    int[] diagJ = {-1, 1};
    int newI = i + inc;
    for (int jInc : diagJ) {
      int potJ = j + jInc;
      Point2D newPoint = checkPoint(newI, potJ, piece);
      if (newPoint != null && getPieceAt(newI, potJ) != null) {
        ret.add(newPoint);
      }
    }
    Point2D newPoint = checkPoint(newI, j, piece);
    if (getPieceAt(newI, j) == null && newPoint != null) {
      ret.add(newPoint);
      if (!piece.hasMoved()) {
        newI += inc;
        newPoint = checkPoint(newI, j, piece);
        if (getPieceAt(newI, j) == null && newPoint != null) {
          ret.add(newPoint);
        }
      }
    }
    return ret;
  }

    // FIXME: these have a ton of duplication; could be made into much simpler methods
    private List<Point2D> up (int x, int y, int distance, Piece piece) {
      List<Point2D> ret = new ArrayList<>();
      int squares = 1;
      while (squares <= distance || distance < 0) {
        int newX = x - squares;
        Point2D newPoint = checkPoint(newX, y, piece); // ***
        if (newPoint != null) {
          ret.add(newPoint);
          if (getPieceAt(newX, y) != null) {
            break;
          }
        } else {
          break;
        }
        squares++;
      }
      return ret;
    }

    private List<Point2D> down ( int x, int y, int distance, Piece piece) {
      List<Point2D> ret = new ArrayList<>();
      int squares = 1;
      while (squares <= distance || distance < 0) {
        int newX = x + squares;
        Point2D newPoint = checkPoint(newX, y, piece); //***
        if (newPoint != null) {
          ret.add(newPoint);
          if (getPieceAt(newX, y) != null) {
            break;
          }
        } else {
          break;
        }
        squares++;
      }
      return ret;
    }

    private List<Point2D> right (int x, int y, int distance, Piece piece) {
      List<Point2D> ret = new ArrayList<>();
      int squares = 1;
      while (squares <= distance || distance < 0) {
        int newY = y + squares;
        Point2D newPoint = checkPoint(x, newY, piece);
        if (newPoint != null) {
          ret.add(newPoint);
          if (getPieceAt(x, newY) != null) {
            break;
          }
        } else {
          break;
        }
        squares++;
      }
      return ret;
    }

  private List<Point2D> left(int x, int y, int distance, Piece piece) {
    List<Point2D> ret = new ArrayList<>();
    int squares = 1;
    while (squares <= distance || distance < 0) {
      int newY = y - squares;
      Point2D newPoint = checkPoint(x, newY, piece);
      if (newPoint != null) {
        ret.add(newPoint);
        if (getPieceAt(x, newY) != null) {
          break;
        }
      } else {
        break;
      }
      squares++;
    }
    return ret;
  }


  private Point2D checkPoint (int x, int y, Piece thisPiece) {
      Point2D ret;
    System.out.println("x + \",\" + y = " + x + "," + y);
      if (!isCellInBounds(x, y)) {
        return null;
      }
      Piece thatPiece = getPieceAt(x, y);
      if (thatPiece != null && thisPiece.isOnSameTeam(thatPiece)) {
        return null;
      }
      ret = new Point2D.Double(x, y);
      return ret;
    }
}
