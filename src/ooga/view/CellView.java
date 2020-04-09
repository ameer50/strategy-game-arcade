package ooga.view;

import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import ooga.CellClickedInterface;

public class CellView extends HBox {

    private boolean isYellow;
    private boolean isRed;
    private double xpos;
    private double ypos;
    private double width;
    private double height;
    private String style;
    private int xindex;
    private int yindex;
    private CellClickedInterface clickPieceFunction;
    private CellClickedInterface noBorderFunction;
    private CellClickedInterface movePieceFunction;
    private PieceView piece;

    public CellView(int xindex, int yindex, double xpos, double ypos, double width, double height, String cellColorStyle){
        this.xindex = xindex;
        this.yindex = yindex;
        this.xpos = xpos;
        this.ypos = ypos;
        this.width = width;
        this.height = height;
        this.style = cellColorStyle;
        this.initialize();
        isRed = false;
        isYellow = false;
        piece = null;
        this.lightUpCells();
    }

    private void initialize(){
        Rectangle rectangle = new Rectangle(width,height);
        rectangle.getStyleClass().add(style); // cellcolor1
        this.getChildren().addAll(rectangle);
        this.setLayoutX(xpos);
        this.setLayoutY(ypos);

        toggleNoBorder();
        lightUpCells();
    }

    public void setPiece(PieceView piece) {
        this.piece = piece;
    }

    public PieceView getPiece() {
        return piece;
    }

    public void toggleYellow(){
        if(!isYellow){
            this.getStyleClass().clear();
            this.getStyleClass().add("yellowborder");
        }else{
            toggleNoBorder();
        }
        isYellow = !isYellow;

    }

    public void toggleRed(){
        if(!isRed){
            this.getStyleClass().clear();
            this.getStyleClass().add("redborder");
        }else{
            toggleNoBorder();
        }
        isRed = !isRed;

    }


    public void lightUpCells(){
        this.setOnMouseClicked(e -> {
            if (piece == null && !isYellow){
                noBorderFunction.clickCell(xindex, yindex);
                return;
            }

            if (!isRed && !isYellow){
                noBorderFunction.clickCell(xindex, yindex);
                toggleRed();
                clickPieceFunction.clickCell(xindex, yindex);
            } else if(isYellow){
                movePieceFunction.clickCell(xindex, yindex);
                noBorderFunction.clickCell(xindex, yindex);
            } else{
                noBorderFunction.clickCell(xindex, yindex);
            }
        });
    }

    public void toggleNoBorder(){
        this.getStyleClass().clear();
        this.getStyleClass().add("blackborder");
        isRed = isYellow = false;
    }

    public String toString(){
        return "[ " + xindex + " , " + yindex + " ] at x = " + xpos + " , y = " + ypos;
    }

    public void setPieceClickedFunction(CellClickedInterface clicked){
        this.clickPieceFunction = clicked;
    }

    public void setMoveClickedFunction(CellClickedInterface clicked) {
        this.movePieceFunction = clicked;
    }

    public void setNoBorderFunction(CellClickedInterface clicked){
        this.noBorderFunction = clicked;
    }

}
