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
import ooga.xml.XMLProcessor;

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
    private boolean isAIOpponent = true; // ***
    private boolean isOpponentTurn = false;
    private List<Point2D> temp;
    private Player activePlayer;
    private Player playerOne;
    private Player playerTwo;
    private History history;
    private ObservableList<Move> historyList;
    private Stage stage;
    private XMLProcessor processor;

    public Controller (Stage stage) {
        startTime = System.currentTimeMillis();
        this.stage = stage;
        setUpMenu();
    }

    private void setUpMenu() {
        menuScreen = new MenuScreen(this.stage);
        printMessageAndTime("Setup Menu Screen.");

        menuScreen.buttonListener(e -> {
            setUpGameScreen(menuScreen.getGameSelected(),menuScreen.getFileName());
        });
        printMessageAndTime("Setup listener.");
    }

    private void setUpGameScreen(String typeString, String fileName) {
        GameType gameType = GameType.valueOf(typeString.toUpperCase());
        System.out.println("File name" + fileName);
        String gameXML = String.format(fileName);
        processor = new XMLProcessor();
        processor.parse(gameXML);
        printMessageAndTime("XML parsed.");

        //TODO: change to reflection
        switch (gameType) {
            case CHESS:
                board = new ChessBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
                break;
            case CHECKERS:
                board = new CheckersBoard(processor.getSettings(), processor.getInitialPieceLocations(), processor.getMovePatterns());
        } printMessageAndTime("Setup Board.");

        gameScreen = new GameScreen(this.stage, board.getWidth(), board.getHeight(), processor.getInitialPieceLocations()); // ***
        printMessageAndTime("Setup Game Screen.");

        boardView = gameScreen.getBoardView();
        if (isAIOpponent) setUpAI();
        setUpPlayers();
        setUpHistory();
        setListeners();
    }

    private void setUpPlayers() {
        // TODO: replace with AI, if AI
        playerOne = new HumanPlayer("Player1", "White", board);
        if (!isAIOpponent) {
            playerTwo = new HumanPlayer("Player2", "Black", board);
        } else {
            playerTwo = CPU;
        }
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
        CPU = new StrategyAI("AI", "Black", board, StrategyType.ALPHA_BETA);
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
            Point2D startLocation = boardView.getSelectedLocation();
            Point2D endLocation = new Point2D.Double(toX, toY);

            printMessageAndTime("Did user's move.");

            Move move = new Move(startLocation, endLocation);
            doMove(move);
            history.addMove(move);
            historyList.add(move);
            toggleActivePlayer();
            board.checkWon();

            if (activePlayer.isCPU()) {
                doAIMove();
                printMessageAndTime("Did CPU's move.");
            }
            // board.print();
        });

        gameScreen.getDashboardView().setUndoMoveClicked((e) -> {
            Move prevMove = history.undo();
            historyList.remove(historyList.size() - 1);
            Point2D startLocation = prevMove.getEndLocation();
            Point2D endLocation = prevMove.getStartLocation();
            Move reverseMove = new Move(startLocation, endLocation);
            reverseMove.setUndoTrue();

            doMove(reverseMove);
            toggleActivePlayer();

            if (prevMove.getCapturedPiece() != null) {
                Piece capturedPiece = prevMove.getCapturedPiece();
                Point2D capturedPieceLocation = prevMove.getCapturedPieceLocation();
                board.putPieceAt(capturedPieceLocation, capturedPiece);
                activePlayer.addToScore(-capturedPiece.getValue());
                PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
                boardView.getCellAt(capturedPieceLocation).setPiece(capturedPieceView);
                //TODO: another backend call that can take care of resetting pawns to their first move pattern
            }
        });

        gameScreen.getDashboardView().setRedoMoveClicked((e) -> {
            Move prevMove = history.redo();
            historyList.add(prevMove);

            doMove(prevMove);
            toggleActivePlayer();
        });

        gameScreen.getDashboardView().setQuitClicked((e) -> {
            setUpMenu();
        });

        gameScreen.getDashboardView().setSaveClicked((e) -> {
            System.out.println("inside");
            processor.write(board, "new.xml");
        });


        board.setOnPiecePromoted((int toX, int toY) -> {
            boardView.getCellAt(toX, toY).setPiece(new PieceView(board.getPieceAt(toX, toY).getFullName()));
        });
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
        gameScreen.getDashboardView().setActivePlayerText(activePlayer);
    }

    private void doAIMove() {
        //TODO: have generateMove return a Move
        List<Integer> AIMove = CPU.generateMove();
        int fromX = AIMove.get(0);
        int fromY = AIMove.get(1);
        Point2D startLocation = new Point2D.Double(fromX, fromY);
        int toX = AIMove.get(2);
        int toY = AIMove.get(3);
        Point2D endLocation = new Point2D.Double(toX, toY);
        Move m = new Move(startLocation, endLocation);

        activePlayer.doMove(m);
        boardView.doMove(m);

        toggleActivePlayer();
        board.checkWon();
    }

    private void printMessageAndTime (String message) {
        long endTime = System.currentTimeMillis();
        System.out.println(message);
        System.out.println(String.format("time: %.2f", (float)(endTime-startTime)));
    }

    @Deprecated
    private void stall (double millis) {
        double initial = System.currentTimeMillis();
        double elapsed = 0;
        while (elapsed < millis) {
            elapsed = System.currentTimeMillis() - initial;
        }
    }

    private void doMove(Move m) {
        activePlayer.doMove(m);
        boardView.doMove(m);
    }
}
