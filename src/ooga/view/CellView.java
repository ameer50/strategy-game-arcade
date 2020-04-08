package ooga.view;

import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class CellView extends HBox {

    private boolean toggleYellow;
    private boolean toggleRed;
    private double xpos;
    private double ypos;
    private double width;
    private double height;
    private String style;

    public CellView(double xpos, double ypos, double width, double height, String cellColorStyle){
        this.xpos = xpos;
        this.ypos = ypos;
        this.width = width;
        this.height = height;
        this.style = cellColorStyle;
        this.initialize();
        toggleRed = true;
        this.toggleRed();
    }

    private void initialize(){
        Rectangle rectangle = new Rectangle(width,height);
        rectangle.getStyleClass().add(style); // cellcolor1
        this.getChildren().addAll(rectangle);
        this.setLayoutX(xpos);
        this.setLayoutY(ypos);

        toggleNoBorder();
    }

    public void toggleYellow(){
        this.getStyleClass().clear();
        this.getStyleClass().add("yellowborder");
    }

    public void toggleRed(){

        this.setOnMouseClicked(e -> {
            if(toggleRed){
                this.getStyleClass().clear();
                this.getStyleClass().add("redborder");
            }else{
                toggleNoBorder();
            }
            toggleRed = !toggleRed;
        });

    }

    public void toggleNoBorder(){
        this.getStyleClass().clear();
        this.getStyleClass().add("blackborder");
    }

    public String toString(){
        return "Hi";
    }

}
