package ooga.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Pair;
import ooga.board.*;
import ooga.history.History;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import ooga.player.HumanPlayer;
import ooga.player.Player;
import ooga.player.CPUPlayer;
import ooga.utility.StringUtility;
import ooga.view.*;

import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.Map;

public class Controller extends Application {

    public enum StrategyType {
        TRIVIAL,
        RANDOM,
        BRUTE_FORCE,
        SINGLE_BRANCH,
        ALPHA_BETA,;
    }

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
    private JSONProcessor processor;

    /**
     * Start of the program.
     */
    public static void main (String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        startTime = System.currentTimeMillis();
        stage = primaryStage;
        setUpMenu();
    }

    private void setUpMenu() {
        menuScreen = new MenuScreen(this.stage);

        menuScreen.setGameButtonListener(e -> {
            setUpGameScreen(menuScreen.getGameChoice(), menuScreen.getFileChoice());
        });
    }

    private void setUpGameScreen(String gameType, String dir) {
        processor = new JSONProcessor();
        processor.parse(dir);

        gameType = new StringUtility().capitalize(gameType);
        instantiateBoard(gameType);

        gameScreen = new GameScreen(this.stage, board.getWidth(), board.getHeight(), processor.getPieceLocations());

        boardView = gameScreen.getBoardView();
        boardView.arrangePlayerIcons(processor.getSettings().get("icon"), menuScreen.getPlayerOneColor(),
            menuScreen.getPlayerTwoColor());
        dashboardView = gameScreen.getDashboardView();
        dashboardView.addIcons(boardView.getIcons());
        board.addPlayerIcons(menuScreen.getPlayerOneColor(), menuScreen.getPlayerTwoColor());

        if (menuScreen.isDarkMode()){
            gameScreen.toggleGameDarkMode();
            dashboardView.toggleDarkMode();
        }

        gameScreen.enableGameCSS(menuScreen.getGameChoice());
        setUpHistory();
        setUpPlayers();
        setListeners();
    }

    private void instantiateBoard(String type) {
        try {
            Class boardClass = Class.forName(String.format("ooga.board.%sBoard", type));
            Constructor boardConstructor = boardClass.getDeclaredConstructor(Map.class, Map.class, Map.class, Map.class);
            board = (Board) boardConstructor.newInstance(processor.getSettings(), processor.getPieceLocations(),
                processor.getPieceMovePatterns(), processor.getPieceScores());
        } catch (Exception e) {
            new DisplayError("Could not find game");
        }
    }

    private void setUpPlayers() {
        playerOne = new HumanPlayer(menuScreen.getPlayerOneName(), menuScreen.getPlayerOneColor(), board);
        if (!menuScreen.getIsGameOnePlayer()) {
            playerTwo = new HumanPlayer(menuScreen.getPlayerTwoName(), menuScreen.getPlayerTwoColor(), board);
        } else {
            String strategyType = menuScreen.getStrategyType();
            playerTwo = CPU = new CPUPlayer("CPU", menuScreen.getPlayerTwoColor(), board, StrategyType.valueOf(strategyType));
        }
        dashboardView.setPlayerNames(playerOne.getName(), playerTwo.getName());
        dashboardView.bindScores(playerOne.getScore(), playerTwo.getScore());

        activePlayer = playerOne;
        gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void setUpHistory() {
        history = new History();
        historyList = FXCollections.observableArrayList();
        dashboardView.getHistoryDisplay().setItems(historyList);
    }

    private void setListeners() {
        setBoardViewListeners();
        setDashboardViewListeners();
    }

    private void setBoardViewListeners() {
        boardView.setOnPieceClicked(coordinate -> {
            PieceView pieceView = boardView.getCellAt(coordinate).getPieceView();
            if (pieceView.getColor().equals(activePlayer.getColor())) {
                boardView.setSelectedLocation(coordinate);
                boardView.highlightValidMoves(board.getValidMoves(coordinate));
            }
        });

        boardView.setOnMoveClicked(endLocation -> {
            Point2D startLocation = boardView.getSelectedLocation();
            Move move = new Move(startLocation, endLocation);
            doMove(move);
            convertPieces(move);
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
        dashboardView.setUndoMoveClicked(e -> {
            toggleActivePlayer();

            Move prevMove = history.undo();
            historyList.remove(historyList.size() - 1);
            Move reverseMove = prevMove.getReverseMove();
            convertPieces(reverseMove);
            doMove(reverseMove);

            dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());

            replenishCapturedPieces(reverseMove);
        });

        dashboardView.setRedoMoveClicked(e -> {
            Move prevMove = history.redo();
            historyList.add(prevMove);
            doMove(prevMove);
            convertPieces(prevMove);
            removeCapturedPieces(prevMove);
            boardView.replenishIcon(prevMove);

            dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());
            toggleActivePlayer();
        });

        dashboardView.setSaveClicked(e -> {
            processor.writeLocations(board, gameScreen.getDashboardView().getNewFileName());
        });

        dashboardView.setSkipTurnClicked(e -> {
            toggleActivePlayer();
            if (activePlayer.isCPU()) {
                doCPUMove();
                toggleActivePlayer();
            }
        });

        dashboardView.setNewWindowClicked(e -> {
            newWindow();
        });

        dashboardView.setReturnToMenuClicked(e -> {
            setUpMenu();
        });
    }

    private void checkWon() {
        String winner = board.checkWon();
        if (winner != null) {
            dashboardView.setWinner(winner);
            dashboardView.setUpWinnerPopup();
        }
    }

    private void removeCapturedPieces(Move move) {
        for (Point2D location: move.getCapturedPiecesAndLocations().keySet()) {
            if (board.getPieceAt(location) == null) boardView.getCellAt(location).setPieceView(null);
        }
    }

    private void replenishCapturedPieces(Move prevMove) {
        Map<Point2D, Piece> map = prevMove.getCapturedPiecesAndLocations();
        for (Point2D capturedPieceLocation: map.keySet()) {
            Piece capturedPiece = map.get(capturedPieceLocation);
            insertPiece(capturedPiece, capturedPieceLocation);
        }
    }

    private void convertPieces(Move m) {
        Map<Point2D, Pair<Piece, Piece>> map = m.getConvertedPiecesAndLocations();
        for (Point2D convertedPieceLocation: map.keySet()) {
            Piece convertedPiece = m.isUndo() ? map.get(convertedPieceLocation).getKey() : map.get(convertedPieceLocation).getValue();
            insertPiece(convertedPiece, convertedPieceLocation);
        }
    }

    private void insertPiece(Piece piece, Point2D location) {
        board.putPieceAt(location, piece);
        PieceView pieceView = new PieceView(piece.getFullName());
        boardView.getCellAt(location).setPieceView(pieceView);
    }


    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
        dashboardView.setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void doCPUMove() {
        //TODO: Have generateMove return a Move.
        Move AIMove = CPU.generateMove();
        Point2D startLocation = AIMove.getStartLocation();
        Point2D endLocation = AIMove.getEndLocation();
        Move m = new Move(startLocation, endLocation);

        doMove(m);
        convertPieces(m);
        removeCapturedPieces(m);
        boardView.replenishIcon(m);

        history.addMove(m);
        historyList.add(m);
        dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());
    }

    private void doMove(Move move) {
        activePlayer.doMove(move);
        boardView.doMove(move);
        checkWon();
    }

    private void newWindow() {
        Stage newStage = new Stage();
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            Controller newSimulation = new Controller();
            try {
                newSimulation.start(newStage);
            } catch (NullPointerException e) {
                new DisplayError("No simulation created");
            }
        }));
        thread.start();
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
}
