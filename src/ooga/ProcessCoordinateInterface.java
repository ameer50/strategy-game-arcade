package ooga;

import java.awt.geom.Point2D;

@FunctionalInterface
public interface ProcessCoordinateInterface {
    void process(Point2D coordinate);
}
