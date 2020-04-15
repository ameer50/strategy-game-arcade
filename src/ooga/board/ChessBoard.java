package ooga.board;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class ChessBoard extends Board implements Serializable {

  public static final String KING = "King";
  public static final String PAWN = "Pawn";
  public static final String KNIGHT = "Knight";
  public static final String BLACK = "Black";
  public static final String WHITE = "White";

  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
      Pair<String, Integer>> pieces) {
    super(settings, locations, pieces);
  }

  @Override
  public List<Point2D> getValidMoves(int i, int j) {
    Piece piece = getPieceAt(i, j);
    if (piece == null) {
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0].toLowerCase();
    int moveDistance = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method moveMethod = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class,
              piece.getClass());
      Object ret = moveMethod.invoke(this, i, j, moveDistance, piece);
      return (List<Point2D>) ret;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      System.out.println("Error: " + moveType);
    }
    return null;
  }

  @Override
  public int doMove(int startX, int startY, int endX, int endY, boolean undo) {
    Piece currPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);
    if(!undo) {
      currPiece.move();
    } else {
      currPiece.unMove();
    }
    int score = 0;
    if (hitPiece != null) {
      score = hitPiece.getValue();
      pieceBiMap.remove(hitPiece); // ***
    }
    pieceBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);
    promote(currPiece, endX, endY);
    return score;
  }

  private void promote(Piece piece, int endX, int endY){
    if(!piece.toString().equals(PAWN)){
      return;
    }
    int inc = getPawnInc(piece);
    if((inc == -1 && endX == 0) || (inc == 1 && endX == height - 1)){
      piece.setType("Queen");
      piece.setMovePattern("Any -1");
      this.promoteAction.process(endX, endY);
    }
  }

  @Override
  public String checkWon() {
   /* a) If not in check return 'null'. If not in check, checkPieces.size() is 0.
    b) Move king. Does king.validMoves have point not in allPossibleMoves. If yes, return FALSE. If not, keep going.
    c) If, when the king moves, it kills an opposing piece, we need to make sure that the new square isn't newly
    accessible to opposing pieces. If we have a safe move return FALSE.
    d) At this point, if there are multiple checking pieces, return color.
    e) Kill threatening piece. Is piece.xy in valid moves of our team?
    f) Knights and Pawns cannot be blocked.
    g) Block threatening piece. What is the path from threatening to king?
    If same x and higher y, it is moving upwards. If lower y, moving downwards.
    If same y and higher x, it is moving left. Otherwise moving right.
    If different x and y, and the difference between their x and our x = their y and our y, it is diagonal.
    If diff x and y and those differences aren't the same, it's a knight and we can't block.
    I need every move the other team can make and every piece holding in check. */
    Integer[] coords = locateKings();
    Integer blackI = coords[0];
    Integer blackJ = coords[1];
    Integer whiteI = coords[2];
    Integer whiteJ = coords[3];
    if(getCheckmate(whiteI, whiteJ, WHITE, BLACK)){
      System.out.println("Checkmate");
      return WHITE;
    }
    if(getCheckmate(blackI, blackJ, BLACK, WHITE)){
      System.out.println("Checkmate");
      return BLACK;
    }
    return null;
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
        if (p.getType().equals(KING)) {
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

  private boolean getCheckmate(int kingI, int kingJ, String ourColor, String opponentColor){
    Pair<List<Point2D>, List<Point2D>> theirMoves = getMovesAndCheckPieces(kingI, kingJ, ourColor, true);
    List<Point2D> opponentMoves = theirMoves.getKey();
    List<Point2D> checkPieces = theirMoves.getValue();

    if (checkPieces.size() == 0) {
      System.out.println("NO CHECK");
      return false;
    }

    List<Point2D> kingMoves = getValidMoves(kingI, kingJ);
    List<Point2D> safeMoves = getSafeKingMoves(kingMoves, opponentMoves);

    safeMoves = checkDanger(safeMoves, kingI, kingJ);
    if(!(safeMoves.size() == 0)){
      System.out.println("CHECK BUT SAFE MOVES");
      return false;
    }

    if (checkPieces.size() > 1) {
      System.out.println("Dead, multiple checkers and no safe moves");
      return true;
    }

    return !canKillOrBlock(kingI, kingJ, opponentColor, checkPieces.get(0));
  }

  private List<Point2D> checkDanger(List<Point2D> safeMoves, int kingI, int kingJ){
    //c) in safe spots, check if there is currently a piece here. if so, check if the spot is newly accessible by opposing team. if so, remove the spot.
    System.out.println("safe spots");
    List<Point2D> hiddenDangerMoves = new ArrayList<>();
    for (Point2D p : safeMoves) {
      int x = (int) p.getX();
      int y = (int) p.getY();
      if (isSpotInDanger(x, y, kingI, kingJ)) {
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

  private boolean canKillOrBlock(int kingI, int kingJ, String opponentColor, Point2D threatLoc){
    Pair<List<Point2D>, List<Point2D>> ourMoveData = getMovesAndCheckPieces(kingI, kingJ, opponentColor, false);
    List<Point2D> ourMoves = ourMoveData.getKey();

    if (ourMoves.contains(threatLoc)) {
      System.out.println("CAN KILL THREAT");
      return true;
    }

    return canBlock(threatLoc, kingI, kingJ, ourMoves);
  }

  private boolean canBlock(Point2D threatLoc, int kingI, int kingJ, List<Point2D> ourMoves){
    int i = (int) threatLoc.getX();
    int j = (int) threatLoc.getY();
    Piece threat = getPieceAt(i, j);
    if (threat.getType().equals(KNIGHT) || threat.getType().equals(PAWN)) {
      System.out.println("CANT BLOCK KNIGHT OR PAWN, DEAD");
      return false;
    }

    List<Point2D> path = getPath(i, j, kingI, kingJ);
    for (Point2D p : path) {
      if (ourMoves.contains(p)) {
        System.out.println("CAN BLOCK");
        return true;
      }
    }
    return false;
  }
  private Pair<List<Point2D>, List<Point2D>> getMovesAndCheckPieces(int kingI, int kingJ,
      String targetColor, boolean ignoreTheirKing) {
    List<Point2D> allPossibleMoves = new ArrayList<>();
    List<Point2D> checkPieces = new ArrayList<>();
    List<Point2D> pawnList = new ArrayList<>();
    Point2D kingPoint = new Point2D.Double(kingI, kingJ);
    Piece storedKing = getPieceAt(kingI, kingJ);
    System.out.println("storedKing = " + storedKing);
    if (ignoreTheirKing) {
      pieceBiMap.forcePut(new Double(kingI, kingJ), null);
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMoves(i, j);
        if ((i == kingI && j == kingJ) || thisPiece == null || thisPiece.getColor()
            .equals(targetColor) || (!ignoreTheirKing && thisPiece.getType().equals(
            KING))) {
          continue;
        }
        if(thisPiece.getType().equals(PAWN)){
          pawnList.add(new Point2D.Double(i, j));
        }
        else {
          if (thisPieceMoves.contains(kingPoint)) {
            checkPieces.add(new Point2D.Double(i, j));
          }
          allPossibleMoves.addAll(thisPieceMoves);
        }
      }
    }
    if (ignoreTheirKing) {
      pieceBiMap.forcePut(new Double(kingI, kingJ), storedKing);
    }
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

  /* Used to see if killing a piece could keep king in check.
  Ignore current position of king. */
  private boolean isSpotInDanger(int potentialI, int potentialJ, int kingI, int kingJ) {
    Point2D potentialPoint = new Point2D.Double(potentialI, potentialJ);
    Piece storedPiece = getPieceAt(potentialI, potentialJ);
    Piece storedKing = getPieceAt(kingI, kingJ);
    String color = storedKing.getColor();
    /*if (storedPiece == null) {
      return false;
    }*/
    pieceBiMap.forcePut(new Double(kingI, kingJ), null);
    pieceBiMap.forcePut(new Double(potentialI, potentialJ), null);
    System.out.println("Potentials: " + potentialI + ", " + potentialJ);

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
          thisPieceMoves = getValidMoves(i, j);
        }
        if ((i == potentialI && j == potentialJ) || color
            .equals(thisPiece.getColor())) {
          continue;
        }
        if (thisPieceMoves.contains(potentialPoint)) {
          pieceBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
          pieceBiMap.forcePut(new Double(kingI, kingJ), storedKing);
          System.out.println(thisPiece + " at " + i + ", " + j);
          return true;
        }
      }
    }
    pieceBiMap.forcePut(new Double(potentialI, potentialJ), storedPiece);
    pieceBiMap.forcePut(new Double(kingI, kingJ), storedKing);
    return false;
  }

  private List<Point2D> getPath(int threatI, int threatJ, int kingI, int kingJ) {
    List<Point2D> path = new ArrayList<>();
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
    return null;
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

  private List<Point2D> any(int x, int y, int dist, Piece piece){
    List<Point2D> lat = lateral(x, y, dist, piece);
    List<Point2D> diag = diagonal(x, y, dist, piece);
    List<Point2D> combined = new ArrayList<>(lat);
    combined.addAll(diag);
    return  combined;
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

  // FIXME: these have a ton of duplication; could be made into much simpler methods
  private List<Point2D> up(int x, int y, int distance, Piece piece) {
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

  private List<Point2D> down(int x, int y, int distance, Piece piece) {
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

  private List<Point2D> right(int x, int y, int distance, Piece piece) {
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

  private List<Point2D> diagonal(int x, int y, int distance, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    int[] iShift = {1, 1, -1, -1};
    int[] jShift = {1, -1, 1, -1};

    for(int i = 0; i < iShift.length; i++){
      int iInc = iShift[i];
      int jInc = jShift[i];
      while(Math.abs(iInc) <= distance || distance < 0){
        int newI = x + iInc;
        int newJ = y + jInc;
        Point2D newPoint = checkPoint(newI, newJ, piece);
        if (newPoint != null) {
          ret.add(newPoint);
          if (getPieceAt(newI, newJ) != null) {
            break;
          }
        } else {
          break;
        }
        iInc += iShift[i];
        jInc += jShift[i];
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
