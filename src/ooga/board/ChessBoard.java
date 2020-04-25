package ooga.board;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javafx.util.Pair;
import ooga.exceptions.ReflectionException;
import ooga.exceptions.ResourceBundleException;
import ooga.history.Move;
import ooga.view.DisplayError;

public class ChessBoard extends Board implements Serializable {

  public static final String KING = "King";
  public static final String PAWN = "Pawn";
  public static final String KNIGHT = "Knight";
  public static final String RESOURCE_BUNDLE_EXCEPTION = "ResourceBundleException";
  public static final String ERROR_MSG = "Could not create User Input resource bundles";
  public static final String MOVES_DIR = "src/properties/chessMoveConstants.properties";
  public static final String ISHIFTS = "IShifts";
  public static final String JSHIFTS = "JShifts";
  public static final String MOVE_SPLIT = ", ";
  public static final String REFLECTION_EXCEPTION = "ReflectionException";
  public static final String REFLECTION_ERR_MSG = "Could not find defined method";
  private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
  private static ResourceBundle moveConstantMap;
  public static final String WHITE = res.getString("ChessColor1");
  public static final String BLACK = res.getString("ChessColor2");
  public static final String QUEEN = "Queen";
  public static final String QUEEN_MOVE_PATTERN = "Any -1";

  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String, String> movePatterns,
      Map<String, Integer> scores) {
    super(settings, locations, movePatterns, scores);
    try {
      moveConstantMap = new PropertyResourceBundle(new FileInputStream(
          MOVES_DIR));
    } catch (IOException e) {
      new DisplayError(RESOURCE_BUNDLE_EXCEPTION);
      throw new ResourceBundleException(ERROR_MSG);
    }
  }

  @Override
  public List<Point2D> getValidMoves(Point2D coord) {
    Piece piece = getPieceAt(coord);
    if (piece == null) {
      return new ArrayList<>();
    }
    List<Point2D> thisPieceValidMoves = getValidMovesIgnoreCheck(coord);
    String color = piece.getColor();
    Point2D kingPoint = locateKings(color);

    Pair<List<Point2D>, List<Point2D>> checks = getMovesAndCheckPieces(kingPoint, color, true);
    List<Point2D> checkPieces = checks.getValue();
    if (piece.getType().equals(KING)) {
      List<Point2D> safeKings = getSafeKingMoves(thisPieceValidMoves, checks.getKey());
      return checkDanger(safeKings, kingPoint);
    }
    return filterCheckMovesFromValid(coord, kingPoint, checkPieces, thisPieceValidMoves);
  }

  public List<Point2D> getValidMovesIgnoreCheck(Point2D coord) {
    Piece piece = getPieceAt(coord);
    if (piece == null) {
      return new ArrayList<>();
    }
    String movePattern = piece.getMovePattern();
    String[] movePatternSplit = movePattern.split(" ");
    String moveType = movePatternSplit[0].toLowerCase();
    List<Integer> params = new ArrayList<>();
    for (int inc = 1; inc < movePatternSplit.length; inc++) {
      params.add(Integer.parseInt(movePatternSplit[inc]));
    }
    try {
      return getMovesFromShift(coord, moveType, params, piece);
    } catch (MissingResourceException e) {
      return getMovesFromMethodName(coord, moveType, params, piece);
    }
  }

  private List<Point2D> getMovesFromShift(Point2D coord, String moveType, List<Integer> params,
      Piece piece) throws MissingResourceException {
    List<String> iShift = Arrays.asList(moveConstantMap.getString(moveType + ISHIFTS).split(MOVE_SPLIT));
    List<String> jShift = Arrays.asList(moveConstantMap.getString(moveType + JSHIFTS).split(
        MOVE_SPLIT));

    return move(coord, iShift, jShift, params, piece);
  }

  private List<Point2D> getMovesFromMethodName(Point2D coord, String moveType, List<Integer> params,
      Piece piece) {
    try {
      Method moveMethod = this.getClass()
          .getDeclaredMethod(moveType, Point2D.class, List.class,
              piece.getClass());
      Object ret = moveMethod.invoke(this, coord, params, piece);
      return (List<Point2D>) ret;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException g) {
      new DisplayError(REFLECTION_EXCEPTION);
      throw new ReflectionException(REFLECTION_ERR_MSG);
    }
  }

  private List<Point2D> filterCheckMovesFromValid(Point2D coords, Point2D kingPoint,
      List<Point2D> checkPieces, List<Point2D> thisPieceValidMoves) {
    List<Point2D> blockingPath = getBlockingMoves(coords, kingPoint, checkPieces);
    if (blockingPath.size() != 0) {
      thisPieceValidMoves.retainAll(blockingPath);
    }

    if (checkPieces.size() == 0) {
      return thisPieceValidMoves;
    }

    if (checkPieces.size() > 1) {
      return new ArrayList<>();
    }

    Point2D threatLoc = checkPieces.get(0);
    List<Point2D> threatPath = getPath(threatLoc, kingPoint);
    threatPath.add(threatLoc);
    thisPieceValidMoves.retainAll(threatPath);

    return thisPieceValidMoves;
  }

  @Override
  public void doMove(Move m) {
    Piece currPiece = getPieceAt(m.getStartLocation());
    Piece hitPiece = getPieceAt(m.getEndLocation());
    currPiece.incrementMoveCount(m.isUndo());

    m.setPiece(currPiece);
    pieceBiMap.forcePut(m.getEndLocation(), currPiece);

    if (hitPiece != null) {
      m.addCapturedPiece(hitPiece, m.getEndLocation());
      pieceBiMap.remove(hitPiece);
    }
    promote(m);
  }

  private void promote(Move m) {
    Piece piece = m.getPiece();
    if (!piece.getType().equals(PAWN)) {
      return;
    }
    int inc = getPawnInc(piece);
    int endX = (int) m.getEndLocation().getX();
    if ((inc == -1 && endX == 0) || (inc == 1 && endX == height - 1)) {
      Piece promotedPiece = new Piece(QUEEN, QUEEN_MOVE_PATTERN, pieceScores.get(QUEEN), piece.getColor());
      m.addConvertedPiece(new Pair<>(piece, promotedPiece), m.getEndLocation());
    }
  }

  private List<Point2D> getBlockingMoves(Point2D blockPoint, Point2D kingPoint,
      List<Point2D> checkPieces) {
    Piece blocker = getPieceAt(blockPoint);
    String blockColor = blocker.getColor();
    pieceBiMap.forcePut(blockPoint, null);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece threat = getPieceAt(i, j);
        Point2D threatPoint = new Point2D.Double(i, j);
        if (threat == null || threat.getColor().equals(blockColor) || checkPieces
            .contains(threatPoint) || !getValidMovesIgnoreCheck(threatPoint).contains(kingPoint)) {
          continue;
        }
        List<Point2D> path = getPath(threatPoint, kingPoint);
        path.add(threatPoint);
        if (path.contains(blockPoint)) {
          pieceBiMap.forcePut(blockPoint, blocker);
          return path;
        }
      }
    }
    pieceBiMap.forcePut(blockPoint, blocker);
    return new ArrayList<>();
  }

  @Override
  public String checkWon() {
    if (getCheckmate(WHITE, BLACK)) {
      return BLACK;
    }
    if (getCheckmate(BLACK, WHITE)) {
      return WHITE;
    }
    return null;
  }

  private Point2D locateKings(String color) {
    Integer iCoord = null;
    Integer jCoord = null;

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece p = getPieceAt(i, j);
        if (p == null) {
          continue;
        }
        if (p.getType().equals(KING) && p.getColor().equals(color)) {
            iCoord = i;
            jCoord = j;
        }
      }
    }
    if (iCoord == null) {
      return null;
    }
    return new Point2D.Double(iCoord, jCoord);
  }

  private boolean getCheckmate(String ourColor, String opponentColor) {
    Point2D kingPoint = locateKings(ourColor);
    if (kingPoint == null) {
      return true;
    }
    Pair<List<Point2D>, List<Point2D>> theirMoves = getMovesAndCheckPieces(kingPoint, ourColor,
        true);
    List<Point2D> opponentMoves = theirMoves.getKey();
    List<Point2D> checkPieces = theirMoves.getValue();

    if (checkPieces.size() == 0) {
      return false;
    }

    List<Point2D> kingMoves = getValidMovesIgnoreCheck(kingPoint);
    List<Point2D> safeMoves = getSafeKingMoves(kingMoves, opponentMoves);

    safeMoves = checkDanger(safeMoves, kingPoint);
    return ((safeMoves.size() == 0) && (checkPieces.size() > 1 || !canKillOrBlock(kingPoint,
        opponentColor, checkPieces.get(0))));
  }

  private List<Point2D> checkDanger(List<Point2D> safeMoves, Point2D kingPoint) {
    List<Point2D> hiddenDangerMoves = new ArrayList<>();
    for (Point2D p : safeMoves) {
      int x = (int) p.getX();
      int y = (int) p.getY();
      if (isSpotInDanger(x, y, kingPoint)) {
        hiddenDangerMoves.add(p);
      }
    }
    for (Point2D p : hiddenDangerMoves) {
      safeMoves.remove(p);
    }
    return safeMoves;
  }

  private boolean canKillOrBlock(Point2D kingPoint, String opponentColor, Point2D threatLoc) {
    Pair<List<Point2D>, List<Point2D>> ourMoveData = getMovesAndCheckPieces(kingPoint,
        opponentColor, false);
    List<Point2D> ourMoves = ourMoveData.getKey();
    return ourMoves.contains(threatLoc) || canBlock(threatLoc, kingPoint, ourMoves);
  }

  private boolean canBlock(Point2D threatLoc, Point2D kingPoint, List<Point2D> ourMoves) {
    Piece threat = getPieceAt(threatLoc);
    if (threat.getType().equals(KNIGHT) || threat.getType().equals(PAWN)) {
      return false;
    }

    List<Point2D> path = getPath(threatLoc, kingPoint);
    for (Point2D p : path) {
      if (ourMoves.contains(p)) {
        return true;
      }
    }
    return false;
  }

  private Pair<List<Point2D>, List<Point2D>> getMovesAndCheckPieces(Point2D kingPoint,
      String targetColor, boolean ignoreTheirKing) {
    List<Point2D> allPossibleMoves = new ArrayList<>();
    List<Point2D> checkPieces = new ArrayList<>();
    List<Point2D> pawnList = new ArrayList<>();
    Piece storedKing = getPieceAt(kingPoint);

    if (ignoreTheirKing) {
      pieceBiMap.forcePut(kingPoint, null);
    }
    updatePawnAndMoveLists(pawnList, checkPieces, allPossibleMoves, kingPoint, targetColor,
        ignoreTheirKing);
    if (ignoreTheirKing) {
      pieceBiMap.forcePut(kingPoint, storedKing);
    }

    checkPossiblePawnMoves(pawnList, checkPieces, allPossibleMoves, kingPoint, ignoreTheirKing);

    return new Pair<>(allPossibleMoves, checkPieces);
  }

  private void updatePawnAndMoveLists(List<Point2D> pawnList, List<Point2D> checkPieces,
      List<Point2D> allPossibleMoves, Point2D kingPoint, String targetColor,
      boolean ignoreTheirKing) {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Point2D thisPoint = new Point2D.Double(i, j);
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMovesIgnoreCheck(thisPoint);
        if (thisPoint.equals(kingPoint) || thisPiece == null || thisPiece.getColor()
            .equals(targetColor) || (!ignoreTheirKing && thisPiece.getType().equals(
            KING))) {
          continue;
        }
        if (thisPiece.getType().equals(PAWN)) {
          pawnList.add(thisPoint);
        } else {
          if (thisPieceMoves.contains(kingPoint)) {
            checkPieces.add(thisPoint);
          }
          allPossibleMoves.addAll(thisPieceMoves);
        }
      }
    }
  }

  private void checkPossiblePawnMoves(List<Point2D> pawnList, List<Point2D> checkPieces,
      List<Point2D> allPossibleMoves, Point2D kingPoint, boolean ignoreTheirKing) {
    for (Point2D pawn : pawnList) {
      int i = (int) pawn.getX();
      int j = (int) pawn.getY();
      Piece piece = getPieceAt(i, j);
      int inc = getPawnInc(piece);
      int newI = i + inc;
      List<Point2D> thisPieceMoves = getPawnDiags(newI, j, piece, true);
      if (!ignoreTheirKing) {
        thisPieceMoves.addAll(getPawnStraights(newI, j, piece, inc));
      }
      if (thisPieceMoves.contains(kingPoint)) {
        checkPieces.add(new Point2D.Double(i, j));
      }
      allPossibleMoves.addAll(thisPieceMoves);
    }
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

  private boolean isSpotInDanger(int potentialI, int potentialJ, Point2D kingPoint) {
    Point2D potentialPoint = new Point2D.Double(potentialI, potentialJ);
    Piece storedPiece = getPieceAt(potentialI, potentialJ);
    Piece storedKing = getPieceAt(kingPoint);
    String color = storedKing.getColor();

    pieceBiMap.forcePut(kingPoint, null);
    pieceBiMap.forcePut(new Double(potentialI, potentialJ), null);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece thisPiece = getPieceAt(i, j);
        if (thisPiece == null) {
          continue;
        }
        List<Point2D> thisPieceMoves;
        boolean pawn = thisPiece.getType().equals(PAWN);
        if (pawn) {
          int inc = getPawnInc(thisPiece);
          thisPieceMoves = getPawnDiags(i + inc, j, thisPiece, false);
        } else {
          thisPieceMoves = getValidMovesIgnoreCheck(new Point2D.Double(i, j));
        }
        if ((i == potentialI && j == potentialJ) || color
            .equals(thisPiece.getColor())) {
          continue;
        }
        if (thisPieceMoves.contains(potentialPoint)) {
          pieceBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
          pieceBiMap.forcePut(kingPoint, storedKing);
          return true;
        }
      }
    }
    pieceBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
    pieceBiMap.forcePut(kingPoint, storedKing);
    return false;
  }

  private List<Point2D> getPath(Point2D threatLoc, Point2D kingLoc) {
    int threatI = (int) threatLoc.getX();
    int threatJ = (int) threatLoc.getY();
    int kingI = (int) kingLoc.getX();
    int kingJ = (int) kingLoc.getY();
    if (threatI == kingI) {
      return getPathSameRowRook(threatI, threatJ, kingJ);
    }
    if (threatJ == kingJ) {
      return getPathSameColRook(threatJ, threatI, kingI);
    }
    if (isDiagonal(threatI, threatJ, kingI, kingJ)) {
      return getPathDiagonal(threatI, threatJ, kingI, kingJ);
    }
    return new ArrayList<>();
  }

  private List<Point2D> getPathSameRowRook(int i, int threatJ, int kingJ) {
    List<Point2D> path = new ArrayList<>();

    int greaterJ = Math.max(threatJ, kingJ);
    int smallerJ = Math.min(threatJ, kingJ);

    for (int j = smallerJ + 1; j < greaterJ; j++) {
      Point2D pointOnPath = new Point2D.Double(i, j);
      path.add(pointOnPath);
    }
    return path;
  }

  private List<Point2D> getPathSameColRook(int j, int threatI, int kingI) {
    List<Point2D> path = new ArrayList<>();

    int greaterI = Math.max(threatI, kingI);
    int smallerI = Math.min(threatI, kingI);

    for (int i = smallerI + 1; i < greaterI; i++) {
      Point2D pointOnPath = new Point2D.Double(i, j);
      path.add(pointOnPath);
    }
    return path;
  }

  private List<Point2D> getPathDiagonal(int threatI, int threatJ, int kingI, int kingJ) {
    List<Point2D> path = new ArrayList<>();
    int greaterI = Math.max(threatI, kingI);
    int smallerI = Math.min(threatI, kingI);
    int smallerJ = Math.min(threatJ, kingJ);
    int greaterJ = Math.max(threatJ, kingJ);

    for (int inc = 1; inc < greaterI - smallerI; inc++) {
      Point2D pointOnPath;
      if ((threatI < kingI && threatJ > kingJ) || (kingI < threatI && kingJ > threatJ)) {
        pointOnPath = new Point2D.Double(smallerI + inc, greaterJ - inc);
      } else {
        pointOnPath = new Point2D.Double(smallerI + inc, smallerJ + inc);
      }
      path.add(pointOnPath);
    }
    return path;
  }

  private boolean isDiagonal(int threatI, int threatJ, int kingI, int kingJ) {
    return Math.abs(kingJ - threatJ) == Math.abs(kingI - threatI);
  }

  private List<Point2D> knight(Point2D coord, List<Integer> params, Piece piece) {
    List<Point2D> ret = new ArrayList<>();
    int first = params.get(0);
    int second = params.get(1);
    int[] iShifts = {first, first, -first, -first, second, second, -second, -second};
    int[] jShifts = {second, -second, second, -second, first, -first, first, -first};
    int i = (int) coord.getX();
    int j = (int) coord.getY();
    for (int idx = 0; idx < iShifts.length; idx++) {
      int newI = i + iShifts[idx];
      int newJ = j + jShifts[idx];
      Point2D newPoint = getPointIfValid(newI, newJ, piece);
      if (newPoint != null) {
        ret.add(newPoint);
      }
    }
    return ret;
  }

  private List<Point2D> pawn(Point2D coord, List<Integer> params, Piece piece) {
    int i = (int) coord.getX();
    int j = (int) coord.getY();
    List<Point2D> ret = new ArrayList<>();
    int inc = getPawnInc(piece);
    int newI = i + inc;
    ret.addAll(getPawnDiags(newI, j, piece, true));
    ret.addAll(getPawnStraights(newI, j, piece, inc));
    return ret;
  }

  private int getPawnInc(Piece piece) {
    int inc;
    if (piece.getColor().equals(bottomColor)) {
      inc = -1;
    } else {
      inc = 1;
    }
    return inc;
  }

  private List<Point2D> getPawnDiags(int newI, int j, Piece piece, boolean check) {
    List<Point2D> ret = new ArrayList<>();
    int[] diagJ = {-1, 1};
    for (int jInc : diagJ) {
      int potJ = j + jInc;
      Point2D newPoint = getPointIfValid(newI, potJ, piece);
      if (newPoint != null && (!check || getPieceAt(newI, potJ) != null)) {
        ret.add(newPoint);
      }
    }
    return ret;
  }

  private List<Point2D> getPawnStraights(int newI, int j, Piece piece, int inc) {
    List<Point2D> ret = new ArrayList<>();
    Point2D newPoint = getPointIfValid(newI, j, piece);
    if (getPieceAt(newI, j) == null && newPoint != null) {
      ret.add(newPoint);
      if (!piece.hasMoved()) {
        newI += inc;
        newPoint = getPointIfValid(newI, j, piece);
        if (getPieceAt(newI, j) == null && newPoint != null) {
          ret.add(newPoint);
        }
      }
    }
    return ret;
  }

  private List<Point2D> move(Point2D coords, List<String> iShifts, List<String> jShifts, List<Integer> params,
      Piece piece) {
    List<Point2D> ret = new ArrayList<>();
    int i = (int) coords.getX();
    int j = (int) coords.getY();
    int distance = params.get(0);
    for (int shift = 0; shift < iShifts.size(); shift++) {
      int inc = 1;
      while (inc <= distance || distance < 0) {
        int newI = i + Integer.parseInt(iShifts.get(shift)) * inc;
        int newJ = j + Integer.parseInt(jShifts.get(shift)) * inc;
        Point2D newPoint = getPointIfValid(newI, newJ, piece);
        if (newPoint != null) {
          ret.add(newPoint);
          if (getPieceAt(newI, newJ) != null) {
            break;
          }
        } else {
          break;
        }
        inc++;
      }
    }
    return ret;
  }

  private Point2D getPointIfValid(int x, int y, Piece thisPiece) {
    if (!isCellInBounds(x, y)) {
      return null;
    }
    Piece thatPiece = getPieceAt(x, y);
    if (thatPiece != null && thisPiece.isSameColor(thatPiece)) {
      return null;
    }
    return new Point2D.Double(x, y);
  }
}