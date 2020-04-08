package ooga.board;

import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class CheckersBoard extends Board {

    public CheckersBoard(Map<String, String> settings, Map<Point2D.Double, String> locs, Map<String, Pair<String, Double>> pieces){
        super(settings, locs, pieces);
    }

    @Override
    public boolean checkWon() {
        return false;
    }

    @Override
    public List<Point2D> getValidMoves(int x, int y) {
        Piece currPiece = getPieceAt(x,y);
        if(currPiece==null){return null;}



        return null;
    }

    @Override
    public double doMove(int startX, int startY, int endX, int endY) {
        return 0;
    }

}
