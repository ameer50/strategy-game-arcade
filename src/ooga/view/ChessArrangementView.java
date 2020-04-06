package ooga.view;

public class ChessArrangementView extends PieceArrangementView {

    private PieceView[][] arrangement;
    private static final int BOARD_HEIGHT = 8;
    private static final int BOARD_WIDTH = 8;

    public ChessArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String gameType, String playerChoice){
        super( boardHeight, boardWidth, pieceHeight, pieceWidth, gameType, playerChoice);
        arrangement = new PieceView[BOARD_WIDTH][BOARD_HEIGHT];

    }

    @Override
    public void initialize() {

        for(int i =0; i < arrangement.length; i++){
            for(int j =0; j < 2; j++){
                //arrangement[i][j] = new PieceView()
            }
        }


    }
}
