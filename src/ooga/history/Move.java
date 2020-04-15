package ooga.history;

import ooga.board.Piece;
import ooga.view.PieceView;

import java.awt.geom.Point2D;

public class Move {

    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;
    private Piece capturedPiece;
    private Point2D capturedPieceLocation;
    private boolean isUndo;

    public Move(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        isUndo = false;
    }

    public Point2D getStartLocation() {
        return startLocation;
    }

    public Point2D getEndLocation() {
        return endLocation;
    }

    public void setUndoTrue() {
        isUndo = true;
    }

    public boolean isUndo() {
        return isUndo;
    }

    @Override
    public String toString() {
        return String.format("%s from (%d, %d) to (%d, %d)", piece, (int) startLocation.getX(), (int) startLocation.getY(), (int) endLocation.getX(), (int) endLocation.getY());
    }

    public void setCapturedPieceAndLocation(Piece capturedPiece, Point2D capturedPieceLocation){
        this.capturedPiece = capturedPiece;
        this.capturedPieceLocation = capturedPieceLocation;
    }

    public Piece getCapturedPiece(){
        return capturedPiece;
    }

    public Point2D getCapturedPieceLocation() {
        return capturedPieceLocation;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }
}
