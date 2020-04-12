package ooga.controller;

import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.strategy.HumanPlayer;
import ooga.strategy.Player;
import ooga.strategy.StrategyAI;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.MenuScreen;
import ooga.xml.XMLParser;

import java.awt.*;
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
    private boolean isAIOpponent = true;
    private boolean isOpponentTurn = false;
    private List<Point2D> temp;
    private Player activePlayer;
    private Player playerOne;
    private Player playerTwo;

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
        // replace with AI if AI, but player 1 is always white since they start
        playerOne = new HumanPlayer("a", Color.WHITE, myBoard);
        playerTwo = new HumanPlayer("b", Color.BLACK, myBoard);
        activePlayer = playerOne;
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
            activePlayer.doMove((int) indexes.getX(), (int) indexes.getY(), x, y);
            myBoardView.movePiece(x, y);
            toggleActivePlayer();
            if (activePlayer.isCPU()) {
                List<Integer> AIMove = myAI.generateMove();
                myBoardView.setSelectedLocation(AIMove.get(2), AIMove.get(3));
                activePlayer.doMove(AIMove.get(0), AIMove.get(1), AIMove.get(2), AIMove.get(3));
                myBoardView.movePiece(AIMove.get(0), AIMove.get(1));
                toggleActivePlayer();
            }
            myBoardView.setSelectedLocation(0, 0);
            myBoard.checkWon();
        });
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
    }
}
