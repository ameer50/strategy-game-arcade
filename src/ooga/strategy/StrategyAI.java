package ooga.strategy;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ooga.board.Board;
import ooga.controller.Controller.StrategyType;

public class StrategyAI extends Player {
  private StrategyType strategy;
  private List<Double> moveTimes;

  public StrategyAI(String name, Color color, Board board, StrategyType strategy) {
    super(name, color, board);
    this.strategy = strategy;
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
    Board board = this.getBoard();
    for (int i=0; i<board.getWidth(); i++) {
      for (int j=0; j<board.getHeight(); j++) {
        List<Point2D> validMoves = board.getValidMoves(i, j, color);
        if (validMoves != null) {
          if (validMoves.size() != 0) {
            Point2D moveTo = validMoves.get(0);
            return Arrays.asList(i, j, (int) moveTo.getX(), (int) moveTo.getY());
          }
        }
      }
    }
    // TODO: replace with an exception.
    System.out.println("AI could not find a piece");
    return new ArrayList<>();
  }
  public List<Integer> generateRandomMove(String color) {
    return null;
    // TODO: implement method
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

  @Override
  public boolean isCPU() {
    return true;
  }
}
