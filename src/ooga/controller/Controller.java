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

    public static final int STALL_TIME = 1000;

    public enum StrategyType {
        TRIVIAL,
        RANDOM,
        BRUTE_FORCE,
        SINGLE_BRANCH,
        ALPHA_BETA,
    }
    public enum GameType {
        CHESS,
        CHECKERS,
        GO,
        OTHELLO,
        TIC_TAC_TOE,
        CUSTOM,
    }

    private long startTime;
    private Board board;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private BoardView boardView;
    private StrategyAI CPU;
    private boolean toggleMoves = true;
    private boolean isAIOpponent = true;
    private boolean isOpponentTurn = false;
    private List<Point2D> temp;
    private Player activePlayer;
    private Player playerOne;
    private Player playerTwo;

    public Controller (Stage stage) {
        startTime = System.currentTimeMillis();
        setUpMenu(stage);
    }

    private void setUpMenu(Stage stage) {
        menuScreen = new MenuScreen(stage);
        printMessageAndTime("Setup Menu Screen.");

        menuScreen.buttonListener(e -> {
            setUpGameScreen(stage, menuScreen.getGameSelected());
        });
        printMessageAndTime("Setup listener.");
    }

    private void setUpGameScreen(Stage stage, String typeString) {
        GameType gameType = GameType.valueOf(typeString.toUpperCase());
        String gameXML = String.format("resources/Chess/defaultBlack.xml");
        XMLParser p = new XMLParser();
        p.parse(gameXML);
        printMessageAndTime("XML parsed.");

        // TODO: set this up for checkers, etc.
        switch (gameType) {
            default:
                board = new ChessBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatterns());
        }
        printMessageAndTime("Setup Board.");
        gameScreen = new GameScreen(stage, board, p.getInitialPieceLocations()); // ***
        printMessageAndTime("Setup Game Screen.");
        boardView = gameScreen.getBoardView();
        // replace with AI if AI, but player 1 is always white since they start
        playerOne = new HumanPlayer("a", Color.WHITE, board);
        playerTwo = new HumanPlayer("b", Color.BLACK, board);
        activePlayer = playerOne;

        if (isAIOpponent) {
            setUpAI();
        }
        setListeners();
    }

    private void setUpAI() {
        // TODO: Make this dependent on the user's choice of strategy.
        CPU = new StrategyAI(StrategyType.RANDOM, board);
    }

    private void setListeners() {
        /* X and Y are the indices of the cell clicked to move FROM */
        boardView.setOnPieceClicked((int x, int y) -> {
            boardView.setSelectedLocation(x, y);
            // TODO: change "White" to whatever the user chose.
            boardView.highlightValidMoves(board.getValidMoves(x, y, "White"));
        });

        /* X and Y are the indices of the cell clicked to move TO */
        boardView.setOnMoveClicked((int toX, int toY) -> {
            Point2D indices = boardView.getSelectedLocation();
            int fromX = (int) indices.getX();
            int fromY = (int) indices.getY();
            board.doMove(fromX, fromY, toX, toY);
            boardView.movePiece(fromX, fromY, toX, toY);
            printMessageAndTime("Did user's move.");
            if (isAIOpponent) {
                doAIMove();
                printMessageAndTime("Did CPU's move.");
            }
            // TODO: we need to make the method much more efficient and robust before uncommenting...
        });
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
    }

    private void doAIMove() {
        List<Integer> AIMove = CPU.generateMove("Black");
        int fromX = AIMove.get(0);
        int fromY = AIMove.get(1);
        int toX = AIMove.get(2);
        int toY = AIMove.get(3);
        board.doMove(fromX, fromY, toX, toY);
        // stall(STALL_TIME);
        boardView.movePiece(fromX, fromY, toX, toY);
    }

    private void printMessageAndTime (String message) {
        long endTime = System.currentTimeMillis();
        System.out.println(message);
        System.out.println(String.format("time: %.2f", (float)(endTime-startTime)));
    }

    private void stall (double millis) {
        double initial = System.currentTimeMillis();
        double elapsed = 0;
        while (elapsed < millis) {
            elapsed = System.currentTimeMillis() - initial;
        }
    }
}
