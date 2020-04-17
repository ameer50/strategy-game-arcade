package ooga.custom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class MoveNodeAnd extends MoveNode {

  public MoveNodeAnd(List<MoveNode> children) {
    // TODO: Throw an error if 'children' has less than two nodes.
    super(children);
  }
  public MoveNodeAnd(MoveNode a, MoveNode b) {
    super(List.of(a, b));
  }

  @Override
  public List<Point2D> generatePoints() {
    MoveNode a = children().get(0);
    MoveNode b = children().get(1);
    return concatenate(a, b);
  }

  protected List<Point2D> concatenate(MoveNode a, MoveNode b) {
    if ((a.size()==0) & (b.size()==0)) {
      Point2D sum = pointSum(a.value(), b.value());
      return List.of(sum);
    }
    List<Point2D> pointsB = b.generatePoints();
    List<Point2D> pointsA = a.generatePoints();
    System.out.println(pointsA);
    System.out.println(pointsB);
    return concatPointLists(pointsA, pointsB);
  }

  private List<Point2D> concatPointLists(List<Point2D> listA, List<Point2D> listB) {
    List<Point2D> pointList = new ArrayList();
    for (Point2D pointA: listA) {
      for (Point2D pointB: listB) {
        pointList.add(pointSum(pointA, pointB));
      }
    }
    return pointList;
  }

  private Point2D pointSum(Point2D a, Point2D b) {
    Point2D sum = new Point2D.Double(a.getX()+b.getX(), a.getY() + b.getY());
//    System.out.println(String.format("(%d, %d), (%d, %d)", (int) a.getX(), (int) a.getY(),
//        (int) b.getX(), (int) b.getY()));
    return sum;
  }
}
