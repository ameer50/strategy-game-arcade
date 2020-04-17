package ooga.custom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MoveNodePrimitive extends MoveNode {

  public MoveNodePrimitive(Point2D value) {
    super(null, value);
  }

  @Override
  public List<Point2D> generatePoints() {
    List<Point2D> pointList = new ArrayList<>();
    pointList.add(this.value());
    return pointList;
  }
}
