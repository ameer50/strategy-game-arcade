package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import ooga.ProcessCoordinateInterface;

import java.awt.geom.Point2D;

public class CellView extends StackPane {

    private boolean isYellow;
    private boolean isRed;
    private double xPos;
    private double yPos;
    private double width;
    private double height;
    private String style;
    private Point2D coordinate;
    private ProcessCoordinateInterface clickPieceFunction;
    private ProcessCoordinateInterface noBorderFunction;
    private ProcessCoordinateInterface movePieceFunction;
    private PieceView piece;

    public CellView(Point2D coordinate, double xpos, double ypos, double width, double height, String cellColorStyle){
        this.coordinate = coordinate;
        this.xPos = xpos;
        this.yPos = ypos;
        this.width = width;
        this.height = height;
        this.style = cellColorStyle;
        this.initialize();
        isRed = false;
        isYellow = false;
        piece = null;
        this.setOnClickFunctions();
    }

    private void initialize(){
        Rectangle rectangle = new Rectangle(width,height);
        rectangle.getStyleClass().add(style); // cellcolor1
        this.getChildren().addAll(rectangle);
        this.setLayoutX(xPos);
        this.setLayoutY(yPos);
        toggleNoBorder();
        setOnClickFunctions();
    }

    public void setPiece(PieceView piece) {
        // remove original piece if it exists
        if (this.piece != null) {
            this.getChildren().remove(this.piece.getImage());
        }

        // set new piece
        this.piece = piece;

        // if we want to set it to null, return since we don't want to put an image there
        if (piece == null) return;
        ImageView pieceImage = piece.getImage();
        pieceImage.setFitHeight(0.9 * height);
        pieceImage.setPreserveRatio(true);
        pieceImage.setLayoutX(width / 2 - pieceImage.getBoundsInLocal().getWidth() / 2);
        this.getChildren().add(pieceImage);
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


    public void setOnClickFunctions(){
        this.setOnMouseClicked(e -> {
            // unhighlight everything if a box is clicked that has nothing there
            if (piece == null && !isYellow){
                noBorderFunction.process(coordinate);
                return;
            }
            // if a piece is there, and it is not highlighted, trigger lambdas to highlight it red and its valid moves yellow
            // also unhighlight everything
            if (!isRed && !isYellow){
                noBorderFunction.process(coordinate);
                //toggleRed();
                clickPieceFunction.process(coordinate);
            // if a cell is yellow and clicked, trigger lambda to move the piece, unhighlight everything
            } else if (isYellow) {
                movePieceFunction.process(coordinate);
                noBorderFunction.process(coordinate);
            // if other, just unhighlight all cells
            } else {
                noBorderFunction.process(coordinate);
            }
        });
    }

    public void toggleNoBorder(){
        this.getStyleClass().clear();
        this.getStyleClass().add("blackborder");
        isRed = isYellow = false;
    }

    public String toString(){
        return "[ " + (int) coordinate.getX() + " , " + (int) coordinate.getY() + " ] at x = " + xPos + " , y = " + yPos;
    }

    public void setPieceClicked(ProcessCoordinateInterface clicked){
        this.clickPieceFunction = clicked;
    }

    public void setMoveClicked(ProcessCoordinateInterface clicked) {
        this.movePieceFunction = clicked;
    }

    public void setNoBorderFunction(ProcessCoordinateInterface clicked){
        this.noBorderFunction = clicked;
    }

    public boolean isYellow(){  return isYellow;  }
    public boolean isRed(){  return isRed;  }

}
