package ooga.testing;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.CheckersBoard;
import ooga.board.ChessBoard;
import ooga.board.Piece;
import ooga.controller.Controller.GameType;
import ooga.controller.Controller.StrategyType;
import ooga.history.History;
import ooga.history.Move;
import ooga.player.CPUPlayer;
import ooga.player.HumanPlayer;
import ooga.player.Player;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.PieceView;
import ooga.xml.XMLProcessor;

public class CustomController {
  private XMLProcessor processor;
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
  private boolean isCPU = true;
  private long startTime;

  public CustomController(Stage stage) {
    startTime = System.currentTimeMillis();
    this.stage = stage;
    setUpGameScreen("Chess", "resources/Chess/defaultBlack.xml");
  }

  private void setUpGameScreen(String gameChoice, String fileChoice) {
    GameType gameType = GameType.valueOf(gameChoice.toUpperCase());
    String gameXML = String.format(fileChoice);

    processor = new XMLProcessor();
    processor.parse(gameXML);
    printMessageAndTime("XML parsed.");

    //TODO: Change to reflection.
    switch (gameType) {
      case CHESS:
        board = new ChessBoard(processor.getSettings(), processor.getInitialPieceLocations(),
            processor.getMovePatterns());
        break;
      case CHECKERS:
        board = new CheckersBoard(processor.getSettings(), processor.getInitialPieceLocations(),
            processor.getMovePatterns());
    } printMessageAndTime("Setup Board.");

    gameScreen = new GameScreen(stage, board.getWidth(), board.getHeight(), processor.getInitialPieceLocations()); // ***
    printMessageAndTime("Setup Game Screen.");

    boardView = gameScreen.getBoardView();
    setUpHistory();
    setUpPlayers();
    setListeners();
  }

  private void setUpPlayers() {
    playerOne = new HumanPlayer("PLAYER ONE", "White", board);
    if (isCPU) {
      playerTwo = new HumanPlayer("PLAYER TWO", "Black", board);
    } else {
      playerTwo = CPU = new CPUPlayer("CPU", "Black", board, StrategyType.TRIVIAL);
    }
    gameScreen.getDashboardView().setPlayerNames(playerOne.getName(), playerTwo.getName());
    gameScreen.getDashboardView().bindScores(playerOne.getScore(), playerTwo.getScore());

    activePlayer = (playerOne.getColor().equals("White")) ? playerOne : playerTwo;
    if (activePlayer.isCPU()) doCPUMove();
    gameScreen.getDashboardView().setActivePlayerText(activePlayer.getName(), activePlayer.getColor());
  }

  private void setUpHistory() {
    history = new History();
    historyList = FXCollections.observableArrayList();
    gameScreen.getDashboardView().getHistory().setItems(historyList);
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
      reverseMove.setUndoTrue();

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
      // setUpMenu();
    });

    gameScreen.getDashboardView().setSaveClicked((e) -> {
      //TODO: change fileName to be an input
      processor.write(board, gameScreen.getDashboardView().getNewFileName());
    });

    board.setOnPiecePromoted((int toX, int toY) -> {
      board.getPieceAt(toX, toY);
      boardView.getCellAt(toX, toY).setPiece(new PieceView(board.getPieceAt(toX, toY).getFullName()));
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
