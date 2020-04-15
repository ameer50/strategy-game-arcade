package ooga.strategy;

import javafx.beans.property.*;
import ooga.board.Board;
import ooga.history.Move;

import java.awt.*;

public abstract class Player {

    private IntegerProperty score;
    private String name;
    private String color;
    private Board board;

    public Player(String name, String color, Board board) {
        this.name = name;
        this.color = color;
        this.board = board;
        this.score = new SimpleIntegerProperty(0);
    }

    public abstract boolean isCPU();

    public void movePiece(Move m) {
        int pointsScored = board.doMove(m);
        addToScore(pointsScored);
    }

    public void movePiece(Move m, boolean isUndo) {
        int pointsScored = board.doMove(m, isUndo);
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

    public String getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }
}
