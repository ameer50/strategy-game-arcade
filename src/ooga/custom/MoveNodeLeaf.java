package ooga.custom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MoveNodeLeaf extends MoveNode implements Serializable {

    public MoveNodeLeaf(Point2D value) {
        super(null, value);
    }

    @Override
    public MoveNode invokeConstructor(List<MoveNode> children) {
        // Should never be called...
        return null;
    }

    @Override
    public void multiply(int multiplier) {
        Point2D oldPoint = this.getValue();
        Point2D newPoint = new Point2D.Double(oldPoint.getX() * multiplier, oldPoint.getY() * multiplier);
        this.setValue(newPoint);
    }

    @Override
    public List<Point2D> generatePoints() {
        List<Point2D> pointList = new ArrayList<>();
        pointList.add(this.getValue());
        return pointList;
    }
}
