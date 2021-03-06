package ooga.player;

import ooga.board.Board;
import ooga.history.Move;
import ooga.utility.StringUtility;
import ooga.view.SetUpError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CPUPlayer extends Player {

    public static final String ERROR_MESSAGE = "Error in: ";
    public static final String GENERATE_MOVE = "generate%sMove";

    public enum StrategyType {
        TRIVIAL,
        RANDOM,
        BRUTE_FORCE,
        SINGLE_BRANCH,
        ALPHA_BETA,
    }

    private String strategy;
    private Board board;
    private String color;
    private List<Double> moveTimes;
    private StringUtility utility;

    public CPUPlayer(String name, String color, Board board, String strategy) {
        super(name, color, board);
        this.strategy = strategy;
        this.board = board;
        this.color = color;

        moveTimes = new ArrayList<>();
        utility = new StringUtility();
    }

    public Move generateMove() {
        long startTime = System.currentTimeMillis();
        Move moveCoordinates = null;

        String strategyStr = utility.strategyToString(StrategyType.valueOf(strategy));
        String generatorName = String.format(GENERATE_MOVE, strategyStr);
        try {
            Method generator = this.getClass().getDeclaredMethod(generatorName, null);
            Object coordinateList = generator.invoke(this);
            moveCoordinates = (Move) coordinateList;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SetUpError(ERROR_MESSAGE + generatorName);
        }

        moveTimes.add((double) (startTime - System.currentTimeMillis()));
        return moveCoordinates;
    }

    public Move generateTrivialMove() {
        Board board = this.getBoard();
        List<Move> possibleMoves = board.getPossibleMoves(color);
        if (possibleMoves.size() != 0) {
            return possibleMoves.get(0);
        }
        return null;
    }

    public Move generateRandomMove() {
        Random rng = new Random();
        List<Move> possibleMoves = board.getPossibleMoves(color);

        if (possibleMoves.size() != 0) {
            int index = rng.nextInt(possibleMoves.size());
            Move move = possibleMoves.get(index);
            return move;
        }
        return null;
    }

    public Move generateAlphaBetaMove() {
        int depth = 2;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

    /*
    The ALPHA-BETA algorithm will generate scores for each of these moves...
     */
        List<Move> parallelMoves = board.getPossibleMoves(color);
        List<Integer> parallelScores = new ArrayList<>();

        for (Move move : parallelMoves) {
            int moveScore = alphabeta(move, depth, alpha, beta, true, board);
            parallelScores.add(moveScore);
        }
        int bestScore = 0;
        Move bestMove = parallelMoves.get(0);
        for (int i = 0; i < parallelScores.size(); i++) {
            int score = parallelScores.get(i);
            if (score > bestScore) {
                bestMove = parallelMoves.get(i);
                bestScore = score;
            }
        }
        return bestMove;
    }

    private int alphabeta(Move move, int depth, int alpha, int beta, boolean maximizer, Board currBoard) {
        if (depth == 0 | currBoard.isGameOver()) {
            return currBoard.getScore(color);
        }
        Board nextBoard = currBoard.getCopy();
        move.setColor(color);
        nextBoard.doMove(move);
        int bestValue;
        if (maximizer) {
            bestValue = Integer.MIN_VALUE;
            for (Move nextMove : nextBoard.getPossibleMoves(color)) {
                int value = alphabeta(nextMove, depth - 1, alpha, beta, false, nextBoard);
                bestValue = Math.max(bestValue, value);
                alpha = Math.max(alpha, bestValue);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move nextMove : nextBoard.getPossibleMoves(color)) {
                int value = alphabeta(nextMove, depth - 1, alpha, beta, true, nextBoard);
                bestValue = Math.min(bestValue, value);
                beta = Math.min(beta, bestValue);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestValue;
    }

    public double getRecentMoveTime() {
        return moveTimes.get(moveTimes.size() - 1);
    }

    @Override
    public boolean isCPU() {
        return true;
    }
}
