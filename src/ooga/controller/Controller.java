package ooga.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.Main;
import ooga.board.*;
import ooga.history.History;
import ooga.history.Move;
import ooga.player.HumanPlayer;
import ooga.player.Player;
import ooga.player.CPUPlayer;
import ooga.view.*;
import ooga.xml.XMLProcessor;

import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

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
        CONNECTFOUR,
        GO,
        OTHELLO,
        TIC_TAC_TOE,
        CUSTOM,
    }

    private static String PACKAGE_NAME = "ooga.board.";
    private long startTime;
    private Board board;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private BoardView boardView;
    private DashboardView dashboardView;
    private CPUPlayer CPU;
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
        //printMessageAndTime("Set up Menu Screen.");
        menuScreen.setButtonListener(e -> {
            setUpGameScreen(menuScreen.getGameChoice(), menuScreen.getFileChoice());
        });
        printMessageAndTime("Set up listeners.");

    }

    private void setUpGameScreen(String gameChoice, String fileChoice) {
        String gameXML = String.format(fileChoice);

        processor = new XMLProcessor();
        processor.parse(gameXML);

        try {
            Class c = Class.forName(PACKAGE_NAME + gameChoice + "Board");
            Constructor objConstruct = c.getDeclaredConstructor(Map.class, Map.class, Map.class);
            board = (Board) objConstruct.newInstance(processor.getSettings(), processor.getInitialPieceLocations(),
                    processor.getMovePatterns());
        } catch (Exception e) {
            System.out.println("Could not find game.");
        }

        gameScreen = new GameScreen(this.stage, board.getWidth(), board.getHeight(), processor.getInitialPieceLocations()); // ***
        //printMessageAndTime("Setup Game Screen.");

        boardView = gameScreen.getBoardView();
        boardView.arrangePlayerIcons(processor.getSettings().get("icon"), menuScreen.getPlayerOneColor(), menuScreen.getPlayerTwoColor());
        dashboardView = gameScreen.getDashboardView();
        dashboardView.addIcons(boardView.getIcons());

        if (menuScreen.isDarkMode()){
            gameScreen.toggleGameDarkMode();
            dashboardView.toggleDarkMode();
        }

        gameScreen.enableGameCSS(menuScreen.getGameChoice());
        setUpHistory();
        setUpPlayers();
        setListeners();

    }

    private void setUpPlayers() {
        playerOne = new HumanPlayer(menuScreen.getPlayerOneName(), menuScreen.getPlayerOneColor(), board);
        if (!menuScreen.getIsGameOnePlayer()) {
            playerTwo = new HumanPlayer(menuScreen.getPlayerTwoName(), menuScreen.getPlayerTwoColor(), board);
        } else {
            playerTwo = CPU = new CPUPlayer("CPU", menuScreen.getPlayerTwoColor(), board, StrategyType.ALPHA_BETA);
        }
        dashboardView.setPlayerNames(playerOne.getName(), playerTwo.getName());
        dashboardView.bindScores(playerOne.getScore(), playerTwo.getScore());

        activePlayer = (playerOne.getColor().equals("White")) ? playerOne : playerTwo;
        if (activePlayer.isCPU()) doCPUMove();
        gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void setUpHistory() {
        history = new History();
        historyList = FXCollections.observableArrayList();
        dashboardView.getHistoryDisplay().setItems(historyList);
    }

    private void setListeners() {
        setBoardListeners();
        setBoardViewListeners();
        setDashboardViewListeners();
    }

    private void setBoardListeners() {
        board.setOnPiecePromoted((int fromX, int fromY) -> {
            boardView.getCellAt(fromX, fromY).setPiece(new PieceView(board.getPieceAt(fromX, fromY).getFullName()));
        });
    }

    private void setBoardViewListeners() {
        boardView.setOnPieceClicked((int x, int y) -> {
            PieceView pieceView = boardView.getCellAt(x, y).getPiece();
            if (pieceView.getColor().equals(activePlayer.getColor())) {
                boardView.setSelectedLocation(x, y);
                boardView.highlightValidMoves(board.getValidMoves(x, y));
            }
        });

        boardView.setOnMoveClicked((int toX, int toY) -> {
            Point2D startLocation = boardView.getSelectedLocation();
            Point2D endLocation = new Point2D.Double(toX, toY);

            Move move = new Move(startLocation, endLocation);
            doMove(move);
            removeCapturedPieces(move);
            boardView.replenishIcon(move);

            history.addMove(move);
            historyList.add(move);
            dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());

            toggleActivePlayer();

            if (activePlayer.isCPU()) {
                doCPUMove();
                toggleActivePlayer();
            }
        });
    }

    private void setDashboardViewListeners() {
        dashboardView.setUndoMoveClicked((e) -> {
            Move prevMove = history.undo();
            historyList.remove(historyList.size() - 1);
            Move reverseMove = prevMove.getReverseMove(true);

            doMove(reverseMove);
            dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());
            toggleActivePlayer();

            replenishCapturedPieces(prevMove);
        });

        dashboardView.setRedoMoveClicked((e) -> {
            Move prevMove = history.redo();
            historyList.add(prevMove);
            doMove(prevMove);
            removeCapturedPieces(prevMove);
            dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());
            toggleActivePlayer();
        });

        dashboardView.setNewWindowClicked((e) -> {
            newWindow();
        });
        dashboardView.setQuitClicked((e) -> {
            setUpMenu();
        });

        dashboardView.setSaveClicked((e) -> {
            //TODO: change fileName to be an input
            processor.write(board, gameScreen.getDashboardView().getNewFileName());
        });
    }

    private void checkWon() {
        String winner = board.checkWon();
        if (winner != null) {
            dashboardView.setWinner(winner);
            dashboardView.winnerPopUp();
        }
    }

    private void removeCapturedPieces(Move m) {
        for (Point2D location: m.getCapturedPiecesAndLocations().values()) {
            if (board.getPieceAt(location) == null) boardView.getCellAt(location).setPiece(null);
        }
    }

    private void replenishCapturedPieces(Move prevMove) {
        Map<Piece, Point2D> map = prevMove.getCapturedPiecesAndLocations();
        for (Piece capturedPiece: map.keySet()) {
            Point2D capturedPieceLocation = map.get(capturedPiece);
            board.putPieceAt(capturedPieceLocation, capturedPiece);
            activePlayer.addToScore(-capturedPiece.getValue());
            PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
            boardView.getCellAt(capturedPieceLocation).setPiece(capturedPieceView);
        }
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
        dashboardView.setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void doCPUMove() {
        //TODO: have generateMove return a Move
        List<Integer> AIMove = CPU.generateMove();
        Point2D startLocation = new Point2D.Double(AIMove.get(0), AIMove.get(1));
        Point2D endLocation = new Point2D.Double(AIMove.get(2), AIMove.get(3));
        Move m = new Move(startLocation, endLocation);

        doMove(m);
        history.addMove(m);
        historyList.add(m);
        dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());
    }

    private void printMessageAndTime(String message) {
        long endTime = System.currentTimeMillis();
        System.out.println(message);
        System.out.println(String.format("time: %.2f", (float)(endTime-startTime)));
    }

    @Deprecated
    private void stall(double millis) {
        double initial = System.currentTimeMillis();
        double elapsed = 0;
        while (elapsed < millis) {
            elapsed = System.currentTimeMillis() - initial;
        }
    }

    private void doMove(Move m) {
        activePlayer.doMove(m);
        boardView.doMove(m);
        checkWon();
    }

    private void newWindow() {
        Stage newStage = new Stage();
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            Main newSimul = new Main();
            try {
                newSimul.start(newStage);
            } catch (NullPointerException e) {
                System.out.println("Null.");
            }
        }));
        thread.start();
    }
}
