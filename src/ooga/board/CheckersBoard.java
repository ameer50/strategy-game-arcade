package ooga.board;

import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckersBoard extends Board {


    public List<Point2D> validMoves;

    /*To-DO Items
    Two move types: Coin and King; Coin allows restricted diagonal movement, King allows diagonal movement in all directions.
    Both can only move one cell or two cells
    1) Add support for both colors Red and Black (Top color can only move down initially, bottom color can only move up initially)
    2) Check upper diagonal neighbors and lower diagonal neighbors (depending on top or bottom), if piece @ that cell is null,
    add to list of validMoves
    3) check upper two diagonal neighbors and lower two diagonal neighbors (depending on top or bottom), if piece @ that cell is
    null and the immediate diagonal neighbor is of the opposite piece color, allow for a valid move and remove opposite color piece
    4) If a piece is captured, skip the other player/CPU turn and allow player to go again. This avoids recursion and a need
    to change the piece's move type
    5) Once a piece reaches the first/last row of the board, change the movetype of the piece to 'King'
    */

    public CheckersBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Double>> pieces){
        super(settings, locs, pieces);
    }

    @Override
    public String checkWon() {
        int numWhite = 0;
        int numBlack = 0;

        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                if(getPieceAt(i,j).getColor().equals("White")){
                    numWhite++;
                }
                else if(getPieceAt(i,j).getColor().equals("Black")){
                    numBlack++;
                }
            }
        }

        if(numBlack==0 || numWhite==0){
            //return true;
        }
        //return false;
        return null;
    }

    @Override
    public List<Point2D> getValidMoves(int x, int y) {
        Piece piece = getPieceAt(x,y);
        if (piece == null) { return null; }
        String color = piece.getColor();
        if (pieceColorMap.get(color).contains(piece)) {
            validMoves = new ArrayList<Point2D>();
            checkRight(x, y, piece);
            checkLeft(x, y, piece);
            return validMoves;
        }
        return null;
    }

    public boolean checkRight(int x, int y, Piece currPiece) {
        if (!isCellInBounds(x + 1, y + 1) || !isCellInBounds(x + 2, y + 2)) {
            return false;
        }

        Piece temp = getPieceAt(x + 1, y + 1);

        if (!(temp.getColor().equals(currPiece.getColor())) && (getPieceAt(x + 2, y + 2) == null)) {
            validMoves.add(new Point2D.Double(x + 2, y + 2));
        }
        return true;
    }

    public boolean checkLeft(int x, int y, Piece currPiece){
        if(!isCellInBounds(x-1, y-1) || !isCellInBounds(x-2, y-2)){
            return false;
        }

        Piece temp = getPieceAt(x-1, y-1);

        if(!(temp.getColor().equals(currPiece.getColor())) && (getPieceAt(x-2, y-2) == null)){
            validMoves.add(new Point2D.Double(x-2, y-2));
        }

        return true;

    }

    public int doMove(int x_i, int y_i, int x_f, int y_f) {
        Piece currPiece = getPieceAt(x_i, y_i);
        Piece oppPiece = getPieceAt(x_f, y_f);
        placePiece(x_i, y_i, null);
        placePiece(x_f, y_f, currPiece);
        if(oppPiece == null){
            return 0;
        }
        else{
            return (int) oppPiece.getValue();
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
