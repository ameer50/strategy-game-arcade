import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
    String gameXML = String.format("resources/Chess/chessJUnit.xml");
    XMLProcessor processor = new XMLProcessor();
    processor.parse(gameXML);
    board = new ChessBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
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
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,7));
    assertEquals(moves.size(), 0);
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
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(6,0));
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
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(5,1));
    assertEquals(moves.size(), 1);
  }

  @Test
  public void testDiagonalKillPawn(){
    Point2D point = new Point2D.Double(5,1);
    board.placePiece(point, new Piece("Pawn", "pawn 1", 0, "Black"));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(6,0));
    Point2D firstMove = point;
    Point2D secondMove = new Point2D.Double(5, 0);
    Point2D thirdMove = new Point2D.Double(4, 0);
    assertEquals(moves.get(0), firstMove);
    assertEquals(moves.get(1), secondMove);
    assertEquals(moves.get(2), thirdMove);
    assertEquals(moves.size(), 3);
  }

  @Test
  public void testKnightMoves(){
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(7,6));
    assertEquals(moves.size(), 2);
    Point2D firstMove = new Point2D.Double(5, 7);
    Point2D secondMove = new Point2D.Double(5, 5);
    assertEquals(moves.get(0), firstMove);
    assertEquals(moves.get(1), secondMove);
  }

  @Test
  public void testBishopMoves(){
    board.placePiece(new Point2D.Double(4,4), new Piece("Bishop", "diagonal -1", 5, "White"));
    List<Point2D> actual = new ArrayList<>();
    actual.add(new Point2D.Double(5, 5));
    actual.add(new Point2D.Double(5, 3));
    actual.add(new Point2D.Double(3, 5));
    actual.add(new Point2D.Double(2, 6));
    actual.add(new Point2D.Double(1, 7));
    actual.add(new Point2D.Double(3, 3));
    actual.add(new Point2D.Double(2, 2));
    actual.add(new Point2D.Double(1, 1));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,4));
    assertEquals(moves.size(), actual.size());
    for(Point2D point: actual){
      assertTrue(moves.contains(point));
    }
  }
  @Test
  public void testRookMoves(){
    board.placePiece(new Point2D.Double(4,4), new Piece("Rook", "lateral -1", 5, "Black"));
    List<Point2D> actual = new ArrayList<>();
    actual.add(new Point2D.Double(3, 4));
    actual.add(new Point2D.Double(2, 4));
    actual.add(new Point2D.Double(5, 4));
    actual.add(new Point2D.Double(6, 4));
    actual.add(new Point2D.Double(4, 3));
    actual.add(new Point2D.Double(4, 2));
    actual.add(new Point2D.Double(4, 1));
    actual.add(new Point2D.Double(4, 0));
    actual.add(new Point2D.Double(4, 5));
    actual.add(new Point2D.Double(4, 6));
    actual.add(new Point2D.Double(4, 7));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,4));
    System.out.println(moves);
    assertEquals(moves.size(), actual.size());
    for(Point2D point: actual){
      assertTrue(moves.contains(point));
    }
  }
  @Test
  public void testQueenMoves(){
    board.placePiece(new Point2D.Double(4,4), new Piece("Queen", "any -1", 5, "White"));
    List<Point2D> actual = new ArrayList<>();
    actual.add(new Point2D.Double(3, 4));
    actual.add(new Point2D.Double(2, 4));
    actual.add(new Point2D.Double(1, 4));
    actual.add(new Point2D.Double(5, 4));
    actual.add(new Point2D.Double(4, 3));
    actual.add(new Point2D.Double(4, 2));
    actual.add(new Point2D.Double(4, 1));
    actual.add(new Point2D.Double(4, 0));
    actual.add(new Point2D.Double(4, 5));
    actual.add(new Point2D.Double(4, 6));
    actual.add(new Point2D.Double(4, 7));
    actual.add(new Point2D.Double(5, 5));
    actual.add(new Point2D.Double(5, 3));
    actual.add(new Point2D.Double(3, 5));
    actual.add(new Point2D.Double(2, 6));
    actual.add(new Point2D.Double(1, 7));
    actual.add(new Point2D.Double(3, 3));
    actual.add(new Point2D.Double(2, 2));
    actual.add(new Point2D.Double(1, 1));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,4));
    System.out.println(moves);
    assertEquals(moves.size(), actual.size());
    for(Point2D point: actual){
      assertTrue(moves.contains(point));
    }
  }
  @Test
  public void testKingMoves(){
    board.placePiece(new Point2D.Double(4,4), new Piece("King", "any 1", 25, "White"));
    List<Point2D> actual = new ArrayList<>();
    actual.add(new Point2D.Double(3, 4));
    actual.add(new Point2D.Double(5, 4));
    actual.add(new Point2D.Double(4, 3));
    actual.add(new Point2D.Double(4, 5));
    actual.add(new Point2D.Double(5, 5));
    actual.add(new Point2D.Double(5, 3));
    actual.add(new Point2D.Double(3, 5));
    actual.add(new Point2D.Double(3, 3));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,4));
    System.out.println(moves);
    assertEquals(moves.size(), actual.size());
    for(Point2D point: actual){
      assertTrue(moves.contains(point));
    }
  }
  @Test
  public void testCantKillTeammatesButKillOpponents(){
    Piece rook = board.getPieceAt(7, 0);
    Piece blackPawn = board.getPieceAt(1, 0);
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(7,0));
    assertEquals(moves.size(), 0);
    board.putPieceAt(new Point2D.Double(6, 0), null);
    moves = board.getValidMoves(new Point2D.Double(7,0));
    assertTrue(moves.contains(new Point2D.Double(1, 0)));
    Move m = new Move(new Point2D.Double(7, 0), new Point2D.Double(1, 0));
    board.doMove(m);
    assertEquals(board.getPieceAt(1, 0), rook);
  }
  @Test
  public void testLimitKingMovesUnderCheck(){
    board.placePiece(new Point2D.Double(4,4), new Piece("King", "any 1", 25, "White"));
    board.placePiece(new Point2D.Double(7,3), null);
    board.placePiece(new Point2D.Double(4,0), new Piece("Rook", "lateral -1", 55, "Black"));
    List<Point2D> actual = new ArrayList<>();
    actual.add(new Point2D.Double(3, 4));
    actual.add(new Point2D.Double(5, 4));
    actual.add(new Point2D.Double(5, 5));
    actual.add(new Point2D.Double(5, 3));
    actual.add(new Point2D.Double(3, 5));
    actual.add(new Point2D.Double(3, 3));
    List<Point2D> moves = board.getValidMoves(new Point2D.Double(4,4));
    System.out.println(moves);
    assertEquals(moves.size(), actual.size());
    for(Point2D point: actual){
      assertTrue(moves.contains(point));
    }
  }
  @Test
  public void testLimitOtherPiecesUnderCheck(){
    board.placePiece(new Point2D.Double(4,4), new Piece("King", "any 1", 25, "White"));
    board.placePiece(new Point2D.Double(7,3), null);
    board.placePiece(new Point2D.Double(4,0), new Piece("Rook", "lateral -1", 55, "Black"));

    assertEquals(board.getValidMoves(new Point2D.Double(6,0)).size(), 0);
    assertEquals(board.getValidMoves(new Point2D.Double(6,4)).size(), 0);
    assertEquals(board.getValidMoves(new Point2D.Double(6,5)).size(), 0);
    //white queen can't move laterally (king has been removed)
    assertEquals(board.getValidMoves(new Point2D.Double(7,4)).size(), 0);
  }

  @Test
  public void testBlocking(){
    board.placePiece(new Point2D.Double(4, 4), new Piece("King", "any 1", 25, "White"));
    board.placePiece(new Point2D.Double(7, 3), null);
    board.placePiece(new Point2D.Double(4, 0), new Piece("Rook", "lateral -1", 55, "Black"));
    board.placePiece(new Point2D.Double(4, 5), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(3, 5), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(5, 5), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(3, 3), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(5, 3), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(3, 4), new Piece("Pawn", "pawn 1", 0, "White"));
    board.placePiece(new Point2D.Double(5, 4), new Piece("Pawn", "pawn 1", 0, "White"));

    //white pawns can block
    assertEquals(board.getValidMoves(new Point2D.Double(4, 4)).size(), 0);
    assertEquals(board.getValidMoves(new Point2D.Double(5, 3)).size(), 1);
    assertEquals(board.getValidMoves(new Point2D.Double(6, 1)).size(), 1);
    assertEquals(board.getValidMoves(new Point2D.Double(6, 2)).size(), 1);
    assertNull(board.checkWon());
  }

  @Test
  public void testCheckWon(){
    board.placePiece(new Point(3, 4), new Piece("King", "any 1", 25, "White"));
    board.placePiece(new Point(7, 3), null);

    board.placePiece(new Point(4, 0), new Piece("Rook", "lateral -1", 55, "Black"));
    board.placePiece(new Point(3, 0), new Piece("Rook", "lateral -1", 55, "Black"));
    board.placePiece(new Point(2, 0), new Piece("Rook", "lateral -1", 55, "Black"));

    //blockers removed
    assertEquals(board.getValidMoves(new Point2D.Double(3, 4)).size(), 0);
    assertEquals(board.checkWon(), "Black");
  }
}
