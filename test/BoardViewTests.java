import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.history.Move;
import ooga.view.*;
import ooga.xml.XMLProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import java.awt.geom.Point2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardViewTests extends DukeApplicationTest {

    /*Board board;
    BoardView boardView;
    Stage stage;


    @BeforeEach
    public void setUp() throws Exception {
        String gameXML = String.format("resources/Chess/chessJUnit.xml");
        XMLProcessor processor = new XMLProcessor();
        processor.parse(gameXML);
        //board = new ChessBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
        boardView = new BoardView(8,8, processor.getInitialPieceLocations());


    }

    @Test
    public void testBoardDimensions() throws Exception {
        int height = boardView.getNumRows();
        int width = boardView.getNumCols();
        assertEquals(height, 8);
        assertEquals(width, 8);
        tearDown();
    }

    @Test
    public void testValidPieceName() throws Exception {
        String pieceName = boardView.getCellAt(7,7).getPieceView().getPieceName();
        assertEquals(pieceName, "White_Rook");
        tearDown();
    }

    @Test
    public void testHighlightValidMoves() throws Exception {
        List<Point2D> validMoves = board.getValidMoves(new Point2D.Double(6,7));
        boardView.highlightValidMoves(validMoves);

        assertEquals(true, boardView.getCellAt(5,7).isHasYellowBorder());
        assertEquals(true, boardView.getCellAt(4,7).isHasYellowBorder());
        tearDown();
    }

    @Test
    public void testToggleRed() throws Exception {
        Point2D point = new Point2D.Double(6,7);
        boardView.setSelectedLocation(point);
        assertEquals(true, boardView.getCellAt(point).isHasRedBorder());

        tearDown();
    }

    @Test
    public void testdoMove() throws Exception {
        PieceView currPiece = boardView.getCellAt(6,7).getPieceView();
        Move move = new Move(new Point2D.Double(6,7), new Point2D.Double(5,7));
        boardView.doMove(move);
        assertEquals(null ,boardView.getCellAt(6,7).getPieceView());
        assertEquals(currPiece, boardView.getCellAt(5,7).getPieceView());
        tearDown();
    }

    @Test
    public void testToggleNoBorder() throws Exception {
        boardView.getCellAt(6,7).toggleNoBorder();

        assertEquals(false, boardView.getCellAt(6,7).isHasRedBorder());
        assertEquals(false, boardView.getCellAt(6,7).isHasYellowBorder());
    }

    @Test
    public void testSetPiece() throws Exception {

        assertEquals(null, boardView.getCellAt(5,7).getPieceView());
        PieceView piece = new PieceView("White_Pawn");
        boardView.getCellAt(5,7).setPieceView(piece);
        assertEquals(piece, boardView.getCellAt(5,7).getPieceView());
    }

    @Test
    public void testOutsideCell() throws Exception {

        CellView cell = boardView.getCellAt(-1, -1);
        assertNull(cell);
    }*/


}
