package ooga.board;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import ooga.custom.MoveNode;

public class Piece implements Serializable {
  private String type;
  private String pattern;
  private int score;
  private String color;
  private int moves;
  private int ID;
  private List<Point2D> displacements;

  public Piece(String type, String pattern, int score, String color, int uniqueID) {
    this.type = type;
    this.pattern = pattern;
    this.score = score;
    this.color = color;
    this.moves = 0;
    this.ID = uniqueID;
  }
  public Piece(String type, MoveNode node, int score, String color, int uniqueID) {
    this.type = type;
    this.pattern = null;
    this.score = score;
    this.color = color;
    this.moves = 0;
    this.ID = uniqueID;
    this.displacements = node.generatePoints();
  }

  public boolean isSameColor(Piece that){
    return (this.getColor().equals(that.getColor()));
  }

  @Override
  public String toString(){
    return String.format("%s %s", this.color, this.type);
  }

  public String getType() {
    return type;
  }

  public boolean hasMoved(){
    return (this.moves != 0);
  }

  public void move(){
    this.moves++;
  }

  public void unMove(){
    this.moves--;
  }

  public String getMovePattern(){
    return this.pattern;
  }
  public int getValue(){
    return this.score;
  }
  public String getColor(){
    return this.color;
  }
  public String getFullName(){
    return String.format("%s_%s", this.color, this.type);
  }
  public int getID(){
    return this.ID;
  }
  public List<Point2D> getDisplacements() { return List.copyOf(displacements); }

  public void setMovePattern(String pattern){
    this.pattern = pattern;
  }
  public void setType(String type){
    this.type = type;
  }
}
