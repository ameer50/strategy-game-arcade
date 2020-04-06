package ooga.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PieceView {

    private ImageView imgview;

    public PieceView(double xpos, double ypos, double width, double height, String name){
        imgview = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(name)));

        setX(xpos);
        setY(ypos);
        setHeight(height);
        setWidth(width);

    }

    public void setHeight(double new_height){
        this.imgview.setFitHeight(new_height);
    }

    public void setWidth(double new_width){
        this.imgview.setFitWidth(new_width);
    }

    public double getHeight(){
        return this.imgview.getFitHeight();
    }

    public double getWidth(){
        return this.imgview.getFitWidth();
    }

    public ImageView getIVShape(){
        return this.imgview;
    }

    public void setX(double new_x){
        this.imgview.setX(new_x);
    }

    public void setY(double new_y){
        this.imgview.setY(new_y);
    }

    public double getX(){
        return this.imgview.getX();
    }

    public double getY(){
        return this.imgview.getY();
    }
}
