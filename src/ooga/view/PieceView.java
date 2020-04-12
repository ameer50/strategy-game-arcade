package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.*;

public class PieceView {

    private ImageView pieceImage;
    private Color color;

    public PieceView (String name){
        pieceImage = new ImageView(name);
    }

    //TODO: set color
    public void setColor(Color color) {
        this.color = color;
    }

    public ImageView getImage() { return this.pieceImage; }
}
