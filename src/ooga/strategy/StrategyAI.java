package ooga.strategy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ooga.board.Board;
import ooga.controller.Controller.StrategyType;

public class StrategyAI {
  private StrategyType myStrategy;
  private Board myBoard;
  private List<Double> moveTimes;

  public StrategyAI(StrategyType strategy, Board board) {
    myStrategy = strategy;
    myBoard = board;
    moveTimes = new ArrayList<>();
  }

  public List<Integer> generateMove() {
    long startTime = System.currentTimeMillis();
    List<Integer> moveCoordinates;
    switch (myStrategy) {
      case TRIVIAL:
        moveCoordinates = generateTrivialMove();
      case RANDOM:
        moveCoordinates =  generateRandomMove();
      case BRUTE_FORCE:
        moveCoordinates = generateBruteForceMove();
      case ALPHA_BETA:
        moveCoordinates = generateAlphaBetaMove();
      default:
        moveCoordinates = generateTrivialMove();
    }
    long endTime = System.currentTimeMillis();
    moveTimes.add((double) startTime - endTime);
    return moveCoordinates;
  }

  public List<Integer> generateTrivialMove() {
    for (int i=0; i<myBoard.getWidth(); i++) {
      for (int j=0; j<myBoard.getHeight(); j++) {
        if (myBoard.getValidMoves(i, j) != null) {
          if (myBoard.getValidMoves(i, j).size() != 0) {
            Point2D moveTo = myBoard.getValidMoves(i, j).get(0);
            return Arrays.asList((int) moveTo.getX(), (int) moveTo.getY(), i, j);
          }
        }
      }
    }
    // TODO: replace with an exception.
    System.out.println("AI could not find a piece");
    return new ArrayList<>();
  }
  public List<Integer> generateRandomMove() {
    return null;
    // TODO: implement method
  }
  public List<Integer> generateBruteForceMove() {
    return null;
    // TODO: implement method
  }
  public List<Integer> generateAlphaBetaMove() {
    return null;
    // TODO: implement method
  }
  public double getRecentMoveTime() {
    return moveTimes.get(moveTimes.size()-1);
  }
}
