package ooga.board;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import ooga.history.Move;

public class ChessBoard extends Board implements Serializable {

  public static final String KING = "King";
  public static final String PAWN = "Pawn";
  public static final String KNIGHT = "Knight";
  public static final String BLACK = "Black";
  public static final String WHITE = "White";
  public static final int[] upIShifts = {-1};
  public static final int[] upJShifts = {0};
  public static final int[] downIShifts = {1};
  public static final int[] downJShifts = {0};
  public static final int[] rightIShifts = {0};
  public static final int[] rightJShifts = {1};
  public static final int[] leftIShifts = {0};
  public static final int[] leftJShifts = {-1};
  public static final int[] lateralIShifts = {-1, 1, 0, 0};
  public static final int[] lateralJShifts = {0, 0, -1, 1};
  public static final int[] diagonalIShifts = {-1, -1, 1, 1};
  public static final int[] diagonalJShifts = {-1, 1, -1, 1};
  public static final int[] anyIShifts = {-1, 1, 0, 0, -1, -1, 1, 1};
  public static final int[] anyJShifts = {0, 0, -1, 1, -1, 1, -1, 1};
  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
      Pair<String, Integer>> pieces) {
    super(settings, locations, pieces);
  }

  @Override
  public List<Point2D> getValidMoves(Point2D coordinate) {
    int i = (int) coordinate.getX();
    int j = (int) coordinate.getY();
    Piece piece = getPieceAt(i, j);
    if (piece == null) {
      return new ArrayList<>();
    }
    List<Point2D> thisPieceValidMoves = getValidMovesIgnoreCheck(i, j);
    String color = piece.getColor();
    Point2D kingPoint = locateKings(color);

    Pair<List<Point2D>, List<Point2D>> checks = getMovesAndCheckPieces(kingPoint, color, true);
    List<Point2D> checkPieces = checks.getValue();
    if (piece.getType().equals(KING)) {
      List<Point2D> safeKings = getSafeKingMoves(thisPieceValidMoves, checks.getKey());
      return checkDanger(safeKings, kingPoint);
    }
    List<Point2D> blockingPath = getBlockingMoves(i, j, kingPoint, checkPieces);
    if(blockingPath != null){
      thisPieceValidMoves.retainAll(blockingPath);
    }

    if (checkPieces.size() == 0){
      return thisPieceValidMoves;
    }

    if (checkPieces.size() > 1) {
      return null;
    }

    Point2D threatLoc = checkPieces.get(0);
    List<Point2D> threatPath = getPath(threatLoc, kingPoint);
    threatPath.add(threatLoc);
    thisPieceValidMoves.retainAll(threatPath);

    return thisPieceValidMoves;
  }

  public List<Point2D> getValidMovesIgnoreCheck(int i, int j) {
    Piece piece = getPieceAt(i, j);
    if (piece == null) {
      return null;
    }
    String movePattern = piece.getMovePattern();
    String[] movePatternSplit = movePattern.split(" ");
    String moveType = movePatternSplit[0].toLowerCase();
    List<Integer> params = new ArrayList<>();
    for(int inc = 1; inc < movePatternSplit.length; inc++){
      params.add(Integer.parseInt(movePatternSplit[inc]));
    }
    try {
      int[] iShift = (int[]) this.getClass().getDeclaredField(moveType + "IShifts").get(null);
      int[] jShift = (int[]) this.getClass().getDeclaredField(moveType + "JShifts").get(null);

      System.out.println("iShift = " + Arrays.toString(iShift));
      System.out.println("jShift = " + Arrays.toString(jShift));
      return move(i, j, iShift, jShift, params, piece);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("IN EXCEPTION");
      System.out.println("moveType = " + moveType);
      try {
        Method moveMethod = this.getClass()
            .getDeclaredMethod(moveType, int.class, int.class, List.class,
                piece.getClass());
        Object ret = moveMethod.invoke(this, i, j, params, piece);
        return (List<Point2D>) ret;
      } catch (NoSuchMethodException |IllegalAccessException | InvocationTargetException g){
        System.out.println("Error: " + moveType);
      }
    }
    return null;
  }

  @Override
  public void doMove(Move m) {
    int startX = (int) m.getStartLocation().getX();
    int startY = (int) m.getStartLocation().getY();
    int endX = (int) m.getEndLocation().getX();
    int endY = (int) m.getEndLocation().getY();
    Piece currPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);
    if (! m.isUndo()) {
      currPiece.move();
    } else {
      currPiece.unMove();
    }

    int score = (hitPiece == null) ? 0 : hitPiece.getValue();
    m.setPiece(currPiece);
    // if undo and it was a promote move before
    if (m.isPromote() && m.isUndo()) {
      // demote piece in backend
      m.getPiece().setType(PAWN);
      m.getPiece().setMovePattern("PAWN -1");
      m.getPiece().setValue(pieceTypeMap.get(m.getPiece().getFullName()).getValue());
      // demote piece in frontend
      this.promoteAction.process(m.getStartLocation());
    }
    promote(m);
    pieceBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);

    if (hitPiece != null) {
      m.addCapturedPiece(hitPiece, m.getEndLocation());
      pieceBiMap.remove(hitPiece);
    }
  }

  private void promote(Move m) {
    Piece piece = m.getPiece();
    if (!piece.getType().equals(PAWN)) {
      return;
    }
    int inc = getPawnInc(piece);
    int startX = (int) m.getStartLocation().getX();
    int startY = (int) m.getStartLocation().getY();
    int endX = (int) m.getEndLocation().getX();
    int endY = (int) m.getEndLocation().getY();
    if ((inc == -1 && endX == 0) || (inc == 1 && endX == height - 1)) {
      piece.setType("Queen");
      piece.setMovePattern("Any -1");
      piece.setValue(pieceTypeMap.get(piece.getFullName()).getValue());
      m.setPromote(true);
      this.promoteAction.process(m.getStartLocation());
    }
  }

  private List<Point2D> getBlockingMoves(int blockerI, int blockerJ, Point2D kingPoint, List<Point2D> checkPieces){
    //what is the path from that threat to the king
    //if the path is empty, the piece can't be blocked, so not blocking
    //if the path doesn't contain this location, i'm not blocking
    //if the path does contain this location, return the path
    Piece blocker = getPieceAt(blockerI, blockerJ);
    Point2D blockPoint = new Point2D.Double(blockerI, blockerJ);
    String blockColor = blocker.getColor();
    pieceBiMap.forcePut(blockPoint, null);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece threat = getPieceAt(i, j);
        Point2D threatPoint = new Point2D.Double(i, j);
        if(threat == null || threat.getColor().equals(blockColor) || checkPieces.contains(threatPoint) || !getValidMovesIgnoreCheck(i, j).contains(kingPoint)){
          continue;
        }
        List<Point2D> path = getPath(threatPoint, kingPoint);
        path.add(threatPoint);
        if(path.contains(blockPoint)){
          pieceBiMap.forcePut(blockPoint, blocker);
          return path;
        }
      }
    }
    pieceBiMap.forcePut(blockPoint, blocker);
    return null;
  }

  @Override
  public String checkWon() {
    if (getCheckmate(WHITE, BLACK)) {
      return BLACK;
    }
    if(getCheckmate(BLACK, WHITE)){
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
        if (p.getType().equals(KING)) {
          if (p.getColor().equals(color)) {
            iCoord = i;
            jCoord = j;
          }
        }
      }
    }
    if(iCoord == null){
      return null;
    }
    Point2D point = new Point2D.Double(iCoord, jCoord);
    return point;
  }

  private boolean getCheckmate(String ourColor, String opponentColor){
    Point2D kingPoint = locateKings(ourColor);
    if(kingPoint == null){
      return true;
    }

    Pair<List<Point2D>, List<Point2D>> theirMoves = getMovesAndCheckPieces(kingPoint, ourColor, true);
    List<Point2D> opponentMoves = theirMoves.getKey();
    List<Point2D> checkPieces = theirMoves.getValue();

    if (checkPieces.size() == 0) {
      return false;
    }

    List<Point2D> kingMoves = getValidMovesIgnoreCheck((int) kingPoint.getX(), (int) kingPoint.getY());
    List<Point2D> safeMoves = getSafeKingMoves(kingMoves, opponentMoves);

    safeMoves = checkDanger(safeMoves, kingPoint);
    if (!(safeMoves.size() == 0)) {
      // System.out.println("Check but safe moves.");
      return false;
    }

    if (checkPieces.size() > 1) {
      // System.out.println("Dead. Multiple checkers and no safe moves");
      return true;
    }

    return !canKillOrBlock(kingPoint, opponentColor, checkPieces.get(0));
  }

  private List<Point2D> checkDanger(List<Point2D> safeMoves, Point2D kingPoint){
    //c) in safe spots, check if there is currently a piece here. if so, check if the spot is newly accessible by opposing team. if so, remove the spot.
    // System.out.println("Safe spots.");
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
    for (Point2D p : safeMoves) {
       System.out.println("p = " + p);
    }
    return safeMoves;
  }

  private boolean canKillOrBlock(Point2D kingPoint, String opponentColor, Point2D threatLoc){
    Pair<List<Point2D>, List<Point2D>> ourMoveData = getMovesAndCheckPieces(kingPoint, opponentColor, false);
    List<Point2D> ourMoves = ourMoveData.getKey();
    return ourMoves.contains(threatLoc) || canBlock(threatLoc, kingPoint, ourMoves);
  }

  private boolean canBlock(Point2D threatLoc, Point2D kingPoint, List<Point2D> ourMoves){
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
    updatePawnAndMoveLists(pawnList, checkPieces, allPossibleMoves, kingPoint, targetColor, ignoreTheirKing);
    if (ignoreTheirKing) {
      pieceBiMap.forcePut(kingPoint, storedKing);
    }

    checkPossiblePawnMoves(pawnList, checkPieces, allPossibleMoves, kingPoint, ignoreTheirKing);

    return new Pair<>(allPossibleMoves, checkPieces);
  }

  private void updatePawnAndMoveLists(List<Point2D> pawnList, List<Point2D> checkPieces, List<Point2D> allPossibleMoves, Point2D kingPoint, String targetColor, boolean ignoreTheirKing){
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Point2D thisPoint = new Point2D.Double(i, j);
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMovesIgnoreCheck(i, j);
        if (thisPoint.equals(kingPoint) || thisPiece == null || thisPiece.getColor()
            .equals(targetColor) || (!ignoreTheirKing && thisPiece.getType().equals(
            KING))) {
          continue;
        }
        if(thisPiece.getType().equals(PAWN)){
          pawnList.add(thisPoint);
        }
        else {
          if (thisPieceMoves.contains(kingPoint)) {
            checkPieces.add(thisPoint);
          }
          allPossibleMoves.addAll(thisPieceMoves);
        }
      }
    }
  }

  private void checkPossiblePawnMoves(List<Point2D> pawnList, List<Point2D> checkPieces, List<Point2D> allPossibleMoves, Point2D kingPoint, boolean ignoreTheirKing){
    for(Point2D pawn: pawnList){
      int i = (int) pawn.getX();
      int j = (int) pawn.getY();
      Piece piece = getPieceAt(i, j);
      int inc = getPawnInc(piece);
      int newI = i + inc;
      List<Point2D> thisPieceMoves = getPawnDiags(newI, j, piece, true);
      if(!ignoreTheirKing){
        thisPieceMoves.addAll(getPawnStraights(newI, j, piece, inc));
      }
      if(thisPieceMoves.contains(kingPoint)){
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

  /* Used to see if killing a piece could keep king in check.
  Ignore current position of king. */
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
        if(thisPiece == null){
          continue;
        }
        List<Point2D> thisPieceMoves;
        boolean pawn = thisPiece.getType().equals(PAWN);
        if(pawn){
          int inc = getPawnInc(thisPiece);
          thisPieceMoves = getPawnDiags(i + inc, j, thisPiece,  false);
        }
        else{
          thisPieceMoves = getValidMovesIgnoreCheck(i, j);
        }
        if ((i == potentialI && j == potentialJ) || color
            .equals(thisPiece.getColor())) {
          continue;
        }
        if (thisPieceMoves.contains(potentialPoint)) {
          pieceBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
          pieceBiMap.forcePut(kingPoint, storedKing);
          // System.out.println(String.format("%s at %d , %d", thisPiece, i, j));
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
    //lateral movement in same row
    if (threatI == kingI) {
      return getPathSameRowRook(threatI, threatJ, kingJ);
    }
    //lateral movement in same column
    if (threatJ == kingJ) {
      return getPathSameColRook(threatJ, threatI, kingI);
    }
    //diagonal
    if(isDiagonal(threatI, threatJ, kingI, kingJ)){
      return getPathDiagonal(threatI, threatJ, kingI, kingJ);
    }
    return new ArrayList<>();
  }

  private List<Point2D> getPathSameRowRook(int i, int threatJ, int kingJ){
    List<Point2D> path = new ArrayList<>();

    int greaterJ = Math.max(threatJ, kingJ);
    int smallerJ = Math.min(threatJ, kingJ);

    for(int j = smallerJ + 1; j < greaterJ; j++){
      Point2D pointOnPath = new Point2D.Double(i, j);
      path.add(pointOnPath);
    }
    return path;
  }

  private List<Point2D> getPathSameColRook(int j, int threatI, int kingI){
    List<Point2D> path = new ArrayList<>();

    int greaterI = Math.max(threatI, kingI);
    int smallerI = Math.min(threatI, kingI);

    for(int i = smallerI + 1; i < greaterI; i++){
      Point2D pointOnPath = new Point2D.Double(i, j);
      path.add(pointOnPath);
    }
    return path;
  }

  private List<Point2D> getPathDiagonal(int threatI, int threatJ, int kingI, int kingJ){
    List<Point2D> path = new ArrayList<>();

    int greaterI = Math.max(threatI, kingI);
    int smallerI = Math.min(threatI, kingI);
    int smallerJ = Math.min(threatJ, kingJ);
    int greaterJ = Math.max(threatJ, kingJ);

    for(int inc = 1; inc < greaterI - smallerI; inc++){
      int newJ;
      if((threatI < kingI && threatJ > kingJ) || (kingI < threatI && kingJ > threatJ)){
        newJ = greaterJ - inc;
      }
      else{
        newJ = smallerJ + inc;
      }
      Point2D pointOnPath = new Point2D.Double(smallerI + inc, newJ);
      path.add(pointOnPath);
    }
    return path;
  }

  private boolean isDiagonal(int threatI, int threatJ, int kingI, int kingJ){
    return Math.abs(kingJ - threatJ) == Math.abs(kingI - threatI);
  }
  
  private List<Point2D> knight(int i, int j, List<Integer> params, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int first = params.get(0);
    int second = params.get(1);
    int[] iShifts = {first, first, -first, -first, second, second, -second, -second};
    int[] jShifts = {second, -second, second, -second, first, -first, first, -first};

    for(int idx = 0; idx < iShifts.length; idx++){
      int newI = i + iShifts[idx];
      int newJ = j + jShifts[idx];
      Point2D newPoint = checkPoint(newI, newJ, piece);
      if(newPoint != null){
        ret.add(newPoint);
      }
    }
    return ret;
  }

  private List<Point2D> pawn(int i, int j, List<Integer> params, Piece piece) {
    List<Point2D> ret = new ArrayList<>();
    int inc = getPawnInc(piece);
    int newI = i + inc;
    ret.addAll(getPawnDiags(newI, j, piece, true));
    ret.addAll(getPawnStraights(newI, j, piece, inc));
    return ret;
  }

  private int getPawnInc(Piece piece){
    int inc;
    if (piece.getColor().equals(bottomColor)) {
      inc = -1;
    } else {
      inc = 1;
    }
    return inc;
  }

  private List<Point2D> getPawnDiags(int newI, int j, Piece piece, boolean check){
    List<Point2D> ret = new ArrayList<>();
    int[] diagJ = {-1, 1};
    for (int jInc : diagJ) {
      int potJ = j + jInc;
      Point2D newPoint = checkPoint(newI, potJ, piece);
      if (newPoint != null && (!check || getPieceAt(newI, potJ) != null)) {
        ret.add(newPoint);
      }
    }
    return ret;
  }

  private  List<Point2D> getPawnStraights(int newI, int j, Piece piece, int inc){
    List<Point2D> ret = new ArrayList<>();
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

  private List<Point2D> move (int i, int j, int[] iShifts, int[] jShifts, List<Integer> params, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int distance = params.get(0);
    for(int shift = 0; shift < iShifts.length; shift++) {
      int inc = 1;
      while (inc <= distance || distance < 0) {
        System.out.println(inc);
        int newI = i + iShifts[shift] * inc;
        int newJ = j + jShifts[shift] * inc;
        Point2D newPoint = checkPoint(newI, newJ, piece);
        if (newPoint != null) {
          System.out.println("newPoint = " + newPoint);
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

  private Point2D checkPoint(int x, int y, Piece thisPiece) {
    Point2D ret;
    if (!isCellInBounds(x, y)) {
      return null;
    }
    Piece thatPiece = getPieceAt(x, y);
    if (thatPiece != null && thisPiece.isSameColor(thatPiece)) {
      return null;
    }
    ret = new Point2D.Double(x, y);
    return ret;
  }
}
