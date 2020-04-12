package ooga.controller;

import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.strategy.StrategyAI;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.MenuScreen;
import ooga.xml.XMLParser;
import java.awt.geom.Point2D;
import java.util.List;

public class Controller {
    public enum StrategyType {
        TRIVIAL,
        RANDOM,
        BRUTE_FORCE,
        ALPHA_BETA,
    }
    private GameScreen myGameScreen;
    private MenuScreen menuScreen;
    private Board myBoard;
    private BoardView myBoardView;
    private StrategyAI myAI;
    private boolean toggleMoves = true;
    private boolean isAIOpponent = false;
    private boolean isOpponentTurn = false;
    private List<Point2D> temp;

    public Controller (Stage stage) {
        menuScreen = new MenuScreen(stage);
        menuScreen.buttonListener(e -> {
            makeGameScreen(stage, menuScreen.getFileType());
        });
    }

    public void makeGameScreen(Stage stage, String file) {
        XMLParser p = new XMLParser();
        p.parse(file);
        myBoard = new ChessBoard(p.getSettings(), p.getInitialPieceLocations(),
            p.getMovePatterns());
        myGameScreen = new GameScreen(stage, p.getSettings(), p.getInitialPieceLocations());
        myBoardView = myGameScreen.getBoard();
        setListeners();
    }

    private void setListeners() {
        myAI = new StrategyAI(StrategyType.TRIVIAL, myBoard);
        // TODO: this will be set to something selected by the user
        myBoardView.setOnPieceClicked((int x, int y) -> {
            myBoardView.setSelectedLocation(x, y);
            myBoardView.highlightValidMoves(myBoard.getValidMoves(x, y));
        });

        myBoardView.setOnMoveClicked((int x, int y) -> {
            Point2D indexes = myBoardView.getSelectedLocation();
            myBoard.doMove((int) indexes.getX(), (int) indexes.getY(), x, y);
            myBoardView.movePiece(x, y);
            if (isAIOpponent) {
                List<Integer> AIMove = myAI.generateMove();
                myBoardView.setSelectedLocation(AIMove.get(2), AIMove.get(3));
                myBoard.doMove(AIMove.get(2), AIMove.get(3), AIMove.get(0), AIMove.get(1));
                myBoardView.movePiece(AIMove.get(0), AIMove.get(1));
            }
            myBoardView.setSelectedLocation(0, 0);
            myBoard.checkWon();
            myBoard.print();
        });
    }
}
