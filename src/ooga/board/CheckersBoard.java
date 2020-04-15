package ooga.board;

import java.io.Serializable;
import javafx.util.Pair;
import ooga.history.Move;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckersBoard extends Board implements Serializable {


    public List<Point2D> validKillMoves = new ArrayList<Point2D>();
    public List<Point2D> validNonKillMoves = new ArrayList<Point2D>();


    public CheckersBoard(Map<String, String> settings, Map<Point2D, String> locs, Map<String, Pair<String, Integer>> pieces){
        super(settings, locs, pieces);
    }

    @Override
    public String checkWon() {
        String result = checkOneColor();
        String result2 = checkTrapped();

        if(result!=null){
            return result;
        }
        else if(result2!= null){
            return result2;
        }
        return null;
    }

    public String checkOneColor(){
        int numWhite = 0;
        int numBlack = 0;
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                if(getPieceAt(i, j) != null){
                    if(getPieceAt(i,j).getColor().equals("White")){
                        numWhite++;
                    }
                    else if(getPieceAt(i,j).getColor().equals("Black")){
                        numBlack++;
                    }
                }
            }
        }
        if(numBlack==0){
            return "White";
        }
        else if(numWhite==0){
            return "Black";
        }
        return null;
    }

    public String checkTrapped(){
        int numWhite = 0;
        int numBlack = 0;
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                if(getPieceAt(i, j) != null){
                    List<Point2D> temp = getValidMoves(i, j);
                    if(getPieceAt(i,j).getColor().equals("White")){
                        numWhite+=temp.size();
                    }
                    else if(getPieceAt(i,j).getColor().equals("Black")){
                        numBlack+=temp.size();
                    }
                }
            }
        }
        if(numBlack==0){
            return "White";
        }
        else if(numWhite==0){
            return "Black";
        }
        return null;
    }

    @Override
    public List<Point2D> getValidMoves(int x, int y) {
        Piece piece = getPieceAt(x,y);
        if (piece == null) {
            System.out.println("COLOR: " + piece.getColor());
            return null;
        }
        String color = piece.getColor();
        if (!pieceColorMap.get(color).contains(piece)) {
            return null;
        }

        String movPat = piece.getMovePattern();
        validKillMoves.clear();
        validNonKillMoves.clear();
        if (movPat.equals("P1 1")) {
            //Employ upper methods
            p1(x,y);
        }
        else if (movPat.equals("P2 1")) {
            //Employ down methods
            p2(x,y);
        } else if (movPat.equals("KING 1")) {
            //Employ king methods (ALL)
            king(x, y);
        } else {
            return null;
        }
        System.out.println("ENDD VNK: " + validNonKillMoves);
        System.out.println("ENDD VK: " + validKillMoves);
        validNonKillMoves.addAll(validKillMoves);
        System.out.println("FINAL ENDD VNK: " + validNonKillMoves);
        return validNonKillMoves;
        /* if (piece == null) { return null; }
        System.out.println("Problem color " + color + " |  Problem piece" + piece + " | X, Y " + x + " " + y);
        System.out.println("problem map " + pieceColorMap);
        if (pieceColorMape.get(color).contains(piece)) {
            validMoves = new ArrayList<Point2D>();
            //checkRight(x, y, piece);
            //checkLeft(x, y, piece);
         } */
    }

    public int doMove(Move m) {
        int x_i = (int) m.getStartLocation().getX();
        int y_i = (int) m.getStartLocation().getY();
        int x_f = (int) m.getEndLocation().getX();
        int y_f = (int) m.getEndLocation().getY();
        System.out.println("Initial: " + x_i + "Initial: " + y_i);
        String init_Color = getPieceAt(x_i, y_i).getColor();
        System.out.println("Final: " + x_f + "Final: " + y_f);
        Piece currPiece = getPieceAt(x_i, y_i);
        Piece oppPiece = getPieceAt(x_f, y_f);
        placePiece(x_i, y_i, null);
        placePiece(x_f, y_f, currPiece);
        boolean isKill = false;
        Point2D.Double capLoc = null;
        Piece hitPiece = null;
        System.out.println("Distance: " + distance(x_i,y_i,x_f,y_f));

        if (distance(x_i,y_i,x_f,y_f)>2.0) {
            System.out.println("X to be removed: " + Math.abs(x_f+x_i)/2 + "  Y To be removed: " + Math.abs(y_f+y_i)/2);
            capLoc = new Point2D.Double(Math.abs(x_f+x_i)/2, Math.abs(y_f+y_i)/2);
            hitPiece = getPieceAt(capLoc);
            removePiece(Math.abs(x_f+x_i)/2, Math.abs(y_f+y_i)/2);
            //placePiece(Math.abs(x_f+x_i)/2, Math.abs(y_f+y_i)/2, null);
        }

        m.setPiece(currPiece);
        m.addCapturedPieceAndLocation(hitPiece, capLoc);
        pieceBiMap.forcePut(new Point2D.Double(x_f, y_f), currPiece);

        int score = 0;
        if(hitPiece != null) {
            score =  hitPiece.getValue();
        }

        //TO-DO check if piece has reached opposite end
        if((getPieceAt(x_f, y_f).getColor().equals(bottomColor) && x_f==0) || (!(getPieceAt(x_f, y_f).getColor().equals(bottomColor)) && x_f==height-1)){
            pieceLocationBiMap.forcePut(new Point2D.Double(x_f, y_f), new Piece(init_Color+"_Monarch", "KING 1", 10, init_Color));
            promoteAction.process(x_f, y_f);
        }
        
        return score;
    }

    /* START: Eight elements that make up the three possible move patterns of the pieces in the game. */
    public boolean up_left (int x, int y) {
        if (isCellInBounds(x - 1, y - 1) && (getPieceAt(x - 1, y - 1) == null)) {
            validNonKillMoves.add(new Point2D.Double(x - 1, y - 1));
            return true;
        }
        return false;
    }

    public boolean up_left_kill(int x, int y){
        Piece temp1 = getPieceAt(x-1, y-1);
        Piece temp2 = getPieceAt(x-2, y-2);
        boolean killConditions = isCellInBounds(x-1, y-1) && isCellInBounds(x-2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return false;
        }
        else{
            validKillMoves.add(new Point2D.Double(x-2, y-2));
            return true;
        }
    }

    public boolean up_right(int x, int y) {
        if(isCellInBounds(x-1, y+1) && (getPieceAt(x-1, y+1)==null)){
            validNonKillMoves.add(new Point2D.Double(x-1, y+1));
            return true;
        }
        return false;
    }

    public boolean up_right_kill(int x, int y) {
        Piece temp1 = getPieceAt(x-1, y+1);
        Piece temp2 = getPieceAt(x-2, y+2);
        boolean killConditions = isCellInBounds(x-1, y+1) && isCellInBounds(x-2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if (!killConditions) {
            return false;
        } else {
            validKillMoves.add(new Point2D.Double(x-2, y+2));
            return true;
        }
    }

    public boolean down_left(int x, int y){
        if (isCellInBounds(x+1, y-1) && (getPieceAt(x+1, y-1)==null)) {
            validNonKillMoves.add(new Point2D.Double(x+1, y-1));
            return true;
        }
        return false;
    }

    public boolean down_left_kill(int x, int y) {
        Piece temp1 = getPieceAt(x+1, y-1);
        Piece temp2 = getPieceAt(x+2, y-2);
        boolean killConditions = isCellInBounds(x+1, y-1) && isCellInBounds(x+2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if (!killConditions) {
            return false;
        } else {
            validKillMoves.add(new Point2D.Double(x+2, y-2));
            return true;
        }
    }

    public boolean down_right(int x, int y){
        if (isCellInBounds(x+1, y+1) && (getPieceAt(x+1, y+1)==null)) {
            validNonKillMoves.add(new Point2D.Double(x+1, y+1));
            return true;
        }
        return false;
    }

    public boolean down_right_kill (int x, int y) {
        Piece temp1 = getPieceAt(x+1, y+1);
        Piece temp2 = getPieceAt(x+2, y+2);
        boolean killConditions = isCellInBounds(x+1, y+1) && isCellInBounds(x+2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if (!killConditions) {
            return false;
        } else {
            validKillMoves.add(new Point2D.Double(x+2, y+2));
            return true;
        }
    }

    /* ***END: Eight elements that make up the three possible move patterns of the pieces in the game.***
    START: Three possible move patterns, one for player 1 (black checkers), one for player 2 (red checkers),
    and one for king checkers. */
    private void p1(int x, int y){
        up_left(x, y);
        up_right(x, y);
        up_left_kill(x, y);
        up_right_kill(x, y);

        int sizeDiff = -1;

        while(sizeDiff != 0){
            int before_size = validKillMoves.size();
            System.out.println("Before loop VKM: " + validKillMoves);
            for(int k = 0; k<validKillMoves.size(); k++){
                Point2D point = validKillMoves.get(k);
                int i = (int)point.getX();
                int j = (int)point.getY();
                up_left_kill(i, j);
                up_right_kill(i, j);
            }
            List<Point2D> newList = validKillMoves.stream().distinct().collect(Collectors.toList());
            validKillMoves.clear();
            for(Point2D p : newList) {
                validKillMoves.add((Point2D) p.clone());
            }

            sizeDiff = validKillMoves.size()-before_size;
        }
    }

    private void p2(int x, int y){
        down_left(x, y);
        System.out.println("VNK: " + validNonKillMoves);
        down_right(x, y);
        System.out.println("VNK: " + validNonKillMoves);
        down_left_kill(x, y);
        System.out.println("VK: " + validKillMoves);
        down_right_kill(x, y);
        System.out.println("VK: " + validKillMoves);

        int sizeDiff = -1;
        while(sizeDiff != 0){
            int before_size = validKillMoves.size();
            for (Point2D point: validKillMoves) {
                int i = (int)point.getX();
                int j = (int)point.getY();
                down_left_kill(i, j);
                down_right_kill(i, j);
            }
            sizeDiff = validKillMoves.size()-before_size;
        }
        System.out.println("END VNK: " + validNonKillMoves);
        System.out.println("END VK: " + validKillMoves);
    }

    private void king(int x, int y) {
        up_left(x, y);
        up_right(x, y);
        up_left_kill(x, y);
        up_right_kill(x, y);
        down_left(x, y);
        down_right(x, y);
        down_left_kill(x, y);
        down_right_kill(x, y);

        int sizeDiff = -1;
        while(sizeDiff != 0) {
            int before_size = validKillMoves.size();
            for(Point2D point: validKillMoves){
                int i = (int)point.getX();
                int j = (int)point.getY();
                up_left_kill(i, j);
                up_right_kill(i, j);
                down_left_kill(i, j);
                down_right_kill(i, j);
            }
            sizeDiff = validKillMoves.size()-before_size;
        }
    }

    public boolean isOppColor(Piece currPiece, Piece oppPiece) {
        if(currPiece==null || oppPiece==null){
            return true;
        }
        else{
            return !(oppPiece.getColor().equals(currPiece.getColor()));
        }
    }

    public double distance(int x_i, int y_i, int x_f, int y_f) {
        return Math.sqrt(Math.pow(x_f-x_i, 2)+Math.pow(y_f-y_i, 2));
    }

    private void removePiece(int i, int j) {
        /*Piece piece = getPieceAt(i, j);
        pieceColorMap.get(piece.getColor()).remove(piece);
        pieceLocationBiMap.forcePut(new Point2D.Double(i, j), null);*/
        putPieceAt(new Point2D.Double(i, j), null);
    }
}
