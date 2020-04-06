package ooga.view;

public abstract class PieceArrangementView {

    private int boardHeight;
    private int boardWidth;
    private int pieceHeight;
    private int pieceWidth;
    private String gameType;
    public PieceArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String gameType){

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.pieceHeight = pieceHeight;
        this.pieceWidth = pieceWidth;
        this.gameType = gameType;

    }

    public abstract void initialize();


}
