package ooga.player;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import ooga.board.Board;
import ooga.controller.Controller.StrategyType;
import ooga.utility.StringUtility;
import ooga.history.Move;

public class CPUPlayer extends Player {
  private StrategyType strategy;
  private Board board;
  private String color;
  private List<Double> moveTimes;
  private StringUtility utility;

  public CPUPlayer(String name, String color, Board board, StrategyType strategy) {
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

    String strategyStr = utility.strategyToString(strategy);
    String generatorName = String.format("generate%sMove", strategyStr);
    try {
      Method generator = this.getClass().getDeclaredMethod(generatorName, null);
      Object coordinateList = generator.invoke(this);
      moveCoordinates = (List<Integer>) coordinateList;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      System.out.println("Error: " + generatorName);
      e.printStackTrace();
      // FIXME: Don't print stack trace.
    }

    moveTimes.add((double) (startTime - System.currentTimeMillis()));
    return moveCoordinates;
  }

  public List<Integer> generateTrivialMove() {
    Board board = this.getBoard();
    for (int i=0; i<board.getWidth(); i++) {
      for (int j=0; j<board.getHeight(); j++) {
        List<Point2D> validMoves = board.getValidMoves(new Point2D.Double(i, j));
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
    List<List<Integer>> possibleMoves = board.getPossibleMoves(color);

    if (possibleMoves.size() != 0) {
      int index = rng.nextInt(possibleMoves.size());
      List<Integer> move = possibleMoves.get(index);
      System.out.println(String.format("Generated RANDOM move: (%.1f, %.1f), (%.1f, %.1f)", (float) move.get(0),
          (float) move.get(1), (float) move.get(2), (float) move.get(3)));
      return move;
    }
    System.out.println("AI could not find a piece");
    return null;
  }

  public List<Integer> generateAlphaBetaMove() {
    int depth = 2;
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;

    /*
    The ALPHA-BETA algorithm will generate scores for each of these moves...
     */
    List<List<Integer>> parallelMoves = board.getPossibleMoves(color);
    List<Integer> parallelScores = new ArrayList<>();

    for (List<Integer> move: parallelMoves) {
      int moveScore = alphabeta(move, depth, alpha, beta, true, board);
      parallelScores.add(moveScore);
    }
    int bestScore = 0;
    List<Integer> bestMove = parallelMoves.get(0);
    for (int i=0; i<parallelScores.size(); i++) {
      int score = parallelScores.get(i);
      if (score > bestScore) {
        bestMove = parallelMoves.get(i);
        bestScore = score;
      }
    }
    return bestMove;
  }

  private int alphabeta(List<Integer> currMove, int depth, int alpha, int beta, boolean maximizer, Board currBoard) {
    if (depth==0 | currBoard.isGameOver()) {
      return currBoard.getScore(color);
    }
    Board nextBoard = currBoard.getCopy();
    Point2D startPoint = new Point2D.Double(currMove.get(0), currMove.get(1));
    Point2D endPoint = new Point2D.Double(currMove.get(2), currMove.get(3));
    nextBoard.doMove(new Move(startPoint, endPoint));
    int bestValue;
    if (maximizer) {
      bestValue = Integer.MIN_VALUE;
      for (List<Integer> nextMove : nextBoard.getPossibleMoves(color)) {
        int value = alphabeta(nextMove, depth-1, alpha, beta, false, nextBoard);
        bestValue = Math.max(bestValue, value);
        alpha = Math.max(alpha, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
    } else {
      bestValue = Integer.MAX_VALUE;
      for (List<Integer> nextMove : nextBoard.getPossibleMoves(color)) {
        int value = alphabeta(nextMove, depth-1, alpha, beta, true, nextBoard);
        bestValue = Math.min(bestValue, value);
        beta = Math.min(beta, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
    }
    return bestValue;
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
