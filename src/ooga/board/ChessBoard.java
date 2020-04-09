package ooga.board;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class ChessBoard extends Board{
  public static final String KING_STRING = "King";
  public static final String BLACK_STRING = "Black";
  public static final String WHITE_STRING = "White";
  public ChessBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
    super(settings, locs, pieces);
  }

  @Override
  public boolean checkWon() {
    //a) if not in check return null. if in check, checkPieces.size() is 0
    //if in check, three options.
    //b) move king. does king.validMoves have point not in allPossibleMoves. if yes, return false. if not, keep going
    //c) if when the king moves it kills an opposing piece, we need to make sure that the new square isn't newly accessible by opposing pieces
    //d) at this point if there are multiple checking pieces, return color
    //e) kill threatening piece. is piece.xy in valid moves of our team?
    //f) block threatening piece. what is the path from threatening to king?
    //if same x and higher y, it is moving upwards. if lower y, moving downwards. both in straight line.
    //if same y and higher x, it is moving left. otherwise moving right.
    //if different x and y, and the difference between their x and our x = their y and our y, it is diagonal.
    //if diff x and y and those differences aren't the same, it's a knight and we can't block
    //I need every move the other team can make and every piece holding in check
    Integer[] coords = locateKings();
    Integer blackKingI = coords[0];
    Integer blackKingJ = coords[1];
    Integer whiteKingI = coords[2];
    Integer whiteKingJ = coords[3];

    Pair<List<Point2D>, List<Point2D>> dataForWhite = getMovesAndCheckPieces(whiteKingI, whiteKingJ, WHITE_STRING);
    //a) not in check -> false
    List<Point2D> checkPieces = dataForWhite.getValue();
    if(checkPieces.size() == 0){
      return false;
    }
    //b) no safe moves -> true
    List<Point2D> opponentMoves = dataForWhite.getKey();
    List<Point2D> kingMoves = getValidMoves(whiteKingI, whiteKingJ);
    List<Point2D> safeMoves = getSafeKingMoves(kingMoves, opponentMoves);
    if(safeMoves.size() == 0){
      return true;
    }
    //c) in safe spots, check if there is currently a piece here. if so, check if the spot is newly accessible by opposing team. if so, remove the spot.
    System.out.println("safe spots");
    List<Point2D> hiddenDangerMoves = new ArrayList<>();
    for(Point2D p: safeMoves){
      int x = (int) p.getX();
      int y = (int) p.getY();
      if(isSpotInDanger(x, y)){
        hiddenDangerMoves.add(p);
      }
    }

    for(Point2D p: hiddenDangerMoves){
      safeMoves.remove(p);
    }
    for(Point2D p: safeMoves){
      System.out.println("p = " + p);
    }
    //king is safe if after all that, there are still safe moves. return false
    if(safeMoves.size() != 0){
      return false;
    }
    System.out.println("Past safe moves");
    //at this point the king can't move anywhere.
    //d) if there are multiple pieces holding king in check, it's dead
    if(checkPieces.size() > 1){
      System.out.println("Dead at D");
      return true;
    }
    //e) there is only one piece holding the king in check. the king can't escape check. can we kill the piece?
    Pair<List<Point2D>, List<Point2D>> ourMoveData = getMovesAndCheckPieces(whiteKingI, whiteKingJ, BLACK_STRING);
    List<Point2D> ourMoves = ourMoveData.getKey();
    Point2D threatening = checkPieces.get(0);
    if(ourMoves.contains(threatening)){
      System.out.println("SAFE");
      return false;
    }

    return false;
  }

  private Integer[] locateKings(){
    Integer blackKingI = null;
    Integer blackKingJ = null;
    Integer whiteKingI = null;
    Integer whiteKingJ = null;
    for(int i = 0; i < myHeight; i++){
      for(int j = 0; j < myWidth; j++){
        Piece p = getPieceAt(i, j);
        if(p == null){
          continue;
        }
        if(p.toString().equals(KING_STRING)){
          if(p.getColor().equals(BLACK_STRING)) {
            blackKingI = i;
            blackKingJ = j;
          }
          else{
            whiteKingI = i;
            whiteKingJ = j;
          }
        }
      }
    }
    Integer[] ret = {blackKingI, blackKingJ, whiteKingI, whiteKingJ};
    return ret;
  }

  private Pair<List<Point2D>, List<Point2D>> getMovesAndCheckPieces(int kingI, int kingJ, String color){
    List<Point2D> allPossibleMoves = new ArrayList<>();
    List<Point2D> checkPieces = new ArrayList<>();
    Point2D kingPoint = new Point2D.Double(kingI, kingJ);
    for(int i = 0; i < myHeight; i++){
      for(int j = 0; j < myWidth; j++){
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMoves(i, j);
        if((i == kingI && j == kingJ) || thisPiece == null || thisPiece.getColor().equals(color)){
          continue;
        }
        if(thisPieceMoves.contains(kingPoint)){
          checkPieces.add(new Point2D.Double(i, j));
        }
        allPossibleMoves.addAll(thisPieceMoves);
      }
    }
    if(allPossibleMoves.size() == 0 && checkPieces.size() == 0){
      return null;
    }
    Pair<List<Point2D>, List<Point2D>> ret = new Pair<>(allPossibleMoves, checkPieces);
    return ret;
  }

  private List<Point2D> getSafeKingMoves(List<Point2D> kingMoves, List<Point2D> oppMoves){
    List<Point2D> safePoints = new ArrayList<>();
    for(Point2D kingMove: kingMoves){
      if(!oppMoves.contains(kingMove)){
        safePoints.add(kingMove);
      }
    }
    return safePoints;
  }

  //used to see if killing a piece could keep king in check
  private boolean isSpotInDanger(int potentialI, int potentialJ){
    Point2D potentialPoint = new Point2D.Double(potentialI, potentialJ);
    Piece storedPiece = getPieceAt(potentialI, potentialJ);
    if(storedPiece == null){
      return false;
    }
    myGrid[potentialI][potentialJ] = null;
    System.out.println("Potentials: " + potentialI + ", " + potentialJ);
    for(int i = 0; i < myHeight; i++){
      for(int j = 0; j < myWidth; j++){
        Piece thisPiece = getPieceAt(i, j);
        List<Point2D> thisPieceMoves = getValidMoves(i, j);
        if((i == potentialI && j == potentialJ) || thisPiece == null || !storedPiece.getColor().equals(thisPiece.getColor())){
          continue;
        }
        if(thisPieceMoves.contains(potentialPoint)){
          myGrid[potentialI][potentialJ] = storedPiece;
          System.out.println(thisPiece + " at " + i + ", " + j);
          return true;
        }
      }
    }
    myGrid[potentialI][potentialJ] = storedPiece;
    return false;
  }

  @Override
  public List<Point2D> getValidMoves(int x, int y){
    Piece piece = getPieceAt(x, y);
    if(piece == null){
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0].toLowerCase();
    int moveDist = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class, piece.getClass());
      Object returnVal = methodToCall.invoke(this, x, y, moveDist, piece);
      return ((List<Point2D>)returnVal);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      //e.printStackTrace();
      System.out.println("Error handling method " + moveType);
    }
    return null;
  }

  @Override
  public double doMove(int startX, int startY, int endX, int endY) {
    Piece thisPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);
    double score = 0;
    if(hitPiece == null){
      score = 0;
    }
    else{
      score = hitPiece.getValue();
    }

    myGrid[startX][startY] = null;
    myGrid[endX][endY] = thisPiece;
    return score;
  }

  private List<Point2D> lateral(int x, int y, int dist, Piece piece){
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
  private List<Point2D> up(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x - inc;
      Point2D newPoint = findPoint(newX, y, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(newX, y) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> down(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x + inc;
      Point2D newPoint = findPoint(newX, y, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(newX, y) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> right(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y + inc;
      Point2D newPoint = findPoint(x, newY, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(x, newY) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private List<Point2D> left(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newY = y - inc;
      Point2D newPoint = findPoint(x, newY, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(x, newY) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }

  private Point2D findPoint(int x, int y, Piece thisPiece){
    Point2D ret;
    if(!isValidCell(x, y)){
      return null;
    }

    Piece thatPiece = getPieceAt(x, y);
    if(thatPiece != null && thisPiece.isOnSameTeam(thatPiece)){
      return null;
    }

    ret = new Point2D.Double(x, y);
    return ret;
  }
}
