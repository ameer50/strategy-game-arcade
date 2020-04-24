package ooga.board;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ooga.utility.Point2DUtility;
import ooga.custom.MoveNode;
import ooga.history.Move;

public class CustomBoard extends Board {

  public CustomBoard(Map<String, String> settings, Map<Point2D, String> locations,
      Map<String, String> movePatterns, Map<String, Integer> pieceScores) {
      super(settings, locations, movePatterns, pieceScores);
  }

  public String checkWon() {
    // TODO: check for sides of the board with no more pieces
    return null;
  }

  public void doMove(Move move) {
    int startX = move.getStartX();
    int startY = move.getStartY();
    int endX = move.getEndX();
    int endY = move.getEndY();
    Piece currPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);

    currPiece.incrementMoveCount(move.isUndo());

    if (hitPiece != null) {
      move.addCapturedPiece(hitPiece, move.getEndLocation());
      pieceBiMap.remove(hitPiece);
    }
    pieceBiMap.remove(currPiece); // Just in case...
    pieceBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);

    move.setPiece(currPiece);
  }

  public List<Point2D> getValidMoves(Point2D coordinate) {
    Piece piece = getPieceAt(coordinate);
    if (piece == null) return null;

    Point2DUtility utility = new Point2DUtility();
    List<Point2D> displacements = piece.getDisplacements();
    List<Point2D> originalCoordinate = List.of(coordinate);
    List<Point2D> displacedCoordinates = utility.concatPointLists(displacements, originalCoordinate);
    List<Point2D> validCoordinates = new ArrayList<>();
    for (Point2D point: displacedCoordinates) {
      if (canMoveToPoint(point, piece.getColor())) {
        validCoordinates.add(point);
      }
    }
    return validCoordinates;
  }

  private boolean canMoveToPoint(Point2D point, String color) {
    Piece pieceAtPoint = getPieceAt(point);
    if (! isCellInBounds(point)) return false;
    if (pieceAtPoint==null) return true;
    if (pieceAtPoint.getColor().equals(color)) return false;
    return true;
  }
}
