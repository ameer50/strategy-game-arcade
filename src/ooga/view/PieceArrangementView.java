package ooga.view;

public abstract class PieceArrangementView {

    private int boardHeight;
    private int boardWidth;
    private int pieceHeight;
    private int pieceWidth;
    private String gameType;
    private String playerChoice;
    public PieceArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String gameType, String playerChoice){

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.pieceHeight = pieceHeight;
        this.pieceWidth = pieceWidth;
        this.gameType = gameType;
        this.playerChoice = playerChoice;

    }

    public abstract void initialize();


}
