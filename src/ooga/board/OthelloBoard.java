package ooga.board;

import javafx.util.Pair;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class OthelloBoard extends Board implements Serializable {

    private static final int PIECES_NEEDED = 3;
    private Map<Point2D, List<Point2D>> moveToPieceTrailMap;
    private boolean turn;

    public OthelloBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String,
            Pair<String, Integer>> pieces) {
        super(settings, locations, pieces);
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

        List<Pair> deltaPair = List.of(new Pair(-1, -1), new Pair(-1, 0), new Pair(-1, 1), new Pair(0, 1),
                new Pair(1, 1), new Pair(1, 0), new Pair(1, -1), new Pair(0, -1));

        for(Pair pair: deltaPair){
            int deltaX = (int) pair.getKey();
            int deltaY = (int) pair.getValue();
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
        int i =  (int) point.getX();
        int j = (int) point.getY();

        if(!firstPass){
            pieceTrail.add(point);
        }


        if (!isCellInBounds(i + deltaX, j + deltaY) || checkStartCell(point, firstPass, deltaX, deltaY)) return null;

        // exit condition, if the surrounding cell is empty, then that cell is a valid move, and all pieces to
        // the right of it are added to the pieceTrail
        // Thus, the valid move and its pieceTrail are added to the map
        if(isCellInBounds(i + deltaX, j + deltaY) && getPieceAt(i + deltaX, j + deltaY) == null){
            Point2D validMove = new Point2D.Double(i + deltaX, j + deltaY);
            addToMap(validMove, pieceTrail);
            return validMove;
        }

        // if neighboring cell has no piece, then it is no longer a valid move since it is checked for above
        // if neighboring colors match, no move is possible
        System.out.println("coordinate " + i + deltaX + " " + j + deltaY);
        if (!firstPass && !piecesMatch(i, j, i + deltaX, j + deltaY)){
            return null;
        }

        Point2D nextLoc = new Point2D.Double(i + deltaX, j + deltaY);
        return check(nextLoc, deltaX, deltaY, false, pieceTrail);
    }

    private boolean checkStartCell(Point2D point, boolean firstPass, int deltaX, int deltaY) {
        if(firstPass){
            int i =  (int) point.getX();
            int j = (int) point.getY();
            if (getPieceAt(i, j) == null || getPieceAt(i + deltaX, j + deltaY) == null) return true;

            if (piecesMatch(i, j, i +deltaX, j +deltaY)) return true;
        }

        return false;
    }


    @Override
    public void doMove(Move move) {
        if(!move.isUndo()){

            List<Point2D> pieceTrail = moveToPieceTrailMap.get(move.getEndLocation());
            System.out.println("the piece trail " + pieceTrail);



            // the first piece in a piece trail is always the original piece that begins the trail
            // it is this piece's color (i.e. full name) which we wish to convert the other pieces to
            //move.setConvertPieceName(getPieceAt(pieceTrail.get(0)).getFullName());
            //pieceTrail.add(move.getEndLocation()); // place a piece in the valid move location at the beginning since it
            // contains the color that the rest of the images must convert to
            for(Point2D point: pieceTrail){
                //Piece piece = new Piece("Coin", "", 1, move.getColor());
                getPieceAt(point).setColor(move.getColor());
                //pieceBiMap.forcePut(point, piece);
                Piece piece = getPieceAt(point);
                move.addConvertedPiece(piece, point);
            }

            Piece piece = new Piece("Coin", "", 1, move.getColor());
            move.setPiece(piece);
            pieceBiMap.forcePut(move.getEndLocation(), piece);
            move.setPieceGenerated(true);
        }else{
            putPieceAt(move.getStartLocation(), null);
        }
        moveToPieceTrailMap.clear();

    }

    @Override
    public List<Point2D> getValidMoves(Point2D coordinate) {

        List<Point2D> validMoves = new ArrayList<>();
       //String clickedPieceColor = getPieceAt(coordinate).getColor();

        // TODO: Fix this problem
        String clickedPieceColor;
        if (turn){
            clickedPieceColor = "Black";
        }else{
            clickedPieceColor = "White";
        }

        turn = !turn;

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
