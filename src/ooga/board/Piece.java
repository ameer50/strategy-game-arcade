package ooga.board;

public class Piece {
  private String myName;
  private String myPattern;
  private double myScore;
  private String myColor;
  private boolean hasMoved;

  public Piece(String name, String pattern, double score, String color){
    this.myName = name;
    this.myPattern = pattern;
    this.myScore = score;
    this.myColor = color;
    this.hasMoved = false;
  }

  public boolean isOnSameTeam(Piece that){
    return(this.getColor().equals(that.getColor()));
  }

  @Override
  public String toString(){
    return String.format("%s %s", this.myColor, this.myName);
  }

  public String getType() {
    return myName;
  }

  public boolean hasMoved(){
    return this.hasMoved;
  }

  public void move(){
    this.hasMoved = true;
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

  public void setMovePattern(String pattern){
    this.myPattern = pattern;
  }

  public void setName(String name){
    this.myName = name;
  }

  public String getFullName(){
    return this.myColor + "_" + this.myName;
  }

}
