package ooga.board;

import java.io.Serializable;
import javafx.util.Pair;
import ooga.history.Move;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CheckersBoard extends Board implements Serializable {


    public List<Point2D> validKillMoves = new ArrayList<Point2D>();
    public List<Point2D> validNonKillMoves = new ArrayList<Point2D>();
    public Map<Point2D, List<Point2D>> killPaths = new HashMap<Point2D, List<Point2D>>();


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
            return null;
        }
        String color = piece.getColor();
        String movPat = piece.getMovePattern();
        validKillMoves.clear();
        validNonKillMoves.clear();
        killPaths.clear();
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
        System.out.println("KILL PATHS: " + killPaths);
        //System.out.println("KILL PATHS CLEANED: " + killPathsCleaner(killPaths, x, y));
        //killPaths = killPathsCleaner(killPaths, x, y);
        validNonKillMoves.addAll(validKillMoves);
        return validNonKillMoves;
    }

    public Map<Point2D, List<Point2D>> killPathsCleaner(Map<Point2D, List<Point2D>> killPaths, int x, int y){
        Map<Point2D, List<Point2D>> ret = new HashMap<Point2D, List<Point2D>>();
        List<Point2D> killedPieces = new ArrayList<Point2D>();

        for(Point2D pd: killPaths.keySet()){
            killedPieces.add(pd);
        }
        System.out.println("Killed Pieces: " + killedPieces);

        List<Point2D> jumpLocs = new ArrayList<Point2D>();

        for(List<Point2D> lp: killPaths.values()){
            jumpLocs.add(lp.get(lp.size()-1));
        }
        System.out.println("Jump Locs: " + jumpLocs);
        List<Point2D> temp = new ArrayList<Point2D>();
        for(int i = 0; i<jumpLocs.size(); i++){
            temp.add(killedPieces.get(i));
            ret.put(jumpLocs.get(i), new ArrayList<Point2D>(temp));
        }
        for(Point2D p: ret.keySet()){
            int size = ret.get(p).size();
            double dist = distance(x, y, (int)p.getX(), (int)p.getY());
            if(dist>8.0 && size>3){
                ret.get(p).remove(0);
            }
            else if(dist>5.0 && size>2){
                ret.get(p).remove(0);
            }
            else if(dist>2.0 && size>1){
                ret.get(p).remove(0);
            }
        }
        return ret;
    }

    public void doMove(Move m) {
        int x_i = (int) m.getStartLocation().getX();
        int y_i = (int) m.getStartLocation().getY();
        int x_f = (int) m.getEndLocation().getX();
        int y_f = (int) m.getEndLocation().getY();
        // System.out.println("Initial: " + x_i + "Initial: " + y_i);
        // System.out.println("Final: " + x_f + "Final: " + y_f);
        Piece currPiece = getPieceAt(x_i, y_i);
        placePiece(x_i, y_i, null);
        placePiece(x_f, y_f, currPiece);
        boolean isKill;
        Point2D.Double capLoc = null;
        Piece hitPiece = null;

        m.setPiece(currPiece);
        pieceBiMap.forcePut(new Point2D.Double(x_f, y_f), currPiece);

        int score = (hitPiece == null) ? 0 : hitPiece.getValue();

        if (killPaths.containsKey(m.getEndLocation())) {
            for (Point2D point : killPaths.get(m.getEndLocation())) {
                System.out.println("inside redo");
                m.addCapturedPiece(getPieceAt(point), point);
                removePiece((int) point.getX(), (int) point.getY());
            }
        }

        for (Point2D location : m.getCapturedPiecesAndLocations().values()) {
            this.captureAction.process((int) location.getX(), (int) location.getY());
        }

        if (m.isPromote() && m.isUndo()) {
            // demote backend
            getPieceAt(x_f, y_f).setType("Coin");
            getPieceAt(x_f, y_f).setMovePattern((currPiece.getColor().equals("White") ? "P2 1" : "P1 1"));
            // demote frontend
            promoteAction.process(x_f, y_f);
        }
        /* TODO: check if piece has reached opposite end */
        if((getPieceAt(x_f, y_f).getColor().equals(bottomColor) && x_f==0) || (!(getPieceAt(x_f, y_f).getColor().equals(bottomColor)) && x_f==height-1)){
            getPieceAt(x_f, y_f).setType("Monarch");
            getPieceAt(x_f, y_f).setMovePattern("KING 1");
            //pieceBiMap.forcePut(new Point2D.Double(x_f, y_f), new Piece("Monarch", "KING 1", 10, init_Color, initID));
            m.setPromote(true);
            promoteAction.process(x_f, y_f);
        }
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
        if (!killConditions) {
            return false;
        } else {
            validKillMoves.add(new Point2D.Double(x-2, y-2));
            Point2D p = new Point2D.Double(x-2, y-2);
            if (!killPaths.containsKey(p)) {
                List<Point2D> temp = new ArrayList<Point2D>();
                if(killPaths.size()>0){
                    Collection<List<Point2D>> values = killPaths.values();
                    ArrayList<List<Point2D>> listVals = new ArrayList<List<Point2D>>(values);
                    temp.addAll(listVals.get(listVals.size()-1));
                }
                temp.add(new Point2D.Double(x-1, y-1));
                killPaths.put(p, temp);

            } else if (killPaths.containsKey(p)) {
                killPaths.get(p).add(new Point2D.Double(x-1, y-1));
            }
            return true;
        }
    }

    public boolean up_right(int x, int y) {
        if (isCellInBounds(x-1, y+1) && (getPieceAt(x-1, y+1)==null)) {
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
            Point2D p = new Point2D.Double(x-2, y+2);
            if (!killPaths.containsKey(p)) {
                List<Point2D> temp = new ArrayList<Point2D>();
                if(killPaths.size()>0){
                    Collection<List<Point2D>> values = killPaths.values();
                    ArrayList<List<Point2D>> listVals = new ArrayList<List<Point2D>>(values);
                    temp.addAll(listVals.get(listVals.size()-1));
                }
                temp.add(new Point2D.Double(x-1, y+1));
                killPaths.put(p, temp);

            } else if (killPaths.containsKey(p)) {
                killPaths.get(p).add(new Point2D.Double(x-1, y+1));
            }
            return true;
        }
    }

    public boolean down_left (int x, int y) {
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
            Point2D p = new Point2D.Double(x+2, y-2);
            if(!killPaths.containsKey(p)){
                List<Point2D> temp = new ArrayList<Point2D>();
                if(killPaths.size()>0){
                    Collection<List<Point2D>> values = killPaths.values();
                    ArrayList<List<Point2D>> listVals = new ArrayList<List<Point2D>>(values);
                    temp.addAll(listVals.get(listVals.size()-1));
                }
                temp.add(new Point2D.Double(x+1, y-1));
                killPaths.put(p, temp);

            } else if (killPaths.containsKey(p)) {
                killPaths.get(p).add(new Point2D.Double(x+1, y-1));
            }
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
            Point2D p = new Point2D.Double(x+2, y+2);
            if (!killPaths.containsKey(p)) {
                List<Point2D> temp = new ArrayList<Point2D>();
                if(killPaths.size()>0){
                    Collection<List<Point2D>> values = killPaths.values();
                    ArrayList<List<Point2D>> listVals = new ArrayList<List<Point2D>>(values);
                    temp.addAll(listVals.get(listVals.size()-1));
                }
                temp.add(new Point2D.Double(x+1, y+1));
                killPaths.put(p, temp);

            } else if (killPaths.containsKey(p)) {
                killPaths.get(p).add(new Point2D.Double(x+1, y+1));
            }
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
        down_right(x, y);
        down_left_kill(x, y);
        down_right_kill(x, y);

        int sizeDiff = -1;
        while(sizeDiff != 0){
            int before_size = validKillMoves.size();
            for(int k = 0; k<validKillMoves.size(); k++){
                Point2D point = validKillMoves.get(k);
                int i = (int)point.getX();
                int j = (int)point.getY();
                down_left_kill(i, j);
                down_right_kill(i, j);
            }
            List<Point2D> newList = validKillMoves.stream().distinct().collect(Collectors.toList());
            validKillMoves.clear();
            for(Point2D p : newList) {
                validKillMoves.add((Point2D) p.clone());
            }
            sizeDiff = validKillMoves.size()-before_size;
        }
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
            for(int k = 0; k<validKillMoves.size(); k++){
                Point2D point = validKillMoves.get(k);
                int i = (int)point.getX();
                int j = (int)point.getY();
                up_left_kill(i, j);
                up_right_kill(i, j);
                down_left_kill(i, j);
                down_right_kill(i, j);
            }
            List<Point2D> newList = validKillMoves.stream().distinct().collect(Collectors.toList());
            validKillMoves.clear();
            for(Point2D p : newList) {
                validKillMoves.add((Point2D) p.clone());
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
        /* Piece piece = getPieceAt(i, j);
        pieceColorMap.get(piece.getColor()).remove(piece);
        pieceLocationBiMap.forcePut(new Point2D.Double(i, j), null) ;*/
        putPieceAt(new Point2D.Double(i, j), null);
    }
}
