package ooga.strategy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import ooga.board.Board;
import ooga.controller.Controller.StrategyType;

public class StrategyAI {
  private StrategyType strategy;
  private Board board;
  private List<Double> moveTimes;

  public StrategyAI(StrategyType strategy, Board board) {
    this.strategy = strategy;
    this.board = board;
    moveTimes = new ArrayList<>();
  }

  public List<Integer> generateMove(String color) {
    long startTime = System.currentTimeMillis();
    List<Integer> moveCoordinates;
    switch (strategy) {
      case TRIVIAL:
        moveCoordinates = generateTrivialMove(color);
      case RANDOM:
        moveCoordinates =  generateRandomMove(color);
      case BRUTE_FORCE:
        moveCoordinates = generateBruteForceMove(color);
      case ALPHA_BETA:
        moveCoordinates = generateAlphaBetaMove(color);
      default:
        moveCoordinates = generateTrivialMove(color);
    }
    long endTime = System.currentTimeMillis();
    moveTimes.add((double) startTime - endTime);
    return moveCoordinates;
  }

  public List<Integer> generateTrivialMove(String color) {
    for (int i=0; i<board.getWidth(); i++) {
      for (int j=0; j<board.getHeight(); j++) {
        List<Point2D> validMoves = board.getValidMoves(i, j, color);
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

  public List<Integer> generateRandomMove(String color) {
    Random rng = new Random();
    List<List<Integer>> moves = new ArrayList<>();
    for (int i = 0; i < board.getWidth(); i++) {
      for (int j = 0; j < board.getHeight(); j++) {
        List<Point2D> pieceMoves = board.getValidMoves(i, j, color);
        if (pieceMoves != null) {
          if (pieceMoves.size() != 0) {
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

  public List<Integer> generateBruteForceMove(String color) {
    return null;
    // TODO: implement method
  }
  public List<Integer> generateAlphaBetaMove(String color) {
    return null;
    // TODO: implement method
  }
  public double getRecentMoveTime() {
    return moveTimes.get(moveTimes.size()-1);
  }
}
