package ooga.view;

public class ChessArrangementView extends PieceArrangementView {

    private Piece[][] arrangement;

    public ChessArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String gameType){
        super( boardHeight, boardWidth, pieceHeight, pieceWidth, gameType);
        arrangement = new Piece[8][8];

    }

    @Override
    public void initialize() {



    }
}
