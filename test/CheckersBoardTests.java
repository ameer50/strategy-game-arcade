import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        board.setOnPieceCaptured((int toX, int toY) -> {
            //boardView.getCellAt(toX, toY).setPiece(null);
        });

        board.setOnPiecePromoted((int toX, int toY) -> {
            //board.getPieceAt(toX, toY);
            //boardView.getCellAt(toX, toY).setPiece(new PieceView(board.getPieceAt(toX, toY).getFullName()));
        });
    }

    @Test
    public void testSelectedPiecePos(){
        Piece white = board.getPieceAt(6, 0);
        Piece black = board.getPieceAt(0, 0);
        assertEquals(white.getColor(), "White");
        assertEquals(black.toString(), "Black");
    }

    @Test
    public void testSelectedPieceNull(){
        Piece nul = board.getPieceAt(4, 4);
        assertNull(nul);
    }

    @Test
    public void testWhiteMove(){
        Piece nul = board.getPieceAt(4, 4);
        assertNull(nul);
    }




}