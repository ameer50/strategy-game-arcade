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
        winner.append(checkUpLeft(i, j, PIECES_NEEDED));
        winner.append(checkUp(i, j, PIECES_NEEDED));
        winner.append(checkUpRight(i, j, PIECES_NEEDED));
        winner.append(checkRight(i, j, PIECES_NEEDED));
        winner.append(checkDownRight(i, j, PIECES_NEEDED));
        winner.append(checkDown(i, j, PIECES_NEEDED));
        winner.append(checkDownLeft(i, j, PIECES_NEEDED));
        winner.append(checkLeft(i, j, PIECES_NEEDED));
        return winner.toString();
    }

    private boolean piecesMatch(int x1, int y1, int x2, int y2) {
        return getPieceAt(x1, y1).getColor().equals(getPieceAt(x2, y2).getColor());
    }

    private String checkLeft(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i, j - 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i, j - 1) == null) return "";
        if (!piecesMatch(i, j, i, j - 1)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();
        return checkLeft(i, j - 1, piecesNeeded - 1);
    }

    private String checkDownLeft(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i + 1, j - 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i + 1, j - 1) == null) return "";
        if (!piecesMatch(i, j, i + 1, j - 1)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkDownLeft(i + 1, j - 1, piecesNeeded - 1);
    }

    private String checkDown(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i + 1, j)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i + 1, j) == null) return "";
        if (!piecesMatch(i, j, i + 1, j)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkDown(i + 1, j, piecesNeeded - 1);
    }

    private String checkDownRight(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i + 1, j + 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i + 1, j + 1) == null) return "";
        if (!piecesMatch(i, j, i + 1, j + 1)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkDownRight(i + 1, j + 1, piecesNeeded - 1);

    }

    private String checkUp(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i - 1, j)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i - 1, j) == null) return "";
        if (!piecesMatch(i, j, i - 1, j)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkUp(i - 1, j, piecesNeeded - 1);
    }

    private String checkRight(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i, j + 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i, j + 1) == null) return "";
        if (!piecesMatch(i, j, i, j + 1)) return "";
        if (piecesNeeded == 1) {
            System.out.println("reached " + getPieceAt(i,j).getColor());
            return getPieceAt(i, j).getColor();
        }

        return checkRight(i, j + 1, piecesNeeded - 1);
    }

    private String checkUpRight(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i - 1, j + 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i - 1, j + 1) == null) return "";
        if (!piecesMatch(i, j, i - 1, j + 1)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkUpRight(i - 1, j + 1, piecesNeeded - 1);

    }

    private String checkUpLeft(int i, int j, int piecesNeeded) {
        if (!isCellInBounds(i - 1, j - 1)) return "";
        if (getPieceAt(i, j) == null || getPieceAt(i - 1, j - 1) == null) return "";
        if (!piecesMatch(i, j, i - 1, j - 1)) return "";
        if (piecesNeeded == 1) return getPieceAt(i, j).getColor();;
        return checkUpLeft(i - 1, j - 1, piecesNeeded - 1);
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
