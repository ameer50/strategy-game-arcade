package ooga.custom;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.CustomBoard;
import ooga.board.Piece;
import ooga.controller.Controller.GameType;
import ooga.controller.Controller.StrategyType;
import ooga.history.History;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import ooga.player.CPUPlayer;
import ooga.player.HumanPlayer;
import ooga.player.Player;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.PieceView;
import ooga.xml.XMLProcessor;

public class CustomController {
  private XMLProcessor XMLprocessor;
  private JSONProcessor JSONprocessor;
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

  public CustomController(Stage stage) {
    startTime = System.currentTimeMillis();
    this.stage = stage;
    setUpGameScreen("custom.json");
  }

  private void setUpGameScreen(String fileChoice) {
    JSONprocessor = new JSONProcessor();
    JSONprocessor.parse(fileChoice);
    printMessageAndTime("JSON parsed.");
    /* To be used in reflection... */
    GameType type = GameType.valueOf(JSONprocessor.getName().toUpperCase());

    Map<String, Long> dimensions = JSONprocessor.getDimensions();
    int width = Math.toIntExact(dimensions.get("width"));
    int height = Math.toIntExact(dimensions.get("height"));

    /* Use reflection here... */
    board = new CustomBoard(width, height, JSONprocessor.getPieceLocations(),
        JSONprocessor.getPieceMoves(), JSONprocessor.getPieceScores());
    printMessageAndTime("Set up Board.");

    gameScreen = new GameScreen(stage, width, height, JSONprocessor.getPieceLocations());
    printMessageAndTime("Set up Game Screen.");

    boardView = gameScreen.getBoardView();
    setUpHistory();
    setUpPlayers();
    setListeners();
  }

  private void setUpPlayers() {
    playerOne = new HumanPlayer("P1", "White", board);
    if (! isCPU) {
      playerTwo = new HumanPlayer("P2", "Black", board);
    } else {
      playerTwo = CPU = new CPUPlayer("CPU", "Black", board, StrategyType.TRIVIAL);
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
      System.out.println(boardView.getCellAt(coordinate).getPiece().getColor()); // ***
      if (! boardView.getCellAt(coordinate).getPiece().getColor().equals(activePlayer.getColor())) {
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

      Map<Piece, Point2D> map = prevMove.getCapturedPiecesAndLocations();
      for (Piece capturedPiece: map.keySet()) {
        Point2D capturedPieceLocation = map.get(capturedPiece);
        board.putPieceAt(capturedPieceLocation, capturedPiece);
        activePlayer.addToScore(-capturedPiece.getValue());
        PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
        boardView.getCellAt(capturedPieceLocation).setPiece(capturedPieceView);
      }
    });

    gameScreen.getDashboardView().setRedoMoveClicked((e) -> {
      Move prevMove = history.redo();
      historyList.add(prevMove);

      doMove(prevMove);
      toggleActivePlayer();
    });

    gameScreen.getDashboardView().setQuitClicked((e) -> {
      // FIXME: restore this later
      // setUpMenu();
    });

    gameScreen.getDashboardView().setSaveClicked((e) -> {
      // FIXME: actually implement this.
      // JSONprocessor.write(board, gameScreen.getDashboardView().getNewFileName());
    });

    board.setOnPiecePromoted((coordinate) -> {
      board.getPieceAt(coordinate);
      boardView.getCellAt(coordinate).setPiece(new PieceView(board.getPieceAt(coordinate).getFullName()));
    });
  }

  private void toggleActivePlayer() {
    activePlayer = (activePlayer == playerOne) ? playerTwo : playerOne;
    gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
  }

  private void doCPUMove() {
    //TODO: have generateMove return a Move
    List<Integer> AIMove = CPU.generateMove();
    int fromX = AIMove.get(0);
    int fromY = AIMove.get(1);
    Point2D startLocation = new Point2D.Double(fromX, fromY);
    int toX = AIMove.get(2);
    int toY = AIMove.get(3);
    Point2D endLocation = new Point2D.Double(toX, toY);
    Move m = new Move(startLocation, endLocation);

    doMove(m);
    history.addMove(m);
    historyList.add(m);

    toggleActivePlayer();
    board.checkWon();
  }

  private void doMove(Move m) {
    activePlayer.doMove(m);
    boardView.doMove(m);
  }

  private void printMessageAndTime (String message) {
    long endTime = System.currentTimeMillis();
    System.out.println(message);
    System.out.println(String.format("time: %.2f", (float)(endTime-startTime)));
  }
}
