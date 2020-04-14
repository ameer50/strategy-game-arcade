package ooga.strategy;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import ooga.board.Board;
import ooga.controller.Controller.StrategyType;
import ooga.controller.StringUtility;

public class StrategyAI extends Player {
  private StrategyType strategy;
  private Board board;
  private String color;
  private List<Double> moveTimes;
  private StringUtility utility;

  public StrategyAI(String name, String color, Board board, StrategyType strategy) {
    super(name, color, board);
    this.strategy = strategy;
    this.board = board;
    this.color = color;
    moveTimes = new ArrayList<>();
    utility = new StringUtility();
  }

  public List<Integer> generateMove() {
    long startTime = System.currentTimeMillis();
    List<Integer> moveCoordinates = null;

    String strategyString = utility.strategyToString(strategy);
    String methodName = String.format("generate%sMove", strategyString);
    try { ;
      Method generator = this.getClass().getDeclaredMethod(methodName, null);
      Object coordinateList = generator.invoke(this);
      moveCoordinates = (List<Integer>) coordinateList;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      System.out.println("Error: " + methodName);
    }

    moveTimes.add((double) (startTime - System.currentTimeMillis()));
    return moveCoordinates;
  }

  public List<Integer> generateTrivialMove() {
    Board board = this.getBoard();
    for (int i=0; i<board.getWidth(); i++) {
      for (int j=0; j<board.getHeight(); j++) {
        List<Point2D> validMoves = board.getValidMoves(i, j);
        if (validMoves != null) {
          if (validMoves.size() != 0) {
            Point2D moveTo = validMoves.get(0);
            System.out.println(String.format("Generated TRIVIAL move: (%.1f, %.1f), (%.1f, %.1f)",
                (float) i, (float) j, (float) moveTo.getX(), (float) moveTo.getY()));
            return Arrays.asList(i, j, (int) moveTo.getX(), (int) moveTo.getY());
          }
        }
      }
    }
    // TODO: replace with an exception.
    System.out.println("AI could not find a piece");
    return null;
  }

  public List<Integer> generateRandomMove() {
    Random rng = new Random();
    List<List<Integer>> moves = new ArrayList<>();
    for (int i = 0; i < board.getWidth(); i++) {
      for (int j = 0; j < board.getHeight(); j++) {
        List<Point2D> pieceMoves = board.getValidMoves(i, j);
        if (pieceMoves != null) {
          if (pieceMoves.size() != 0 && board.getPieceAt(i, j).getColor().equals(color)) {
            addAllPieceMoves(moves, pieceMoves, i, j);
          }
        }
      }
    }
    if (moves.size() == 0) {
      System.out.println("AI could not find a piece");
      return null;
    } else {
      int index = rng.nextInt(moves.size());
      List<Integer> move = moves.get(index);
      System.out.println(String.format("Generated RANDOM move: (%.1f, %.1f), (%.1f, %.1f)", (float) move.get(0),
          (float) move.get(1), (float) move.get(2), (float) move.get(3)));
      return moves.get(index);
    }
  }

  private void addAllPieceMoves(List<List<Integer>> moves, List<Point2D> pieceMoves, int i, int j) {
      for (Point2D moveTo: pieceMoves) {
        List<Integer> move = Arrays.asList(i, j, (int) moveTo.getX(), (int) moveTo.getY());
        moves.add(move);
      }
    }

  public List<Integer> generateAlphaBetaMove() {
    int branches = 4;
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;


    return null;
  }

  public List<Integer> generateBruteForceMove() {
    return null;
    // TODO: Implement.
  }
  public List<Integer> generateSingleBranchMove() {
    // TODO: Implement.
    return null;
  }

  public double getRecentMoveTime() {
    return moveTimes.get(moveTimes.size()-1);
  }

  @Override
  public boolean isCPU() {
    return true;
  }
}
