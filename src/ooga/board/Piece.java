package ooga.board;

public class Piece {
  private String movePattern;

  public Piece(String pattern){
    movePattern = pattern;
  }

  @Override
  public String toString(){
    return movePattern;
  }

  public String getMovePattern(){
    return movePattern;
  }

}
