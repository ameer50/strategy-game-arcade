package ooga.board;

import javafx.util.Pair;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class OthelloBoard extends Board implements Serializable {

    private Map<Point2D, List<Point2D>> moveToPieceTrailMap;
    private static final List<Pair<Integer, Integer>> DELTA_PAIR = List.of(new Pair<>(-1, -1), new Pair<>(-1, 0), new Pair<>(-1, 1), new Pair<>(0, 1),
            new Pair<>(1, 1), new Pair<>(1, 0), new Pair<>(1, -1), new Pair<>(0, -1));
    private boolean turn;

    public OthelloBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String, String> pieces, Map<String, Integer> pieceScores) {
        super(settings, locations, pieces, pieceScores);
        moveToPieceTrailMap = new HashMap<>();
        turn = false;
    }

    @Override
    public String checkWon() {
        String winner;
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                winner = checkAllDirections(i, j);
//                if (winner.length() > 0) return winner;
//            }
//        }
//        if (this.isFull()){
//            return "Tie";
//        }
        return null;
    }


    private List<Point2D> checkAllDirections(Point2D coordinate) {
        List<Point2D> possibleMoves = new ArrayList<>();
        for (Pair<Integer, Integer> pair: DELTA_PAIR) {
            int deltaX = pair.getKey();
            int deltaY = pair.getValue();
            Point2D possibleMove = check(coordinate, deltaX, deltaY, true, new ArrayList<>());
            if (possibleMove != null){
                possibleMoves.add(possibleMove);
            }
        }
        return possibleMoves;
    }

    private boolean piecesMatch(int x1, int y1, int x2, int y2) {
        return getPieceAt(x1, y1).getColor().equals(getPieceAt(x2, y2).getColor());
    }

    private Point2D check(Point2D point, int deltaX, int deltaY, boolean firstPass, List<Point2D> pieceTrail) {
        int i = (int) point.getX();
        int j = (int) point.getY();

        if (firstPass) {
            if (!checkFirstNeighborValid(point, deltaX, deltaY)) return null;
        } else {
            pieceTrail.add(point);

            // exit condition, if the surrounding cell is empty, then that cell is a valid move, and all pieces to
            // the right of it are added to the pieceTrail
            // Thus, the valid move and its pieceTrail are added to the map
            if (isCellInBounds(i + deltaX, j + deltaY) && getPieceAt(i + deltaX, j + deltaY) == null) {
                Point2D validMove = new Point2D.Double(i + deltaX, j + deltaY);
                addToMap(validMove, pieceTrail);
                return validMove;
            }

            // if neighboring cell has no piece, then it is no longer a valid move since it is checked for above
            // if neighboring colors match, no move is possible
            if (!piecesMatch(i, j, i + deltaX, j + deltaY)) {
                return null;
            }
        }

        Point2D nextLoc = new Point2D.Double(i + deltaX, j + deltaY);
        return check(nextLoc, deltaX, deltaY, false, pieceTrail);
    }

    private boolean checkFirstNeighborValid(Point2D point, int deltaX, int deltaY) {
        int i = (int) point.getX();
        int j = (int) point.getY();
        if (getPieceAt(i, j) == null || !isCellInBounds(i + deltaX, j + deltaY) || getPieceAt(i + deltaX, j + deltaY) == null) return false;

        return !piecesMatch(i, j, i + deltaX, j + deltaY);
    }

    @Override
    public void doMove(Move move) {
        if (!move.isUndo()) {
            List<Point2D> pieceTrail = moveToPieceTrailMap.getOrDefault(move.getEndLocation(), new ArrayList<>());
            //move.setPromote(true);

            Piece piece = new Piece("Coin", "", 1, move.getColor());
            move.setPiece(piece);
            pieceBiMap.forcePut(move.getEndLocation(), piece);
            move.setPieceGenerated(true);

            for (Point2D point: pieceTrail) {
                Piece oldPiece = getPieceAt(point);
                Piece trailPiece = new Piece(oldPiece.getType(), oldPiece.getMovePattern(),  oldPiece.getValue(), move.getColor());
                move.addConvertedPiece(new Pair(oldPiece, trailPiece), point);

            }

//            for(Point2D point: move.getConvertedPiecesAndLocations().keySet()){
//                Piece oldPiece = getPieceAt(point);
//                Piece trailPiece = new Piece(oldPiece.getType(), oldPiece.getMovePattern(),  oldPiece.getValue(), move.getColor());
//                move.addConvertedPiece(new Pair(oldPiece, trailPiece), point);
//            }

        } else {
            putPieceAt(move.getStartLocation(), null);
        }

        moveToPieceTrailMap.clear();
    }

    @Override
    public List<Point2D> getValidMoves(Point2D coordinate) {

        List<Point2D> validMoves = new ArrayList<>();
       String clickedPieceColor = "White";

        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();

        if(!isCellInBounds(coordinate) && x == width && (y == 0 || y == 1)){
            clickedPieceColor = (y == 0) ? "White": "Black";
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Piece currPiece = getPieceAt(i,j);
                if(currPiece != null && currPiece.getColor().equals(clickedPieceColor)){
                    List<Point2D> possibleValidMoves = checkAllDirections(new Point2D.Double(i,j));
                    validMoves.addAll(possibleValidMoves);
                }
            }
        }

        validMoves = removeDuplicates(validMoves);
        return validMoves;


    }

    private void addToMap(Point2D validMove, List<Point2D> pieceTrail){
        if(!moveToPieceTrailMap.containsKey(validMove)){
            moveToPieceTrailMap.put(validMove, pieceTrail);
        }else{
            moveToPieceTrailMap.get(validMove).addAll(pieceTrail);
        }
    }

    private List<Point2D> removeDuplicates(List<Point2D> validMoves){
        Set<Point2D> set = new LinkedHashSet<>();

        set.addAll(validMoves);
        validMoves.clear();
        validMoves.addAll(set);

        return validMoves;
    }
}
