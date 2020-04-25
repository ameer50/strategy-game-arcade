package ooga.custom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.CustomBoard;
import ooga.history.History;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import ooga.player.CPUPlayer;
import ooga.player.HumanPlayer;
import ooga.player.Player;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.PieceView;

import java.awt.geom.Point2D;

public class TestingController {
    private JSONProcessor processor;
    private Board board;
    private GameScreen gameScreen;
    private BoardView boardView;
    private Stage stage;
    private Player playerOne;
    private Player playerTwo;
    private Player activePlayer;
    private CPUPlayer CPU;
    private History history;
    private ObservableList historyList;
    private boolean isCPU = false;
    private long startTime;

    public TestingController(Stage stage) {
        startTime = System.currentTimeMillis();
        this.stage = stage;
        setUpGameScreen("resources/defaultBlack.json");
    }

    private void setUpGameScreen(String fileChoice) {
        processor = new JSONProcessor();
        processor.parse(fileChoice);
        printMessageAndTime("JSON parsed.");
        /* To be used in reflection... */
        int width = processor.getWidth();
        int height = processor.getHeight();

        /* Use reflection here... */
        board = new CustomBoard(processor.getSettings(), processor.getPieceLocations(),
                processor.getPieceMovePatterns(), processor.getPieceScores());
        printMessageAndTime("Set up Board.");

        gameScreen = new GameScreen(stage, width, height, processor.getPieceLocations());
        printMessageAndTime("Set up Game Screen.");

        boardView = gameScreen.getBoardView();
        setUpHistory();
        setUpPlayers();
        setListeners();
    }

    private void setUpPlayers() {
        playerOne = new HumanPlayer("P1", "White", board);
        if (!isCPU) {
            playerTwo = new HumanPlayer("P2", "Black", board);
        } else {
            playerTwo = CPU = new CPUPlayer("CPU", "Black", board, "Trivial");
        }
        gameScreen.getDashboardView().setPlayerNames("P1", "P2");
        gameScreen.getDashboardView().bindScores(playerOne.getScore(), playerTwo.getScore());

        activePlayer = (playerOne.getColor().equals("White")) ? playerOne : playerTwo;
        if (activePlayer.isCPU()) doCPUMove();
        gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void setUpHistory() {
        history = new History();
        historyList = FXCollections.observableArrayList();
        gameScreen.getDashboardView().getHistoryDisplay().setItems(historyList);
    }

    private void setListeners() {
        /* X and Y are the indices of the cell clicked to move FROM */
        boardView.setOnPieceClicked((coordinate) -> {
            System.out.println(boardView.getCellAt(coordinate).getPieceView().getColor()); // ***
            if (!boardView.getCellAt(coordinate).getPieceView().getColor().equals(activePlayer.getColor())) {
                return;
            }
            boardView.setSelectedLocation(coordinate);
            boardView.highlightValidMoves(board.getValidMoves(coordinate));
            System.out.println("Highlighted moves.");
        });

        /* X and Y are the indices of the cell clicked to move TO */
        boardView.setOnMoveClicked((coordinate) -> {
            Point2D startLocation = boardView.getSelectedLocation();
            Point2D endLocation = coordinate;

            printMessageAndTime("Did user's move.");

            Move move = new Move(startLocation, endLocation);
            doMove(move);

            history.addMove(move);
            historyList.add(move);
            toggleActivePlayer();
            board.checkWon();

            if (activePlayer.isCPU()) {
                doCPUMove();
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
            reverseMove.setUndo(true);
            doMove(reverseMove);
            toggleActivePlayer();

//      Map<Piece, Point2D> map = prevMove.getCapturedPiecesAndLocations();
//      for (Piece capturedPiece: map.keySet()) {
//        Point2D capturedPieceLocation = map.get(capturedPiece);
//        board.putPieceAt(capturedPieceLocation, capturedPiece);
//        activePlayer.addToScore(-capturedPiece.getValue());
//        PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
//        boardView.getCellAt(capturedPieceLocation).setPieceView(capturedPieceView);
//      }
        });

        gameScreen.getDashboardView().setRedoMoveClicked((e) -> {
            Move prevMove = history.redo();
            historyList.add(prevMove);

            doMove(prevMove);
            toggleActivePlayer();
        });

        gameScreen.getDashboardView().setReturnToMenuClicked((e) -> {
            // FIXME: restore this later
            // setUpMenu();
        });

        gameScreen.getDashboardView().setSaveClicked((e) -> {
            // FIXME: actually implement this.
            // JSONprocessor.write(board, gameScreen.getDashboardView().getNewFileName());
        });

        board.setOnPiecePromoted((coordinate) -> {
            board.getPieceAt(coordinate);
            boardView.getCellAt(coordinate).setPieceView(new PieceView(board.getPieceAt(coordinate).getFullName()));
        });
    }

    private void toggleActivePlayer() {
        activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
        gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
    }

    private void doCPUMove() {
        //TODO: have generateMove return a Move
        Move AIMove = CPU.generateMove();
        doMove(AIMove);
        history.addMove(AIMove);
        historyList.add(AIMove);

        toggleActivePlayer();
        board.checkWon();
    }

    private void doMove(Move m) {
        activePlayer.doMove(m);
        boardView.doMove(m);
    }

    private void printMessageAndTime(String message) {
        long endTime = System.currentTimeMillis();
        System.out.println(message);
        System.out.println(String.format("time: %.2f", (float) (endTime - startTime)));
    }
}
