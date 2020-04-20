package ooga.custom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import ooga.utility.Point2DUtility;

public class MoveNodeAnd extends MoveNode implements Serializable {

  private Point2DUtility utility;

  public MoveNodeAnd(List<MoveNode> children) {
    /* TODO: Throw an error if 'children' has less than two nodes. */
    super(children);
    this.utility = new Point2DUtility();
  }
  public MoveNodeAnd(MoveNode a, MoveNode b) {
    super(List.of(a, b));
  }

  @Override
  public MoveNode invokeConstructor(List<MoveNode> children) {
    return new MoveNodeAnd(children);
  }

  @Override
  public List<Point2D> generatePoints() {
    MoveNode a = getChildren().get(0);
    MoveNode b = getChildren().get(1);
    return concatenate(a, b);
  }

  protected List<Point2D> concatenate(MoveNode a, MoveNode b) {
    if ((a.size()==0) & (b.size()==0)) {
      Point2D sum = utility.pointSum(a.getValue(), b.getValue());
      return List.of(sum);
    }
    List<Point2D> pointsB = b.generatePoints();
    List<Point2D> pointsA = a.generatePoints();
    System.out.println(pointsA);
    System.out.println(pointsB);
    return utility.concatPointLists(pointsA, pointsB);
  }
}