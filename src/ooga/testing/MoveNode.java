package ooga.testing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class MoveNode {

  private List<MoveNode> children;
  private Point2D value;

  public MoveNode(List<MoveNode> children) {
    this.children = children;
  }
  public MoveNode(List<MoveNode> children, Point2D value) {
    this.children = children;
    this.value = value;
  }

  public abstract List<Point2D> generatePoints();

  public int size() {
    if (children == null) return 0;
    return children.size();
  }

  public Point2D value() {
    // TODO: Clone instead?
    return value;
  }

  public List<MoveNode> children() {
    return List.copyOf(children);
  }
}
