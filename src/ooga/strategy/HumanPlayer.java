package ooga.strategy;

import ooga.board.Board;

import java.awt.*;

public class HumanPlayer implements Player {

    private double score;
    private String name;
    private Color color;
    private Board myBoard;

    public HumanPlayer(String name, Color color, Board board) {
        this.score = 0;
        this.name = name;
        this.color = color;
        myBoard = board;
    }

    public void doMove(int startX, int startY, int endX, int endY) {
        score += myBoard.doMove(startX, startY, endX, endY);
    }

    @Override
    public boolean isCPU() {
        return false;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
