package ooga.board;

public class Piece {
  private String myName;
  private String myPattern;
  private double myScore;
  private String myColor;

  public Piece(String name, String pattern, double score, String color){
    this.myName = name;
    this.myPattern = pattern;
    this.myScore = score;
    this.myColor = color;
  }

  public boolean isOnSameTeam(Piece that){
    return(this.getColor().equals(that.getColor()));
  }
  @Override
  public String toString(){
    return this.myName;
  }

  public String getMovePattern(){
    return this.myPattern;
  }

  public double getValue(){
    return this.myScore;
  }

  public String getColor(){
    return this.myColor;
  }

}
