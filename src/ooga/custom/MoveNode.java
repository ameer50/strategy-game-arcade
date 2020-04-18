package ooga.custom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class MoveNode implements Serializable {

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

  public void multiply(int multiplier) {
    for (MoveNode node: children) {
      node.multiply(multiplier);
    }
  }

  public int size() {
    if (children == null) return 0;
    return children.size();
  }

  public Point2D getValue() {
    return (Point2D) value.clone();
  }

  public void setValue(Point2D point) {
    this.value = point;
  }

  public List<MoveNode> getChildren() { return List.copyOf(children); }

  public MoveNode copy() {
    // Base case.
    if (children == null) {
      return new MoveNodeLeaf(getValue());
    }
    // Recursion.
    List<MoveNode> newChildren = new ArrayList<>();
    for (MoveNode child: children) {
      newChildren.add(child.copy());
    }
    return invokeConstructor(newChildren);
  }

  public abstract MoveNode invokeConstructor(List<MoveNode> children);
}
