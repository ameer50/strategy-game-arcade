package ooga.player;

import javafx.beans.property.*;
import ooga.board.Board;
import ooga.board.Piece;
import ooga.history.Move;

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

    public void doMove(Move m) {
        m.setColor(color);
        board.doMove(m);
        for (Piece capturedPiece: m.getCapturedPiecesAndLocations().keySet()) {
            addToScore(capturedPiece.getValue());
        }
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
