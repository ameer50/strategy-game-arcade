import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.board.Piece;
import ooga.history.Move;
import ooga.view.PieceView;
import ooga.xml.XMLProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChessBoardTests {
  ChessBoard board;
  @BeforeEach
  public void setUp(){
    String gameXML = String.format("resources/Chess/defaultWhite.xml");
    XMLProcessor processor = new XMLProcessor();
    processor.parse(gameXML);
    board = new ChessBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
    board.setOnPieceCaptured((int toX, int toY) -> {
      //boardView.getCellAt(toX, toY).setPiece(null);
    });

    board.setOnPiecePromoted((int toX, int toY) -> {
      //board.getPieceAt(toX, toY);
      //boardView.getCellAt(toX, toY).setPiece(new PieceView(board.getPieceAt(toX, toY).getFullName()));
    });
  }

  @Test
  public void testCorrectDims(){
    int height = board.getHeight();
    int width = board.getWidth();
    assertEquals(height, 8);
    assertEquals(width, 8);
  }

  @Test
  public void testCorrectStartPos(){
    Piece pawn = board.getPieceAt(6, 0);
    Piece rook = board.getPieceAt(0, 0);
    assertEquals(pawn.toString(), "White Pawn");
    assertEquals(rook.toString(), "Black Rook");
  }

  @Test
  public void testEmptyCells(){
    Piece empty = board.getPieceAt(4, 7);
    assertNull(empty);
  }

  @Test
  public void testInvalidCells(){
    Piece p1 = board.getPieceAt(10, 17);
    Piece p2 = board.getPieceAt(-20, -5);
    assertNull(p1);
    assertNull(p2);
  }

  @Test
  public void testGetValidMovesEmptyCell(){
    List<Point2D> moves = board.getValidMoves(4, 7);
    assertNull(moves);
  }


  @Test
  public void testMovePiece(){
    Piece pawn1 = board.getPieceAt(6, 1);
    Move m = new Move(new Point2D.Double(6, 1), new Point2D.Double(5, 1));
    board.doMove(m);
    Piece pawn2 = board.getPieceAt(5, 1);
    Piece empty = board.getPieceAt(6, 1);
    assertEquals(pawn1.toString(), "White Pawn");
    assertEquals(pawn2.toString(), "White Pawn");
    assertNull(empty);
    assertEquals(pawn1, pawn2);
  }

  @Test
  public void testTwoMovesOnStartPawn(){
    List<Point2D> moves = board.getValidMoves(6, 0);
    Point2D firstMove = new Point2D.Double(5, 0);
    Point2D secondMove = new Point2D.Double(4, 0);
    assertEquals(moves.size(), 2);
    assertEquals(moves.get(0), firstMove);
    assertEquals(moves.get(1), secondMove);
  }

  @Test
  public void testOnlyOneMoveAfterFirstPawn(){
    Move m = new Move(new Point2D.Double(6, 1), new Point2D.Double(5, 1));
    board.doMove(m);
    List<Point2D> moves = board.getValidMoves(5, 1);
    assertEquals(moves.size(), 1);
  }

  @Test
  public void testDiagonalKillPawn(){
    board.placePiece(5, 1, new Piece("Pawn", "pawn 1", 0, "Black"));
    Move m = new Move(new Point2D.Double(6, 1), new Point2D.Double(5, 1));
    board.doMove(m);
    List<Point2D> moves = board.getValidMoves(5, 1);
    assertEquals(moves.size(), 1);
  }

  @Test
  public void testKnightMoves(){
    List<Point2D> moves = board.getValidMoves(7, 6);
    assertEquals(moves.size(), 2);
    Point2D firstMove = new Point2D.Double(5, 7);
    Point2D secondMove = new Point2D.Double(5, 5);
    assertEquals(moves.get(0), firstMove);
    assertEquals(moves.get(1), secondMove);
  }

  @Test
  public void testBishopMoves(){
    List<Point2D> moves = board.getValidMoves(7, 6);
    assertEquals(moves.size(), 2);
    Point2D firstMove = new Point2D.Double(5, 7);
    Point2D secondMove = new Point2D.Double(5, 5);
    assertEquals(moves.get(0), firstMove);
    assertEquals(moves.get(1), secondMove);
  }

  @Test
  public void testCantKillTeammatesButKillOpponents(){
    Piece rook = board.getPieceAt(7, 0);
    Piece blackPawn = board.getPieceAt(1, 0);
    List<Point2D> moves = board.getValidMoves(7, 0);
    assertEquals(moves.size(), 0);
    board.putPieceAt(new Point2D.Double(6, 0), null);
    moves = board.getValidMoves(7, 0);
    assertTrue(moves.contains(new Point2D.Double(1, 0)));
    Move m = new Move(new Point2D.Double(7, 0), new Point2D.Double(1, 0));
    board.doMove(m);
    assertEquals(board.getPieceAt(1, 0), rook);
  }
}
