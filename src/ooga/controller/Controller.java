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
        stage = primaryStage;
        setUpMenu();
    }

    private void setUpMenu() {
        menuScreen = new MenuScreen(this.stage);
        menuScreen.setGameButtonListener(e -> {
            try {
                parseFile(menuScreen.getFileChoice());
                setUpGameScreen(menuScreen.getGameChoice());
            } catch (SetUpError error) {
                error.show();
                error.setReturnToMenuFunction(event -> setUpMenu());
            }
        });
    }

    private void parseFile(String file) {
        processor = new JSONProcessor();
        processor.parse(file);
    }

    private void setUpGameScreen(String gameType) {
        gameType = new StringUtility().capitalize(gameType);
        instantiateBoard(gameType);

        gameScreen = new GameScreen(this.stage, board.getWidth(), board.getHeight(), processor.getPieceLocations());

        boardView = gameScreen.getBoardView();
        boardView.arrangePlayerIcons(processor.getSettings().get("icon"), menuScreen.getPlayerOneColor(), menuScreen.getPlayerTwoColor());
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
        setUpListeners();
    }

    private void instantiateBoard(String type) {
        try {
            Class boardClass = Class.forName(String.format("ooga.board.%sBoard", type));
            Constructor boardConstructor = boardClass.getDeclaredConstructor(Map.class, Map.class, Map.class, Map.class);
            board = (Board) boardConstructor.newInstance(processor.getSettings(), processor.getPieceLocations(),
                    processor.getPieceMovePatterns(), processor.getPieceScores());
        } catch (Exception e) {
            throw new SetUpError("Error creating board");
        }
    }

    private void setUpPlayers() {
        playerOne = new HumanPlayer(menuScreen.getPlayerOneName(), menuScreen.getPlayerOneColor(), board);
        if (!menuScreen.getIsGameOnePlayer()) {
            playerTwo = new HumanPlayer(menuScreen.getPlayerTwoName(), menuScreen.getPlayerTwoColor(), board);
        } else {
            playerTwo = CPU = new CPUPlayer("CPU", menuScreen.getPlayerTwoColor(), board, menuScreen.getStrategyType());
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

    private void setUpListeners() {
        setBoardViewListeners();
        setDashboardViewListeners();
    }

    private void setBoardViewListeners() {
        boardView.setOnPieceClicked(coordinate -> {
            try {
                PieceView pieceView = boardView.getCellAt(coordinate).getPieceView();
                if (pieceView.getColor().equals(activePlayer.getColor())) {
                    boardView.setSelectedLocation(coordinate);
                    boardView.highlightValidMoves(board.getValidMoves(coordinate));
                }
            } catch (SetUpError error) {
                error.show();
                error.setReturnToMenuFunction(event -> setUpMenu());
            }
        });

        boardView.setOnMoveClicked(endLocation -> {
            Point2D startLocation = boardView.getSelectedLocation();
            performStandardMove(startLocation, endLocation);

            if (checkWon()) return;

            toggleActivePlayer();

            if (activePlayer.isCPU()) {
                doCPUMove();
                toggleActivePlayer();
            }
        });
    }

    private void setDashboardViewListeners() {
        setUndoRedoListeners();

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

    private void setUndoRedoListeners() {
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

            if (checkWon()) return;

            toggleActivePlayer();
        });
    }

    private boolean checkWon() {
        try {
            String winner = board.checkWon();
            if (winner != null) {
                dashboardView.setWinner(winner);
                dashboardView.setUpWinnerPopup();
                return true;
            }
        } catch (Exception e) {
            SetUpError error = new SetUpError(e.getMessage());
            error.show();
            error.setReturnToMenuFunction(event -> setUpMenu());
        }
        return false;
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
        Move AIMove = CPU.generateMove();
        Point2D startLocation = AIMove.getStartLocation();
        Point2D endLocation = AIMove.getEndLocation();
        performStandardMove(startLocation, endLocation);

        checkWon();
    }

    private void performStandardMove(Point2D startLocation, Point2D endLocation) {
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
        try {
            activePlayer.doMove(move);
            boardView.doMove(move);
        } catch (Exception e) {
            SetUpError error = new SetUpError(e.getMessage());
            error.show();
            error.setReturnToMenuFunction(event -> setUpMenu());
        }
    }

    private void newWindow() {
        Stage newStage = new Stage();
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            Controller newSimulation = new Controller();
            try {
                newSimulation.start(newStage);
            } catch (NullPointerException e) {
                SetUpError error = new SetUpError("Error creating new window");
                error.show();
                error.setReturnToMenuFunction(event -> setUpMenu());
            }
        }));
        thread.start();
    }
}
