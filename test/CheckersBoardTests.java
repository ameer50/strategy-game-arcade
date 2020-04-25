import ooga.board.CheckersBoard;
import ooga.board.Piece;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CheckersBoardTests {
    CheckersBoard board;

    @BeforeEach
    public void setUp() {
        String gameJSON = String.format("resources/Checkers/defaultBlack.json");
        JSONProcessor processor = new JSONProcessor();
        processor.parse(gameJSON);
        board = new CheckersBoard(processor.getSettings(), processor.getPieceLocations(), processor.getPieceMovePatterns(), processor.getPieceScores());

    }

    @Test
    public void testSelectedPiecePos() {
        Piece white = board.getPieceAt(1, 0);
        Piece black = board.getPieceAt(5, 0);
        assertEquals(white.getColor(), "Red");
        assertEquals(black.getColor(), "Black");
    }

    @Test
    public void testSelectedPieceNull() {
        Piece nul = board.getPieceAt(4, 4);
        assertNull(nul);
    }

    @Test
    public void testWhiteMove() {
        Piece wh = board.getPieceAt(2, 1);
        Move mov = new Move(new Point2D.Double(2, 1), new Point2D.Double(3, 2));
        board.doMove(mov);
        Piece afterPiece = board.getPieceAt(3, 2);
        Piece origin = board.getPieceAt(2, 1);
        assertEquals(wh.getColor(), "Red");
        assertEquals(afterPiece.getColor(), "Red");
        assertNull(origin);
        assertEquals(wh, afterPiece);
    }

    @Test
    public void testBlackMove() {
        Piece bl = board.getPieceAt(5, 0);
        Move mov = new Move(new Point2D.Double(5, 0), new Point2D.Double(4, 1));
        board.doMove(mov);
        Piece afterPiece = board.getPieceAt(4, 1);
        Piece origin = board.getPieceAt(5, 0);
        assertEquals(bl.getColor(), "Black");
        assertEquals(afterPiece.getColor(), "Black");
        assertNull(origin);
        assertEquals(bl, afterPiece);
    }

    @Test
    public void testOutOfBounds() {
        Piece test_1 = board.getPieceAt(9, 9);
        assertNull(test_1);
    }

    @Test
    public void nonKillOnFirstTry() {
        Piece p1 = board.getPieceAt(5, 2);
        Move m = new Move(new Point2D.Double(5, 2), new Point2D.Double(4, 3));
        board.doMove(m);
        Piece immediateDiagonal = board.getPieceAt(4, 3);
        Piece oneOverDiagonal = board.getPieceAt(3, 4);
        Piece origin = board.getPieceAt(5, 2);
        assertNull(origin);
        assertNull(oneOverDiagonal);
        assertEquals(immediateDiagonal.getColor(), "Black");
    }

    @Test
    public void blackKillWhite() {
        Point2D point = new Point2D.Double(4, 3);
        board.placePieceAt(point, new Piece("Coin", "P2 1", 5, "Red"));
        System.out.println(board);
        Move m = new Move(new Point2D.Double(5, 2), new Point2D.Double(3, 4));
        board.doMove(m);
        board.putPieceAt(point, null);
        Piece p = board.getPieceAt(3, 4);
        Piece origin = board.getPieceAt(5, 2);
        Piece killed = board.getPieceAt(4, 3);
        assertEquals(p.getColor(), "Black");
        assertNull(origin);
        assertNull(killed);
    }

    @Test
    public void whiteKillBlack() {
        Point2D point = new Point2D.Double(3, 2);
        board.placePieceAt(point, new Piece("Coin", "P1 1", 5, "Black"));
        System.out.println(board);
        Move m = new Move(new Point2D.Double(2, 1), new Point2D.Double(4, 3));
        board.doMove(m);
        board.putPieceAt(new Point2D.Double(3, 2), null);
        Piece p = board.getPieceAt(4, 3);
        Piece origin = board.getPieceAt(2, 1);
        Piece killed = board.getPieceAt(3, 2);
        assertEquals(p.getColor(), "Red");
        assertNull(origin);
        assertNull(killed);
    }
}