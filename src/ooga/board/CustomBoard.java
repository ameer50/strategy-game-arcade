package ooga.board;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ooga.controller.Point2DUtility;
import ooga.custom.MoveNode;
import ooga.history.Move;

public class CustomBoard extends Board {

  public CustomBoard(int width, int height, Map<Point2D, String> locations,
      Map<String, MoveNode> pieceMoves, Map<String, Long> pieceScores) {
    super(width, height, locations, pieceMoves, pieceScores);
  }

  public String checkWon() {
    return null;
  }

  public void doMove(Move move) {
    int startX = move.getStartX();
    int startY = move.getStartY();
    int endX = move.getEndX();
    int endY = move.getEndY();
    Piece currPiece = getPieceAt(startX, startY);
    Piece hitPiece = getPieceAt(endX, endY);

    if (! move.isUndo()) currPiece.move();
    else currPiece.unMove();

    if (hitPiece != null) {
      move.addCapturedPiece(hitPiece, move.getEndLocation());
      pieceBiMap.remove(hitPiece);
    }
    pieceBiMap.remove(currPiece); // Just in case...
    pieceBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);

    move.setPiece(currPiece);
  }

  public List<Point2D> getValidMoves(int i, int j) {
    Piece piece = getPieceAt(i, j);
    /* FIXME: remove */
    System.out.println(piece.getValue());
    if (piece == null) return null;

    Point2DUtility utility = new Point2DUtility();
    List<Point2D> displacements = piece.getDisplacements();
    List<Point2D> originalCoordinates = List.of(new Double(i, j));
    List<Point2D> displacedCoordinates = utility.concatPointLists(displacements, originalCoordinates);
    List<Point2D> validCoordinates = new ArrayList<>();
    for (Point2D point: displacedCoordinates) {
      if (canMoveToPoint(point, piece.getColor())) {
        validCoordinates.add(point);
      }
    }
    /* FIXME: remove */
    System.out.println(validCoordinates);
    return validCoordinates;
  }

  private boolean canMoveToPoint(Point2D point, String color) {
    Piece pieceAtPoint = getPieceAt(point);
    if (! isCellInBounds(point)) {
      return false;
    } else if (pieceAtPoint==null) {
      return true;
    } else if (pieceAtPoint.getColor().equals(color)) {
      return false;
    } else {
      return true;
    }
  }
}
