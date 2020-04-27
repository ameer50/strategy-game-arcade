import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import ooga.view.BoardView;
import ooga.view.CellView;
import ooga.view.PieceView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import java.awt.geom.Point2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardViewTests extends DukeApplicationTest {

    Board board;
    BoardView boardView;

    @BeforeEach
    public void setUp() {
        String gameJSON = String.format("resources/Chess/defaultWhite.json");
        JSONProcessor processor = new JSONProcessor();
        processor.parse(gameJSON);
        board = new ChessBoard(processor.getSettings(), processor.getPieceLocations(), processor.getPieceMovePatterns(), processor.getPieceScores());
        boardView = new BoardView(8, 8, processor.getPieceLocations());
    }

    @Test
    public void testBoardDimensions() {
        int height = boardView.getNumRows();
        int width = boardView.getNumCols();
        assertEquals(height, 8);
        assertEquals(width, 8);
    }

    @Test
    public void testValidPieceName() {
        String pieceName = boardView.getCellAt(7, 7).getPieceView().getPieceName();
        assertEquals(pieceName, "White_Rook");
    }

    @Test
    public void testHighlightValidMoves() {
        List<Point2D> validMoves = board.getValidMoves(new Point2D.Double(6, 7));
        boardView.highlightValidMoves(validMoves);

        assertEquals(true, boardView.getCellAt(5, 7).isHasYellowBorder());
        assertEquals(true, boardView.getCellAt(4, 7).isHasYellowBorder());
    }

    @Test
    public void testToggleRed() {
        Point2D point = new Point2D.Double(6, 7);
        boardView.setSelectedLocation(point);
        assertEquals(true, boardView.getCellAt(point).isHasRedBorder());
    }

    @Test
    public void testdoMove() {
        PieceView currPiece = boardView.getCellAt(6, 7).getPieceView();
        Move move = new Move(new Point2D.Double(6, 7), new Point2D.Double(5, 7));
        boardView.doMove(move);
        assertEquals(null, boardView.getCellAt(6, 7).getPieceView());
        assertEquals(currPiece, boardView.getCellAt(5, 7).getPieceView());
    }

    @Test
    public void testToggleNoBorder() {
        boardView.getCellAt(6, 7).toggleNoBorder();

        assertEquals(false, boardView.getCellAt(6, 7).isHasRedBorder());
        assertEquals(false, boardView.getCellAt(6, 7).isHasYellowBorder());
    }

    @Test
    public void testSetPiece() {
        assertEquals(null, boardView.getCellAt(5, 7).getPieceView());
        PieceView piece = new PieceView("White_Pawn");
        boardView.getCellAt(5, 7).setPieceView(piece);
        assertEquals(piece, boardView.getCellAt(5, 7).getPieceView());
    }

    @Test
    public void testOutsideCell() {
        CellView cell = boardView.getCellAt(-1, -1);
        assertNull(cell);
    }
}
