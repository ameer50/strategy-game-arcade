package ooga.strategy;

import ooga.board.Board;

import java.awt.*;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Color color, Board board) {
        super(name, color, board);
    }

    @Override
    public boolean isCPU() {
        return false;
    }
}
