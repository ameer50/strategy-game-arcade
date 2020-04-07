package ooga.board;

import java.util.List;

public class ChessBoard extends Board{
  public ChessBoard(int height, int width){
    super(height, width);
  }

  @Override
  public boolean checkWon() {
    return false;
  }

  @Override
  public List<String> getValidMoves(int x, int y) {
    return null;
  }

  @Override
  public double doMove(int x, int y, String move) throws NoSuchMethodException {
    return 0;
  }

}
