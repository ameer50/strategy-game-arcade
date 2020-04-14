package ooga.board;

import java.io.Serializable;

public class Piece implements Serializable {
  private String myName;
  private String myPattern;
  private int myScore;
  private String myColor;
  private int moves;

  public Piece (String name, String pattern, int score, String color) {
    this.myName = name;
    this.myPattern = pattern;
    this.myScore = score;
    this.myColor = color;
    this.moves = 0;
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
    return this.moves != 0;
  }

  public void move(){
    this.moves++;
  }

  public void unmove(){
    this.moves--;
  }
  public String getMovePattern(){
    return this.myPattern;
  }

  public int getValue(){
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
