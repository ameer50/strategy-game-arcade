package ooga.custom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MoveNodeOr extends MoveNode {

  public MoveNodeOr(List<MoveNode> children) {
    // TODO: Throw an error if 'children' has less than two nodes.
    super(children);
  }
  public MoveNodeOr(MoveNode a, MoveNode b) {
    super(List.of(a, b));
  }

  @Override
  public List<Point2D> generatePoints() {
    List<Point2D> pointList = new ArrayList<>();
    for (MoveNode subNode: this.children()) {
      pointList.addAll(subNode.generatePoints());
    }
    return pointList;
  }
}
