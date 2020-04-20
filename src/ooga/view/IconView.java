package ooga.view;

import java.util.ArrayList;
import java.util.List;

public class IconView extends CellView {

    private List<PieceView> pieces;
    private int numPieces;

    public IconView(int xindex, int yindex, double xpos, double ypos, double width, double height, String cellColorStyle) {
        super(xindex, yindex, xpos, ypos, width, height, cellColorStyle);
        pieces = new ArrayList<>();
        numPieces = (int) Math.ceil(0.5 * width * height);
    }

    public void fillPieces() {

    }
}
