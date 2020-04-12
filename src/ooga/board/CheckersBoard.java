package ooga.board;

import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckersBoard extends Board {


    public List<Point2D> validMoves;

    public CheckersBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
        super(settings, locs, pieces);
    }

    @Override
    public boolean checkWon() {
        int numWhite = 0;
        int numBlack = 0;

        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                if(getPieceAt(i,j).getColor().equals("white")){
                    numWhite++;
                }
                else if(getPieceAt(i,j).getColor().equals("black")){
                    numBlack++;
                }
            }
        }

        if(numBlack==0 || numWhite==0){
            return true;
        }
        return false;
    }

    @Override
    public List<Point2D> getValidMoves(int x, int y, String color) {
        Piece piece = getPieceAt(x,y);
        if (piece == null) { return null; }
        if (pieceColorMap.get(color).contains(piece)) {
            validMoves = new ArrayList<>();
            checkRight(x, y, piece);
            checkLeft(x, y, piece);
            return validMoves;
        }
        return null;
    }

    public boolean checkRight(int x, int y, Piece piece) {
        if (!isCellInBounds(x+1, y+1) || !isCellInBounds(x+2, y+2)) {
            return false;
        }
        Piece temp = getPieceAt(x+1, y+1);
        if (!(temp.getColor().equals(piece.getColor())) && (getPieceAt(x+2, y+2) == null)) {
            validMoves.add(new Point2D.Double(x+2, y+2));
        }
        return true;
    }

    public boolean checkLeft(int x, int y, Piece piece){
        if (!isCellInBounds(x-1, y-1) || !isCellInBounds(x-2, y-2)){
            return false;
        }
        Piece temp = getPieceAt(x-1, y-1);
        if (!(temp.getColor().equals(piece.getColor())) && (getPieceAt(x-2, y-2) == null)) {
            validMoves.add(new Point2D.Double(x-2, y-2));
        }
        return true;
    }

    public double doMove(int x_i, int y_i, int x_f, int y_f) {
        Piece currPiece = getPieceAt(x_i, y_i);
        Piece oppPiece = getPieceAt(x_f, y_f);
        placePiece(x_i, y_i, null);
        placePiece(x_f, y_f, currPiece);
        if(oppPiece == null){
            return 0;
        }
        else{
            return oppPiece.getValue();
        }
    }


    /*public boolean goLeft(int x, int y, Piece currPiece){
        if(!isValidCell(x-1, y-1) || !isValidCell(x-2, y-2)){
            return false;
        }
        Piece temp = getPieceAt(x-1, y-1);

        if(!(temp.getColor().equals(currPiece.getColor())) && (getPieceAt(x-2, y-2) == null)){
            validMoves.add(new Point2D.Double(x-2, y-2));
        }

        return goLeft(x-2, y-2, currPiece);

    }

    public boolean goRight(int x, int y, Piece currPiece){
        if(!isValidCell(x+1, y+1) || !isValidCell(x+2, y+2)){
            return false;
        }
        Piece temp = getPieceAt(x+1, y+1);

        if(!(temp.getColor().equals(currPiece.getColor())) && (getPieceAt(x+2, y+2) == null)){
            validMoves.add(new Point2D.Double(x+2, y+2));
        }

        return goRight(x+2, y+2, currPiece);

    }*/

    public boolean isOppColor(int x, int y, String currPieceColor){
        return currPieceColor.equals(getPieceAt(x,y).getColor());
    }

}
