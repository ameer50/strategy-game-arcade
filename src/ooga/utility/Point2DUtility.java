package ooga.utility;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Point2DUtility {

    public Point2DUtility() {
    }

    public List<Point2D> concatPointLists(List<Point2D> listA, List<Point2D> listB) {
        List<Point2D> pointList = new ArrayList();
        for (Point2D pointA : listA) {
            for (Point2D pointB : listB) {
                pointList.add(pointSum(pointA, pointB));
            }
        }
        return pointList;
    }

    public Point2D pointSum(Point2D a, Point2D b) {
        Point2D sum = new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
        return sum;
    }
}
