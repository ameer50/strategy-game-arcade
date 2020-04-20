package ooga.board;

import javafx.util.Pair;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectFourBoard extends Board implements Serializable {


    public ConnectFourBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
            Pair<String, Integer>> pieces) {
        super(settings, locations, pieces);
    }


    @Override
    public String checkWon() {
        return null;
    }

    @Override
    public void doMove(Move move) {
        if(!move.isUndo()){
            Piece piece = new Piece("Coin", "", 1, move.getColor());
            move.setPiece(piece);
            pieceBiMap.forcePut(move.getEndLocation(), piece);
            move.setPieceGenerated(true);
        }else{
            putPieceAt(move.getStartLocation(), null);
        }

    }

    @Override
    public List<Point2D> getValidMoves(int i, int j) {

        List<Point2D> validMoves = new ArrayList<>();

        for (int x = 0; x < width; x++ ){
            for (int y = height - 1; y >=0; y --){
                if (getPieceAt(y,x) == null){
                    validMoves.add(new Point2D.Double(y, x));
                    break;
                }
            }
        }
        return validMoves;

    }
}
