package ooga.history;

import ooga.board.Piece;
import ooga.view.PieceView;

import java.awt.geom.Point2D;

public class Move {

    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;
    private Piece capturedPiece;

    public Move(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public Point2D getStartLocation() {
        return startLocation;
    }

    public Point2D getEndLocation() {
        return endLocation;
    }

    @Override
    public String toString() {
        return String.format("%s from (%d, %d) to (%d, %d)", piece, (int) startLocation.getX(), (int) startLocation.getY(), (int) endLocation.getX(), (int) endLocation.getY());
    }

    public Piece getCapturedPiece(){
        return capturedPiece;
    }


    public void setCapturedPiece(Piece cap){
        capturedPiece = cap;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }
}
