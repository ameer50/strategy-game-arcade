import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import ooga.custom.MoveNode;
import ooga.custom.MoveNodeAnd;
import ooga.custom.MoveNodeOr;
import ooga.custom.MoveNodeLeaf;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveNodeTest {

  @Test
  public void multiLevelTree() {
    int[] ints1 = {1, 2, 3, 4, 5, 6};
    int[] ints2 = {4, 3, 2, 1};
    MoveNodeOr or = createOrFromInts(ints1);
    MoveNodeAnd and = createAndFromInts(ints2);
    MoveNode root = new MoveNodeOr(List.of(and, or));

    List points = root.generatePoints();
    assertEquals(new Point2D.Double(6, 4), points.get(0),
        "AND node incorrect: " + points.get(0));
    assertEquals(new Point2D.Double(1, 2), points.get(1),
        "OR node (1) incorrect: " + points.get(1));
    assertEquals(new Point2D.Double(3, 4), points.get(2), "OR node (2) incorrect: ");
    assertEquals(new Point2D.Double(5, 6), points.get(3), "OR node (3) incorrect: ");

    MoveNode rootCopy = root.copy();
    points = rootCopy.generatePoints();
    assertEquals(new Point2D.Double(6, 4), points.get(0),
        "COPY incorrect: " + points.get(0));

    root.multiply(2);
    points = root.generatePoints();
    assertEquals(new Point2D.Double(12, 8), points.get(0),
        "MULTIPLICATION incorrect: " + points.get(0));
  }

  private MoveNodeAnd createAndFromInts(int[] ints) {
    return new MoveNodeAnd(createPrimitivesFromInts(ints));
  }

  private MoveNodeOr createOrFromInts(int[] ints) {
    return new MoveNodeOr(createPrimitivesFromInts(ints));
  }

  private List<MoveNode> createPrimitivesFromInts(int[] ints) {
    List list = new ArrayList<>();
    int i = 0;
    while (i < ints.length) {
      int x = ints[i];
      int y = ints[i+1];
      Point2D point = new Double(x, y);
      list.add(new MoveNodeLeaf(point));
      i+=2;
    }
    return list;
  }
}
