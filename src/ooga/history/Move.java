package ooga.history;

import ooga.board.Piece;

import java.awt.geom.Point2D;

public class Move {

    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;

    public Move(Piece piece, Point2D startLocation, Point2D endLocation) {
        this.piece = piece;
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
        return String.format("%s from %s to %s", piece, startLocation, endLocation);
    }
}
