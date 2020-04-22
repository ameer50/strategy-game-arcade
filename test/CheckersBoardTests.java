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
import ooga.board.CheckersBoard;
import ooga.board.ChessBoard;
import ooga.board.Piece;
import ooga.history.Move;
import ooga.view.PieceView;
import ooga.xml.XMLProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CheckersBoardTests{
    CheckersBoard board;
    @BeforeEach
    public void setUp(){
        String gameXML = String.format("resources/Checkers/defaultBlack.xml");
        XMLProcessor processor = new XMLProcessor();
        processor.parse(gameXML);
        board = new CheckersBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
    }

    @Test
    public void testSelectedPiecePos(){
        Piece white = board.getPieceAt(1, 0);
        Piece black = board.getPieceAt(5, 0);
        assertEquals(white.getColor(), "White");
        assertEquals(black.getColor(), "Black");
    }

    @Test
    public void testSelectedPieceNull(){
        Piece nul = board.getPieceAt(4, 4);
        assertNull(nul);
    }

    @Test
    public void testWhiteMove(){
        Piece wh = board.getPieceAt(2, 1);
        Move mov = new Move(new Point2D.Double(2, 1), new Point2D.Double(3, 2));
        board.doMove(mov);
        Piece afterPiece = board.getPieceAt(3, 2);
        Piece origin = board.getPieceAt(2, 1);
        assertEquals(wh.getColor(), "White");
        assertEquals(afterPiece.getColor(), "White");
        assertNull(origin);
        assertEquals(wh, afterPiece);
    }

    @Test
    public void testBlackMove(){
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
    public void testOutOfBounds(){
        Piece test_1 = board.getPieceAt(9, 9);
        assertNull(test_1);
    }

    @Test
    public void nonKillOnFirstTry(){
        Piece p1 = board.getPieceAt(5, 2);
        Move m = new Move(new Point2D.Double(5, 2), new Point2D.Double(4, 3));
        board.doMove(m);
        Piece immediateDiagonal = board.getPieceAt(4, 3);
        Piece oneOverDiagonal = board.getPieceAt(3,4);
        Piece origin = board.getPieceAt(5, 2);
        assertNull(origin);
        assertNull(oneOverDiagonal);
        assertEquals(immediateDiagonal.getColor(), "Black");
    }

    @Test
    public void blackKillWhite(){
        Point2D point = new Point2D.Double(4,3);
        board.placePiece(point, new Piece("Coin", "P2 1", 5, "White"));
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
    public void whiteKillBlack(){
        Point2D point = new Point2D.Double(3,2);
        board.placePiece(point, new Piece("Coin", "P1 1", 5, "Black"));
        System.out.println(board);
        Move m = new Move(new Point2D.Double(2, 1), new Point2D.Double(4, 3));
        board.doMove(m);
        board.putPieceAt(new Point2D.Double(3, 2), null);
        Piece p = board.getPieceAt(4, 3);
        Piece origin = board.getPieceAt(2, 1);
        Piece killed = board.getPieceAt(3, 2);
        assertEquals(p.getColor(), "White");
        assertNull(origin);
        assertNull(killed);
    }
}