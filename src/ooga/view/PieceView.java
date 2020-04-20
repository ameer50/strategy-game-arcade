package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class PieceView {

    private ImageView pieceImage;
    private String color;
    private String pieceName;

    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());

    public PieceView(String fullName) {
        pieceName = fullName;
        System.out.println("fullname " + fullName);
        pieceImage = new ImageView(res.getString(fullName));
        color = fullName.split("_")[0];
    }

    public String getColor() {
        return color;
    }

    public ImageView getImage() { return pieceImage; }

    public String getPieceName(){ return pieceName; }
}
