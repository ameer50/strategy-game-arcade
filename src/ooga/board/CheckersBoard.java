package ooga.board;

import java.awt.Point;
import java.io.Serializable;
import javafx.util.Pair;
import ooga.history.Move;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CheckersBoard extends Board implements Serializable {
    public Map<Point2D, Set<Point2D>> killPaths = new HashMap<>();

    public CheckersBoard(Map<String, String> settings, Map<Point2D, String> locations, Map<String, String> movePatterns,
        Map<String, Integer> scores) {
        super(settings, locations, movePatterns, scores);
    }

    @Override
    public String checkWon() {
        String result = checkOneColor();
        String result2 = checkTrapped();

        if(result!=null){
            return result;
        }
        else {
            return result2;
        }
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
                    List<Point2D> temp = getValidMoves(new Point2D.Double(i, j));
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
    public List<Point2D> getValidMoves(Point2D coordinate) {
        Piece piece = getPieceAt(coordinate);
        if (piece == null) {
            return null;
        }
        killPaths.clear();
        String movPat = piece.getMovePattern();
        switch (movPat) {
            case "P1 1":
                return new ArrayList<>(p1(coordinate));
            case "P2 1":
                return new ArrayList<>(p2(coordinate));
            case "KING 1":
                return new ArrayList<>(king(coordinate));
            default:
                return null;
        }
    }


    public void doMove(Move m) {
        int x_f = (int) m.getEndLocation().getX();
        int y_f = (int) m.getEndLocation().getY();
        Piece currPiece = getPieceAt(m.getStartLocation());
        m.setPiece(currPiece);

        if (m.isPromote() && m.isUndo()) {
            // demote backend
            currPiece.setType("Coin");
            currPiece.setMovePattern((currPiece.getColor().equals("White") ? "P2 1" : "P1 1"));
            currPiece.setValue(pieceScores.get(currPiece.getFullName()));
            // demote frontend
            promoteAction.process(m.getStartLocation());
        }
        if((currPiece.getColor().equals(bottomColor) && x_f==0) || (!(currPiece.getColor().equals(bottomColor)) && x_f==height-1)){
            currPiece.setType("Monarch");
            currPiece.setMovePattern("KING 1");
            currPiece.setValue(pieceScores.get(currPiece.getFullName()));
            m.setPromote(true);
            promoteAction.process(m.getStartLocation());
        }

        placePiece(m.getStartLocation(), null);
        placePiece(m.getEndLocation(), currPiece);

        pieceBiMap.forcePut(m.getEndLocation(), currPiece);

        if (killPaths.containsKey(m.getEndLocation())) {
            for (Point2D point : killPaths.get(m.getEndLocation())) {
                System.out.println("inside redo");
                if(getPieceAt(point) != null) {
                    m.addCapturedPiece(getPieceAt(point), point);
                }
                removePiece(point);
            }
        }

        for (Point2D location : m.getCapturedPiecesAndLocations().values()) {
            if(location!= null){
                removePiece(location);
                //this.captureAction.process((int) location.getX(), (int) location.getY());
            }
        }


        //return score;
    }

    /* START: Eight elements that make up the three possible move patterns of the pieces in the game. */
    private Point2D up_left (Point2D coordinate) {
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        if (isCellInBounds(x - 1, y - 1) && (getPieceAt(x - 1, y - 1) == null)) {
            return new Point2D.Double(x - 1, y - 1);
        }
        return null;
    }

    private Point2D up_left_kill(Point2D coordinate, Set<Point2D> currentPath){
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        Piece temp1 = getPieceAt(x-1, y-1);
        Piece temp2 = getPieceAt(x-2, y-2);
        boolean killConditions = isCellInBounds(x-1, y-1) && isCellInBounds(x-2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x-2, y-2);
            if(!killPaths.containsKey(ret)){
                Set<Point2D> killPath = new HashSet<>();
                killPath.addAll(currentPath);
                killPath.add(new Point2D.Double(x-1, y-1));
                killPaths.put(ret, killPath);
            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x-1, y-1));
            }
            return ret;
        }
    }

    private Point2D up_right (Point2D coordinate) {
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        if (isCellInBounds(x - 1, y + 1) && (getPieceAt(x - 1, y + 1) == null)) {
            return new Point2D.Double(x - 1, y + 1);
        }
        return null;
    }

    private Point2D up_right_kill(Point2D coordinate, Set<Point2D> currentPath){
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        Piece temp1 = getPieceAt(x-1, y+1);
        Piece temp2 = getPieceAt(x-2, y+2);
        boolean killConditions = isCellInBounds(x-1, y+1) && isCellInBounds(x-2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x-2, y+2);
            if(!killPaths.containsKey(ret)){

                Set<Point2D> killPath = new HashSet<>();
                killPath.addAll(currentPath);
                killPath.add(new Point2D.Double(x-1, y+1));
                killPaths.put(ret, killPath);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x-1, y+1));
            }
            return ret;
        }
    }

    private Point2D down_left (Point2D coordinate) {
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        if (isCellInBounds(x + 1, y - 1) && (getPieceAt(x + 1, y - 1) == null)) {
            return new Point2D.Double(x + 1, y - 1);
        }
        return null;
    }

    private Point2D down_left_kill(Point2D coordinate, Set<Point2D> currentPath){
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        Piece temp1 = getPieceAt(x+1, y-1);
        Piece temp2 = getPieceAt(x+2, y-2);
        boolean killConditions = isCellInBounds(x+1, y-1) && isCellInBounds(x+2, y-2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x+2, y-2);
            if(!killPaths.containsKey(ret)){
                Set<Point2D> killPath = new HashSet<>();
                killPath.addAll(currentPath);
                killPath.add(new Point2D.Double(x+1, y-1));
                killPaths.put(ret, killPath);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x+1, y-1));
            }
            return ret;
        }
    }

    private Point2D down_right (Point2D coordinate) {
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        if (isCellInBounds(x + 1, y + 1) && (getPieceAt(x + 1, y + 1) == null)) {
            return new Point2D.Double(x + 1, y + 1);
        }
        return null;
    }

    private Point2D down_right_kill(Point2D coordinate, Set<Point2D> currentPath){
        int x = (int) coordinate.getX();
        int y = (int) coordinate.getY();
        Piece temp1 = getPieceAt(x+1, y+1);
        Piece temp2 = getPieceAt(x+2, y+2);
        boolean killConditions = isCellInBounds(x+1, y+1) && isCellInBounds(x+2, y+2) && temp1!=null && temp2==null && isOppColor(getPieceAt(x, y), temp1);
        if(!killConditions){
            return null;
        }
        else{
            Point2D ret = new Point2D.Double(x+2, y+2);
            if(!killPaths.containsKey(ret)){
                Set<Point2D> killPath = new HashSet<>();
                killPath.addAll(currentPath);
                killPath.add(new Point2D.Double(x+1, y+1));
                killPaths.put(ret, killPath);

            } else if(killPaths.containsKey(ret)){
                killPaths.get(ret).add(new Point2D.Double(x+1, y+1));
            }
            return ret;
        }
    }

    /* ***END: Eight elements that make up the three possible move patterns of the pieces in the game.***
    START: Three possible move patterns, one for player 1 (black checkers), one for player 2 (red checkers),
    and one for king checkers. */

    private Set<Point2D> p1(Point2D coordinate){
        Set<Point2D> nonKills = p1NoKills(coordinate);
        Set<Point2D> kills = p1Kills(coordinate);
        kills.addAll(nonKills);
        //System.out.println("KILL PATHS: " + killPaths);
        return kills;
    }
    private Set<Point2D> p1NoKills(Point2D coordinate){
        Point2D p1 = up_left(coordinate);
        Point2D p2 = up_right(coordinate);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p1);
        ret.add(p2);

        ret.remove(null);
        return ret;
    }
    private Set<Point2D> p1Kills(Point2D coordinate){
        Point2D p3 = up_left_kill(coordinate, new HashSet<>());
        Point2D p4 = up_right_kill(coordinate, new HashSet<>());
        Set<Point2D> ret = new HashSet<>();
        ret.add(p3);
        ret.add(p4);
        Piece p = getPieceAt(coordinate);
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
        pieceBiMap.forcePut(start, p);
        getNextStepsP1(up_left_kill(start, killPaths.get(start)), ret, p);
        getNextStepsP1(up_right_kill(start, killPaths.get(start)), ret, p);
        pieceBiMap.forcePut(start, null);
    }

    private Set<Point2D> p2(Point2D coordinate){
        Set<Point2D> nonKills = p2NoKills(coordinate);
        Set<Point2D> kills = p2Kills(coordinate);
        kills.addAll(nonKills);
        return kills;
    }
    private Set<Point2D> p2NoKills(Point2D coordinate){
        Point2D p1 = down_left(coordinate);
        Point2D p2 = down_right(coordinate);
        Set<Point2D> ret = new HashSet<>();
        ret.add(p1);
        ret.add(p2);

        ret.remove(null);
        return ret;
    }
    private Set<Point2D> p2Kills(Point2D coordinate){
        Point2D p3 = down_left_kill(coordinate, new HashSet<>());
        Point2D p4 = down_right_kill(coordinate, new HashSet<>());
        Set<Point2D> ret = new HashSet<>();
        ret.add(p3);
        ret.add(p4);
        Piece p = getPieceAt(coordinate);
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
        pieceBiMap.forcePut(start, p);
        getNextStepsP2(down_left_kill(start, killPaths.get(start)), ret, p);
        getNextStepsP2(down_right_kill(start, killPaths.get(start)), ret, p);
        pieceBiMap.forcePut(start, null);
    }
    private void getNextStepsKing(Point2D start, Set<Point2D> ret, Piece p){
        if(start == null){
            return;
        }
        ret.add(start);
        pieceBiMap.forcePut(start, p);
        getNextStepsP1(up_left_kill(start, killPaths.get(start)), ret, p);
        getNextStepsP1(up_right_kill(start, killPaths.get(start)), ret, p);
        getNextStepsP2(down_left_kill(start, killPaths.get(start)), ret, p);
        getNextStepsP2(down_right_kill(start, killPaths.get(start)), ret, p);
        pieceBiMap.forcePut(start, null);
    }
    private Set<Point2D> king(Point2D coordinate) {
        Set<Point2D> kingNoKills = p1NoKills(coordinate);
        kingNoKills.addAll(p2NoKills(coordinate));
        Point2D p3 = up_left_kill(coordinate, new HashSet<>());
        Point2D p4 = up_right_kill(coordinate, new HashSet<>());
        Point2D p5 = down_left_kill(coordinate, new HashSet<>());
        Point2D p6 = down_right_kill(coordinate, new HashSet<>());
        Set<Point2D> ret = new HashSet<>();
        ret.addAll(kingNoKills);
        ret.add(p3);
        ret.add(p4);
        ret.add(p5);
        ret.add(p6);
        Piece p = getPieceAt(coordinate);
        Point2D[] pArray = {p3, p4, p5, p6};

        for(Point2D point: pArray){
            getNextStepsKing(point, ret, new Piece(p.getType(), p.getMovePattern(), p.getValue(), p.getColor()));
        }
        ret.remove(null);

        System.out.println("KILL PATHS: " + killPaths);
        return ret;
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

    private void removePiece(Point2D coordinate) {
        /*Piece piece = getPieceAt(i, j);
        pieceColorMap.get(piece.getColor()).remove(piece);
        pieceLocationBiMap.forcePut(new Point2D.Double(i, j), null);*/
        putPieceAt(coordinate, null);
    }
}
