package ooga.strategy;

import ooga.board.Board;

import java.awt.*;

public abstract class Player {

    private double score;
    private String name;
    private Color color;
    private Board board;

    public Player(String name, Color color, Board board) {
        this.name = name;
        this.color = color;
        this.board = board;
        this.score = 0;
    }

    public abstract boolean isCPU();

    public void doMove(int startX, int startY, int endX, int endY) {
        score += board.doMove(startX, startY, endX, endY);
    }

    public double getScore() {
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
