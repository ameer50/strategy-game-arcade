package test;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import ooga.testing.MoveNode;
import ooga.testing.MoveNodeAND;
import ooga.testing.MoveNodeOR;
import ooga.testing.MoveNodePrimitive;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveNodeTest {

  @Test
  public void twoLevelTree() {
    List orList = new ArrayList<>();
    for (double i: List.of(1, 2, 3)) {
      Point2D point = new Double(i, i+1);
      MoveNode primitive = new MoveNodePrimitive(point);
      orList.add(primitive);
    }
    List andList = new ArrayList<>();
    for (double i: List.of(1, 2)) {
      Point2D point = new Double(i, i+1);
      MoveNode primitive = new MoveNodePrimitive(point);
      andList.add(primitive);
    }

    MoveNode orNode = new MoveNodeOR(List.copyOf(orList));
    MoveNode andNode = new MoveNodeAND(List.copyOf(andList));

    MoveNode rootNode = new MoveNodeOR(List.of(andNode, orNode));
    List points = rootNode.generatePoints();

    assertEquals(new Point2D.Double(3, 5), points.get(0), "AND node (0) not correct");
    assertEquals(new Point2D.Double(1, 2), points.get(1), "OR node (1) not correct");
    assertEquals(new Point2D.Double(2, 3), points.get(2), "OR node (2) not correct");
    assertEquals(new Point2D.Double(3, 4), points.get(3), "OR node (3) not correct");
  }

  @Test
  public void andNodeTree() {
    int[] ints1 = {0, 1, 2, 3};
    int[] ints2 = {3, 2, 1, 0};
    MoveNodeAND sub1 = createAndFromInts(ints1);
    MoveNodeAND sub2 = createAndFromInts(ints2);
    MoveNodeAND rootNode = new MoveNodeAND(sub1, sub2);

    Point2D point = rootNode.generatePoints().get(0);
    Point2D sub1Point = sub1.generatePoints().get(0);
    Point2D sub2Point = sub2.generatePoints().get(0);
    assertEquals(new Point2D.Double(6, 6), point, "AND incorrect:"+point);
    assertEquals(new Point2D.Double(2, 4), sub1Point, "SUB1 incorrect:"+sub1Point);
    assertEquals(new Point2D.Double(4, 2), sub2Point, "SUB2 incorrect:"+sub2Point);
  }

  private MoveNodeAND createAndFromInts(int[] ints) {
    List primitives = new ArrayList<>();
    int i = 0;
    while (i < ints.length) {
      int x = ints[i];
      int y = ints[i+1];
      i+=2;
      Point2D point = new Double(x, y);
      primitives.add(new MoveNodePrimitive(point));
    }
    return new MoveNodeAND(primitives);
  }
}
