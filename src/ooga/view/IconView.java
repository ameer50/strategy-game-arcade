package ooga.view;

import java.awt.geom.Point2D;

public class IconView extends CellView {

    private String iconName;

    public IconView(Point2D coordinate, double width, double height, String cellColorStyle) {
        super(coordinate, width, height, cellColorStyle);
    }

    public void setIconName(String name) {
        iconName = name;
    }

    public String getIconName() {
        return iconName;
    }
}
