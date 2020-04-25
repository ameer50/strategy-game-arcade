package ooga.history;

import javafx.util.Pair;
import ooga.board.Piece;
import ooga.view.SetUpError;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Move implements Cloneable {

    public static final String ERROR_MESSAGE = "Cannot get reverse move.";
    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;
    private Map<Point2D, Piece> capturedPiecesAndLocations;
    private Map<Point2D, Pair<Piece, Piece>> convertedPiecesAndLocations;
    private boolean isUndo;
    private boolean isPieceGenerated;
    private String color;

    public Move(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        capturedPiecesAndLocations = new HashMap<>();
        convertedPiecesAndLocations = new HashMap<>();
        isUndo = false;
        isPieceGenerated = false;
    }

    public Move getReverseMove() {
        try {
            Move reverseMove = (Move) this.clone();
            reverseMove.setStartLocation(endLocation);
            reverseMove.setEndLocation(startLocation);
            reverseMove.setUndo(true);
            return reverseMove;
        } catch (CloneNotSupportedException e) {
            System.out.println(ERROR_MESSAGE);
            return null;
        }
    }

    public Point2D getStartLocation() {
        return (Point2D) startLocation.clone();
    }

    public void setStartLocation(Point2D location) {
        startLocation = (Point2D) location.clone();
    }

    public Point2D getEndLocation() { return (Point2D) endLocation.clone(); }

    public void setEndLocation(Point2D location) {
        endLocation = (Point2D) location.clone();
    }

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

    @Override
    public String toString() {
        if (isPieceGenerated) return String.format("%s added at (%d, %d)", piece, (int) endLocation.getX(), (int) endLocation.getY());
        return String.format("%s from (%d, %d) to (%d, %d)", piece, (int) startLocation.getX(), (int) startLocation.getY(), (int) endLocation.getX(), (int) endLocation.getY());
    }

    public void addCapturedPiece(Piece capturedPiece, Point2D capturedPieceLocation){
        capturedPiecesAndLocations.put(capturedPieceLocation, capturedPiece);
    }

    public Map<Point2D, Piece> getCapturedPiecesAndLocations() {
        return capturedPiecesAndLocations;
    }

    public void addConvertedPiece(Pair<Piece, Piece> piecePair, Point2D convertedPieceLocation){
        convertedPiecesAndLocations.put(convertedPieceLocation, piecePair);
    }

    public Map<Point2D, Pair<Piece, Piece>> getConvertedPiecesAndLocations() {
        return convertedPiecesAndLocations;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public boolean isPieceGenerated() {
        return isPieceGenerated;
    }

    public void setPieceGenerated(boolean isPieceGenerated) {
        this.isPieceGenerated = isPieceGenerated;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
