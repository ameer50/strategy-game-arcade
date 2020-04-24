package ooga.board;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ooga.custom.MoveNode;

public class Piece implements Serializable {

  private String type;
  private String pattern;
  private int value;
  private String color;
  private int moves;
  private List<Point2D> displacements;

  public Piece(String type, String pattern, int value, String color) {
    this.type = type;
    this.pattern = pattern;
    this.value = value;
    this.color = color;
    this.moves = 0;
    displacements = new ArrayList<>();
    if (pattern.contains("(")) {
      for (String pointStr: pattern.split("), ")) {
        int x = Integer.parseInt(pointStr.substring(1, 2));
        int y = Integer.parseInt(pointStr.substring(4, 5));
        displacements.add(new Point2D.Double(x, y));
      }
    }
  }

  public boolean isSameColor(Piece that){
    return (this.getColor().equals(that.getColor()));
  }

  @Override
  public String toString(){
    return String.format("%s %s", this.color, this.type);
  }

  public boolean hasMoved(){
    return (this.moves != 0);
  }

  public void incrementMoveCount(boolean isUndo) {
    if (isUndo) moves--; else moves++;
  }

  public String getType() { return type; }
  public String getMovePattern() { return this.pattern; }
  public int getValue() { return this.value; }
  public String getColor() { return this.color; }


  public List<Point2D> getDisplacements() { return List.copyOf(displacements); }

  
  public String getFullName() {
    return String.format("%s_%s", this.color, this.type);
  }

  public void setType(String type){ this.type = type; }
  public void setColor(String color) { this.color = color; }
  public void setValue(int value) { this.value = value; }
  public void setMovePattern(String pattern){ this.pattern = pattern; }
}
