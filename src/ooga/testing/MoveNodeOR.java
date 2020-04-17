package ooga.testing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MoveNodeOR extends MoveNode {

  public MoveNodeOR(List<MoveNode> children) {
    // TODO: Throw an error if 'children' has less than two nodes.
    super(children);
  }
  public MoveNodeOR(MoveNode a, MoveNode b) {
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
