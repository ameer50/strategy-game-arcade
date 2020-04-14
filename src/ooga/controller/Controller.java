package ooga.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.CheckersBoard;
import ooga.board.ChessBoard;
import ooga.board.Piece;
import ooga.history.History;
import ooga.history.Move;
import ooga.strategy.HumanPlayer;
import ooga.strategy.Player;
import ooga.strategy.StrategyAI;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.MenuScreen;
import ooga.view.PieceView;
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
    private boolean isAIOpponent = false;
    private boolean isOpponentTurn = false;
    private List<Point2D> temp;
    private Player activePlayer;
    private Player playerOne;
    private Player playerTwo;
    private History history;
    private ObservableList<Move> historyList;

    public Controller (Stage stage) {
        startTime = System.currentTimeMillis();
        setUpMenu(stage);
    }

    private void setUpMenu(Stage stage) {
        menuScreen = new MenuScreen(stage);
        printMessageAndTime("Setup Menu Screen.");

        menuScreen.buttonListener(e -> {
            setUpGameScreen(stage, menuScreen.getGameSelected(),menuScreen.getFileName());
        });
        printMessageAndTime("Setup listener.");
    }

    private void setUpGameScreen(Stage stage, String typeString, String fileName) {
        GameType gameType = GameType.valueOf(typeString.toUpperCase());
        System.out.println("File name" + fileName);
        String gameXML = String.format(fileName);
        XMLParser p = new XMLParser();
        p.parse(gameXML);
        printMessageAndTime("XML parsed.");

        switch (gameType) {
            case CHESS:
                board = new ChessBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatterns());
                break;
            case CHECKERS:
                board = new CheckersBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatterns());
        } printMessageAndTime("Setup Board.");

        gameScreen = new GameScreen(stage, board.getWidth(), board.getHeight(), p.getInitialPieceLocations()); // ***
        printMessageAndTime("Setup Game Screen.");

        boardView = gameScreen.getBoardView();
        setUpPlayers();
        setUpHistory();
        if (isAIOpponent) setUpAI();
        setListeners();
    }

    private void setUpPlayers() {
        // TODO: replace with AI, if AI
        playerOne = new HumanPlayer("Player1", "WHITE", board);
        playerTwo = new HumanPlayer("Player2", "BLACK", board);
        gameScreen.getDashboardView().bindScores(playerOne, playerTwo);
        activePlayer = playerOne;
        gameScreen.getDashboardView().setActivePlayerText(activePlayer);
    }

    private void setUpHistory() {
        history = new History();
        historyList = FXCollections.observableArrayList();
        gameScreen.getDashboardView().getHistory().setItems(historyList);
    }

    private void setUpAI() {
        // TODO: Make this dependent on the user's choice of strategy.
        CPU = new StrategyAI("AI", "BLACK", board, StrategyType.TRIVIAL);
    }

    private void setListeners() {
        /* X and Y are the indices of the cell clicked to move FROM */
        boardView.setOnPieceClicked((int x, int y) -> {
            System.out.println(boardView.getCellAt(x, y).getPiece().getColor()); // ***
            if (!boardView.getCellAt(x, y).getPiece().getColor().equals(activePlayer.getColor())) {
                return;
            }
            boardView.setSelectedLocation(x, y);
            boardView.highlightValidMoves(board.getValidMoves(x, y));
            System.out.println("Highlighted moves.");
        });

        /* X and Y are the indices of the cell clicked to move TO */
        boardView.setOnMoveClicked((int toX, int toY) -> {
            Point2D startLoc = boardView.getSelectedLocation();
            Point2D endLoc = new Point2D.Double(toX, toY);
            Piece capturedPiece = board.getPieceAt(toX, toY);

            doPieceMove(startLoc, endLoc, false);
            printMessageAndTime("Did user's move.");

            Move move = new Move(board.getPieceAt(toX, toY), startLoc, endLoc, capturedPiece);
            history.addMove(move);
            historyList.add(move);

            if (activePlayer.isCPU()) {
                doAIMove();
                printMessageAndTime("Did CPU's move.");
            }
            toggleActivePlayer();
            board.checkWon();
            // board.print();
            //gameScreen.setRecentLocation(fromX, fromY, toX, toY);
        });

        gameScreen.getDashboardView().setUndoMoveClicked((e) -> {
            Move prevMove = history.undo();
            historyList.remove(historyList.size() - 1);
            Point2D startLoc = prevMove.getEndLocation();
            Point2D endLoc = prevMove.getStartLocation();

            doPieceMove(startLoc, endLoc, true);
            toggleActivePlayer();

            if (prevMove.getCapturedPiece() != null) {
                int fromX = (int) startLoc.getX();
                int fromY = (int) startLoc.getY();
                Piece capturedPiece = prevMove.getCapturedPiece();
                board.putPieceAt(fromX, fromY, capturedPiece);
                activePlayer.addToScore((int) -capturedPiece.getValue());
                PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
                boardView.getCellAt(fromX, fromY).setPiece(capturedPieceView);
                //TODO: another backend call that can take care of resetting pawns to their first move pattern
            }
        });

        gameScreen.getDashboardView().setRedoMoveClicked((e) -> {
            Move prevMove = history.redo();
            historyList.add(prevMove);
            Point2D startLoc = prevMove.getStartLocation();
            Point2D endLoc = prevMove.getEndLocation();
            doPieceMove(startLoc, endLoc, false);
            toggleActivePlayer();
        });

        board.setOnPiecePromoted((int toX, int toY) -> {
            Piece piece = board.getPieceAt(toX, toY);
            String name = String.format("%s_%s", piece.getColor(), piece.getType());
            boardView.getCellAt(toX, toY).setPiece(new PieceView(name));
        });
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
        gameScreen.getDashboardView().setActivePlayerText(activePlayer);
    }

    private void doAIMove() {
        List<Integer> AIMove = CPU.generateMove();
        int fromX = AIMove.get(0);
        int fromY = AIMove.get(1);
        int toX = AIMove.get(2);
        int toY = AIMove.get(3);
        activePlayer.doMove(fromX, fromY, toX, toY, false);
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

    private void doPieceMove(Point2D start, Point2D end, boolean undo) {
        int fromX = (int) start.getX();
        int fromY = (int) start.getY();
        int toX = (int) end.getX();
        int toY = (int) end.getY();

        boardView.movePiece(fromX, fromY, toX, toY);
        activePlayer.doMove(fromX, fromY, toX, toY, undo);
    }
}
