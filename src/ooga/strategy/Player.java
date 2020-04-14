package ooga.strategy;

import javafx.beans.property.*;
import ooga.board.Board;

import java.awt.*;

public abstract class Player {

    private IntegerProperty score;
    private String name;
    private Color color;
    private Board board;

    public Player(String name, Color color, Board board) {
        this.name = name;
        this.color = color;
        this.board = board;
        this.score = new SimpleIntegerProperty(0);
    }

    public abstract boolean isCPU();

    public void doMove(int startX, int startY, int endX, int endY, boolean undo) {
        int pointsScored = board.doMove(startX, startY, endX, endY, undo);
        addToScore(pointsScored);
    }

    public void addToScore(int amount) {
        score.setValue(score.getValue() + amount);
    }

    public IntegerProperty getScore() {
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
