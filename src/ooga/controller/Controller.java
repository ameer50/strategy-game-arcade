package ooga.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.Main;
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
import java.util.List;
import java.util.Map;

public class Controller {

    public enum StrategyType {
        TRIVIAL,
        RANDOM,
        BRUTE_FORCE,
        SINGLE_BRANCH,
        ALPHA_BETA,
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

    public Controller(Stage stage) {
        startTime = System.currentTimeMillis();
        this.stage = stage;
        setUpMenu();
    }

    private void setUpMenu() {
        menuScreen = new MenuScreen(this.stage);
        menuScreen.setGameButtonListener(e -> {
            setUpGameScreen(menuScreen.getGameChoice(), menuScreen.getFileChoice());
        });
    }

    private void setUpGameScreen(String gameType, String dir) {
        /* TODO: Change 'Preset' to something received from the UI */
        String customOrPreset = "Preset";
        boolean isCustom = customOrPreset.equals("Custom");
        processor = new JSONProcessor();
        processor.parse(dir, isCustom);
        gameType = new StringUtility().capitalize(gameType);
        instantiateBoard(gameType, isCustom);

        gameScreen = new GameScreen(this.stage, board.getWidth(), board.getHeight(), processor.getPieceLocations());

        boardView = gameScreen.getBoardView();
        boardView.arrangePlayerIcons(processor.getSettings().get("icon"), menuScreen.getPlayerOneColor(),
            menuScreen.getPlayerTwoColor());
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

    private void instantiateBoard(String type, boolean isCustom) {
        if (isCustom) {
            board = new CustomBoard(processor.getWidth(), processor.getHeight(), processor.getSettings(),
                processor.getPieceLocations(), processor.getPieceMoveNodes(), processor.getPieceScores());
            return;
        }
        try {
            Class boardClass = Class.forName(String.format("ooga.board.%sBoard", type));
            Constructor boardConstructor = boardClass.getDeclaredConstructor(Map.class, Map.class, Map.class, Map.class);
            board = (Board) boardConstructor.newInstance(processor.getSettings(), processor.getPieceLocations(),
                processor.getPieceMovePatterns(), processor.getPieceScores());
        } catch (Exception e) {
            System.out.println("Could not find game.");
        }
    }

    private void setUpPlayers() {
        playerOne = new HumanPlayer(menuScreen.getPlayerOneName(), menuScreen.getPlayerOneColor(), board);
        if (!menuScreen.getIsGameOnePlayer()) {
            playerTwo = new HumanPlayer(menuScreen.getPlayerTwoName(), menuScreen.getPlayerTwoColor(), board);
        } else {
            // TODO: Determine the StrategyType dynamically.
            playerTwo = CPU = new CPUPlayer("CPU", menuScreen.getPlayerTwoColor(), board, StrategyType.ALPHA_BETA);
        }
        dashboardView.setPlayerNames(playerOne.getName(), playerTwo.getName());
        dashboardView.bindScores(playerOne.getScore(), playerTwo.getScore());

        // TODO: Change "White" to the color that the player chose
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
        board.setOnPiecePromoted((coordinate) -> {
            boardView.getCellAt(coordinate).setPieceView(new PieceView(board.getPieceAt(coordinate).getFullName()));
        });
    }

    private void setBoardViewListeners() {
        boardView.setOnPieceClicked((coordinate) -> {
            PieceView pieceView = boardView.getCellAt(coordinate).getPieceView();
            if (pieceView.getColor().equals(activePlayer.getColor())) {
                boardView.setSelectedLocation(coordinate);
                boardView.highlightValidMoves(board.getValidMoves(coordinate));
            }
        });

        boardView.setOnMoveClicked((endLocation) -> {
            Point2D startLocation = boardView.getSelectedLocation();
            Move move = new Move(startLocation, endLocation);
            doMove(move);
            removeCapturedPieces(move);
            //convertPieces(move);
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
            historyList.remove(historyList.size()-1);

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
            processor.writeLocations(board, gameScreen.getDashboardView().getNewFileName());
        });
    }

    private void checkWon() {
        String winner = board.checkWon();
        if (winner != null) {
            dashboardView.setWinner(winner);
            dashboardView.setUpWinnerStage();
        }
    }

    private void removeCapturedPieces(Move move) {
        for (Point2D location: move.getCapturedPiecesAndLocations().values()) {
            if (board.getPieceAt(location) == null) boardView.getCellAt(location).setPieceView(null);
        }
    }

//    private void convertPieces(Move m) {
//        System.out.println("it is called");
//        for (Point2D location: m.getConvertedPiecesAndLocations().values()) {
//            Piece
//            if (board.getPieceAt(location) != null) boardView.getCellAt(location).setPiece(null);
//        }
//    }

//    private void convertPieces(Move m) {
//        Map<Piece, Point2D> map = m.getConvertedPiecesAndLocations();
//
//        for (Piece piece: map.keySet()) {
//            Point2D convertedPieceLocation = map.get(piece);
//            activePlayer.addToScore(piece.getValue());
//            PieceView convertedPieceView = new PieceView(m.getPiece().getFullName());
//            boardView.getCellAt(convertedPieceLocation).setPiece(convertedPieceView);
//        }
//    }

    private void replenishCapturedPieces(Move prevMove) {
        Map<Piece, Point2D> map = prevMove.getCapturedPiecesAndLocations();
        for (Piece capturedPiece: map.keySet()) {
            Point2D capturedPieceLocation = map.get(capturedPiece);
            board.putPieceAt(capturedPieceLocation, capturedPiece);
            activePlayer.addToScore(-capturedPiece.getValue());
            PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
            boardView.getCellAt(capturedPieceLocation).setPieceView(capturedPieceView);
        }
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
            Main newSimulation = new Main();
            try {
                newSimulation.start(newStage);
            } catch (NullPointerException e) {
                System.out.println("Null.");
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
