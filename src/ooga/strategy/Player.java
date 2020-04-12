package ooga.strategy;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import ooga.board.Board;

import java.awt.*;

public abstract class Player {

    private DoubleProperty score;
    private String name;
    private Color color;
    private Board board;

    public Player(String name, Color color, Board board) {
        this.name = name;
        this.color = color;
        this.board = board;
        this.score = new SimpleDoubleProperty(0);
    }

    public abstract boolean isCPU();

    public void doMove(int startX, int startY, int endX, int endY) {
        score.setValue(score.getValue() + board.doMove(startX, startY, endX, endY)) ;
    }

    public DoubleProperty getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }
}
