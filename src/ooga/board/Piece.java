package ooga.board;

public class Piece {
  private String myName;
  private String myPattern;
  private double myScore;

  public Piece(String name, String pattern, double score){
    myName = name;
    myPattern = pattern;
    myScore = score;
  }

  @Override
  public String toString(){
    return myName;
  }

  public String getMovePattern(){
    return myPattern;
  }

  public double getValue(){
    return myScore;
  }

}
