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
    public Map<Point2D, Set<Point2D>> killPaths = new HashMap<>();
    public Set<Point2D> tempKills = new HashSet<>();


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
                    List<Point2D> temp = getValidMoves(i, j, true);
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
        System.out.println("GET VALID MOVES OUTPUT:" + getValidMoves(x, y, true));
        return getValidMoves(x, y, true);
    }

    private List<Point2D> getValidMoves(int x, int y, boolean clear){
        Piece piece = getPieceAt(x,y);
        if (piece == null) {
            return null;
        }
        String movPat = piece.getMovePattern();
        validKillMoves.clear();
        validNonKillMoves.clear();
        if(clear){
            killPaths.clear();
            tempKills.clear();
        }
        switch (movPat) {
            case "P1 1":
                //Employ upper methods
                Set<Point2D> nonKills = p1NoKills(x, y);
                Set<Point2D> kills = p1Kills(x, y);
                kills.addAll(nonKills);
                System.out.println("KILL PATHS: " + killPaths);
                return new ArrayList<>(kills);
            case "P2 1":
                //Employ down methods
                nonKills = p2NoKills(x, y);
                kills = p2Kills(x, y);
                kills.addAll(nonKills);
                System.out.println("KILL PATHS: " + killPaths);
                return new ArrayList<>(kills);
            case "KING 1":
                //Employ king methods (ALL)
                king(x, y);
                break;
            default:
                return null;
        }


        //System.out.println("KILL PATHS CLEANED: " + killPathsCleaner(killPaths, x, y));
        //killPaths = killPathsCleaner(killPaths, x, y);
        validNonKillMoves.addAll(validKillMoves);
        validNonKillMoves = validNonKillMoves.stream().distinct().collect(Collectors.toList());
        return validNonKillMoves;
    }


    public void doMove(Move m) {
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

        m.setPiece(currPiece);
        pieceBiMap.forcePut(new Point2D.Double(x_f, y_f), currPiece);

        if (killPaths.containsKey(m.getEndLocation())) {
            for (Point2D point : killPaths.get(m.getEndLocation())) {
                System.out.println("inside redo");
                if(getPieceAt(point) != null) {
                    m.addCapturedPieceAndLocation(getPieceAt(point), point);
                }
                removePiece((int) point.getX(), (int) point.getY());
            }
        }

        for (Point2D location : m.getCapturedPiecesAndLocations().values()) {
            if(location!= null){
                this.captureAction.process((int) location.getX(), (int) location.getY());
            }
        }

        if (m.isPromote() && m.isUndo()) {
            // demote backend
            getPieceAt(x_f, y_f).setType("Coin");
            getPieceAt(x_f, y_f).setMovePattern((currPiece.getColor().equals("White") ? "P2 1" : "P1 1"));
            // demote frontend
            promoteAction.process(x_f, y_f);
        }
        //TO-DO check if piece has reached opposite end
        if((getPieceAt(x_f, y_f).getColor().equals(bottomColor) && x_f==0) || (!(getPieceAt(x_f, y_f).getColor().equals(bottomColor)) && x_f==height-1)){
            getPieceAt(x_f, y_f).setType("Monarch");
            getPieceAt(x_f, y_f).setMovePattern("KING 1");
            //pieceBiMap.forcePut(new Point2D.Double(x_f, y_f), new Piece("Monarch", "KING 1", 10, init_Color, initID));
            m.setPromote(true);
            promoteAction.process(x_f, y_f);
        }

        //return score;
    }

    /* START: Eight elements that make up the three possible move patterns of the pieces in the game. */
    private Point2D up_left (int x, int y) {
        if (isCellInBounds(x - 1, y - 1) && (getPieceAt(x - 1, y - 1) == null)) {
            return new Point2D.Double(x - 1, y - 1);
        }
        return null;
    }

    private Point2D up_left_kill(int x, int y){
        Piece temp1 = getPieceAt(x-1, y-1);
        Piece temp2 = getPieceAt(x-2, y-2);
        boolean killConditions = isCellInBounds(x-1, y-1) && isCellInBounds(x-2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x-2, y-2);
            if(!killPaths.containsKey(ret)){

                tempKills.add(new Point2D.Double(x-1, y-1));
                killPaths.put(ret, new HashSet<>(tempKills));

                Piece tp = getPieceAt(x, y);
                pieceBiMap.forcePut(new Point2D.Double(x, y), null);
                pieceBiMap.forcePut(ret, tp);
                if(p1Kills(x-2, y-2).size()==0){
                    tempKills.clear();
                }
                pieceBiMap.forcePut(new Point2D.Double(x, y), tp);
                pieceBiMap.forcePut(ret, null);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x-1, y-1));
            }
            return ret;
        }
    }

    private Point2D up_right (int x, int y) {
        if (isCellInBounds(x - 1, y + 1) && (getPieceAt(x - 1, y + 1) == null)) {
            return new Point2D.Double(x - 1, y + 1);
        }
        return null;
    }

    private Point2D up_right_kill(int x, int y){
        Piece temp1 = getPieceAt(x-1, y+1);
        Piece temp2 = getPieceAt(x-2, y+2);
        boolean killConditions = isCellInBounds(x-1, y+1) && isCellInBounds(x-2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x-2, y+2);
            if(!killPaths.containsKey(ret)){

                tempKills.add(new Point2D.Double(x-1, y+1));
                killPaths.put(ret, new HashSet<>(tempKills));

                Piece tp = getPieceAt(x, y);
                pieceBiMap.forcePut(new Point2D.Double(x, y), null);
                pieceBiMap.forcePut(ret, tp);
                if(p1Kills(x-2, y+2).size()==0){
                    tempKills.clear();
                }
                pieceBiMap.forcePut(new Point2D.Double(x, y), tp);
                pieceBiMap.forcePut(ret, null);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x-1, y+1));
            }
            return ret;
        }
    }

    private Point2D down_left (int x, int y) {
        if (isCellInBounds(x + 1, y - 1) && (getPieceAt(x + 1, y - 1) == null)) {
            return new Point2D.Double(x + 1, y - 1);
        }
        return null;
    }

    private Point2D down_left_kill(int x, int y){
        Piece temp1 = getPieceAt(x+1, y-1);
        Piece temp2 = getPieceAt(x+2, y-2);
        boolean killConditions = isCellInBounds(x+1, y-1) && isCellInBounds(x+2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x+2, y-2);
            if(!killPaths.containsKey(ret)){
                tempKills.add(new Point2D.Double(x+1, y-1));
                killPaths.put(ret, new HashSet<>(tempKills));

                Piece tp = getPieceAt(x, y);
                pieceBiMap.forcePut(new Point2D.Double(x, y), null);
                pieceBiMap.forcePut(ret, tp);
                if(p2Kills(x+2, y-2).size()==0){
                    tempKills.clear();
                }
                pieceBiMap.forcePut(new Point2D.Double(x, y), tp);
                pieceBiMap.forcePut(ret, null);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x+1, y-1));
            }
            return ret;
        }
    }

    private Point2D down_right (int x, int y) {
        if (isCellInBounds(x + 1, y + 1) && (getPieceAt(x + 1, y + 1) == null)) {
            return new Point2D.Double(x + 1, y + 1);
        }
        return null;
    }

    private Point2D down_right_kill(int x, int y){
        Piece temp1 = getPieceAt(x+1, y+1);
        Piece temp2 = getPieceAt(x+2, y+2);
        boolean killConditions = isCellInBounds(x+1, y+1) && isCellInBounds(x+2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x+2, y+2);
            if(!killPaths.containsKey(ret)){
                tempKills.add(new Point2D.Double(x+1, y+1));
                killPaths.put(ret, new HashSet<>(tempKills));

                Piece tp = getPieceAt(x, y);
                pieceBiMap.forcePut(new Point2D.Double(x, y), null);
                pieceBiMap.forcePut(ret, tp);
                if(p2Kills(x+2, y+2).size()==0){
                    tempKills.clear();
                }
                pieceBiMap.forcePut(new Point2D.Double(x, y), tp);
                pieceBiMap.forcePut(ret, null);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x+1, y+1));
            }
            return ret;
        }
    }

    /* ***END: Eight elements that make up the three possible move patterns of the pieces in the game.***
    START: Three possible move patterns, one for player 1 (black checkers), one for player 2 (red checkers),
    and one for king checkers. */
    private Set<Point2D> p1NoKills(int x, int y){
        Point2D p1 = up_left(x, y);
        Point2D p2 = up_right(x, y);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p1);
        ret.add(p2);

        ret.remove(null);
        return ret;
    }
    private Set<Point2D> p1Kills(int x, int y){
        Point2D p3 = up_left_kill(x, y);
        Point2D p4 = up_right_kill(x, y);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p3);
        ret.add(p4);
        Piece p = getPieceAt(x, y);
        getNextStepsP1(p3, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
        getNextStepsP1(p4, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
        ret.remove(null);
        return ret;
    }
    private void getNextStepsP1(Point2D start, Set<Point2D> ret, Piece p){
        if(start == null){
            return;
        }
        ret.add(start);
        int i = (int)start.getX();
        int j = (int)start.getY();
        pieceBiMap.forcePut(start, p);
        getNextStepsP1(up_left_kill(i, j), ret, p);
        getNextStepsP1(up_right_kill(i, j), ret, p);
        pieceBiMap.forcePut(start, null);
    }
    private Set<Point2D> p2NoKills(int x, int y){
        Point2D p1 = down_left(x, y);
        Point2D p2 = down_right(x, y);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p1);
        ret.add(p2);

        ret.remove(null);
        return ret;
    }
    private Set<Point2D> p2Kills(int x, int y){
        Point2D p3 = down_left_kill(x, y);
        Point2D p4 = down_right_kill(x, y);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p3);
        ret.add(p4);
        Piece p = getPieceAt(x, y);
        getNextStepsP2(p3, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
        getNextStepsP2(p4, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
        ret.remove(null);
        return ret;
    }
    private void getNextStepsP2(Point2D start, Set<Point2D> ret, Piece p){
        if(start == null){
            return;
        }
        ret.add(start);
        int i = (int)start.getX();
        int j = (int)start.getY();
        pieceBiMap.forcePut(start, p);
        getNextStepsP2(down_left_kill(i, j), ret, p);
        getNextStepsP2(down_right_kill(i, j), ret, p);
        pieceBiMap.forcePut(start, null);
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
        /*Piece piece = getPieceAt(i, j);
        pieceColorMap.get(piece.getColor()).remove(piece);
        pieceLocationBiMap.forcePut(new Point2D.Double(i, j), null);*/
        putPieceAt(new Point2D.Double(i, j), null);
    }
}
