package ooga.history;

import ooga.board.Piece;
import ooga.view.PieceView;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Move {

    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;
    private Map<Piece, Point2D> capturedPiecesAndLocations;
    private boolean isUndo;
    private boolean isPromote;
    private boolean isPieceGenerated;

    public Move(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        capturedPiecesAndLocations = new HashMap<>();
        isUndo = false;
        isPromote = false;
        isPieceGenerated = false;
    }

    public Move getReverseMove() {
        return new Move(endLocation, startLocation);
    }

    public Point2D getStartLocation() {
        return (Point2D) startLocation.clone();
    }
    public Point2D getEndLocation() { return (Point2D) endLocation.clone(); }
    /* TODO: replace instances of the above with instances of the below */
    public int getStartX() { return (int) startLocation.getX(); }
    public int getStartY() { return (int) startLocation.getY(); }
    public int getEndX() { return (int) endLocation.getX(); }
    public int getEndY() { return (int) endLocation.getY(); }

    public void setUndo(boolean isUndo) {
        this.isUndo = isUndo;
    }

    public boolean isUndo() {
        return isUndo;
    }

    public boolean isPromote() {
        return isPromote;
    }

    @Override
    public String toString() {
        return String.format("%s from (%d, %d) to (%d, %d)", piece, (int) startLocation.getX(), (int) startLocation.getY(), (int) endLocation.getX(), (int) endLocation.getY());
    }

    public void addCapturedPiece(Piece capturedPiece, Point2D capturedPieceLocation){
        capturedPiecesAndLocations.put(capturedPiece, capturedPieceLocation);
    }

    public Map<Piece, Point2D> getCapturedPiecesAndLocations() {
        return capturedPiecesAndLocations;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public void setPromote(boolean isPromote) {
        this.isPromote = isPromote;
    }

    public boolean isPieceGenerated() {
        return isPieceGenerated;
    }

    public void setPieceGenerated(boolean isPieceGenerated) {
        this.isPieceGenerated = isPieceGenerated;
    }
}
