package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.*;

public class PieceView {

    private ImageView pieceImage;
    private Color color;

    public PieceView(String name) {
        pieceImage = new ImageView(name);
        color = Color.getColor(name.split("_")[0].toUpperCase());
    }

    public Color getColor() {
        return color;
    }

    public ImageView getImage() { return this.pieceImage; }
}
