package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class PieceView {

    private ImageView pieceImage;
    private Color color;
    private String pieceName;

    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());

    public PieceView(String name) {
        pieceName = name;
        System.out.println("name " + name);
        pieceImage = new ImageView(res.getString(name));
        try {
            Field field = Color.class.getField(name.split("_")[0].toLowerCase());
            color = (Color) field.get(null);
        } catch (Exception e) {
            System.out.println("undefined color");
        }
        //color = Color.getColor(name.split("_")[0].toLowerCase());
    }

    public Color getColor() {
        return color;
    }

    public ImageView getImage() { return pieceImage; }

    public String getPieceName(){ return pieceName; }
}
