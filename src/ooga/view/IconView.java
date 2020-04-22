package ooga.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IconView extends CellView {

    private String iconName;

    public IconView(Point2D coordinate, double xpos, double ypos, double width, double height, String cellColorStyle) {
        super(coordinate, xpos, ypos, width, height, cellColorStyle);
//        pieces = new ArrayList<>();
//        numPieces = (int) Math.ceil(0.5 * width * height);
    }

    public void setIconName(String name) {
        iconName = name;
    }

    public String getIconName() {
        return iconName;
    }
}
