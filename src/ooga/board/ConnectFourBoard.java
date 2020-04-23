package ooga.board;

import javafx.util.Pair;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectFourBoard extends Board implements Serializable {

    private static final int PIECES_NEEDED = 3;
    private static final List<Pair<Integer, Integer>> DELTA_PAIR = List.of(new Pair<>(-1, -1), new Pair<>(-1, 0), new Pair<>(-1, 1), new Pair<>(0, 1),
            new Pair<>(1, 1), new Pair<>(1, 0), new Pair<>(1, -1), new Pair<>(0, -1));

    public ConnectFourBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
            Pair<String, Integer>> pieces) {
        super(settings, locations, pieces);
    }

    @Override
    public String checkWon() {
        String winner;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                winner = checkAllDirections(i, j);
                if (winner.length() > 0) return winner;
            }
        }
        if (this.isFull()){
            return "Tie";
        }
        return null;
    }

    private String checkAllDirections(int i, int j) {
        StringBuilder winner = new StringBuilder();
        // iterating through this array accessing every two elements, allows for checking of all eight locations
        // surrounding a cell

        for(Pair<Integer, Integer> pair: DELTA_PAIR){
            int deltaX = pair.getKey();
            int deltaY = pair.getValue();
            winner.append(check(i, j, deltaX, deltaY, PIECES_NEEDED));
        }
        return winner.toString();
    }

    private boolean piecesMatch(int x1, int y1, int x2, int y2) {
        return getPieceAt(x1, y1).getColor().equals(getPieceAt(x2, y2).getColor());
    }

    private String check(int i, int j, int deltaX, int deltaY, int piecesNeeded) {
        if (!isCellInBounds(i + deltaX, j + deltaY)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i + deltaX, j + deltaY) == null) return "";
        if (!piecesMatch(i, j, i + deltaX, j + deltaY)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();
        return check(i + deltaX, j + deltaY,deltaX, deltaY, piecesNeeded - 1);
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
    public List<Point2D> getValidMoves(Point2D coordinate) {

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
