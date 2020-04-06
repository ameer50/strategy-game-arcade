package ooga.view;

import javafx.scene.image.ImageView;

public abstract class PieceArrangementView {

    private int boardHeight;
    private int boardWidth;
    private int pieceHeight;
    private int pieceWidth;
    private String playerChoice;
    public PieceArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String playerChoice){

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.pieceHeight = pieceHeight;
        this.pieceWidth = pieceWidth;
        this.playerChoice = playerChoice;

    }
    public abstract void initialize();

    public abstract ImageView[] gamePieces();


}
