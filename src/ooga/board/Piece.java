package ooga.board;

public class Piece {
  private String myName;
  private String myPattern;

  public Piece(String name, String pattern){
    myName = name;
    myPattern = pattern;
  }

  @Override
  public String toString(){
    return myName;
  }

  public String getMovePattern(){
    return myPattern;
  }

}
