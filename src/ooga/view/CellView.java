package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import ooga.ProcessCoordinateInterface;

public class CellView extends StackPane {

    private boolean isYellow;
    private boolean isRed;
    private double xPos;
    private double yPos;
    private double width;
    private double height;
    private String style;
    private int xIndex;
    private int yIndex;
    private ProcessCoordinateInterface clickPieceFunction;
    private ProcessCoordinateInterface noBorderFunction;
    private ProcessCoordinateInterface movePieceFunction;
    private PieceView piece;

    public CellView(int xindex, int yindex, double xpos, double ypos, double width, double height, String cellColorStyle){
        this.xIndex = xindex;
        this.yIndex = yindex;
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
        System.out.println("Entered setPiece");
        System.out.println("piece = " + piece);
        // remove original piece if it exists
        if (this.piece != null) {
            this.getChildren().remove(this.piece.getImage());
        }
        // set new piece
        this.piece = piece;

        // if we want to set it to null, return since we don't want to put an image there
        if (piece == null) return;
        System.out.println("piece.getPieceName() = " + piece.getPieceName());
        ImageView pieceImage = piece.getImage();
        System.out.println(pieceImage);
        pieceImage.setFitHeight(0.95 * height);
        pieceImage.setPreserveRatio(true);
        pieceImage.setLayoutX(width / 2 - pieceImage.getBoundsInLocal().getWidth() / 2);
        this.getChildren().add(pieceImage);
    }

    public double getWidthOfCell() {
        return width;
    }

    public double getHeightOfCell() {
        return height;
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
                noBorderFunction.process(xIndex, yIndex);
                return;
            }
            // if a piece is there, and it is not highlighted, trigger lambdas to highlight it red and its valid moves yellow
            // also unhighlight everything
            if (!isRed && !isYellow){
                noBorderFunction.process(xIndex, yIndex);
                //toggleRed();
                clickPieceFunction.process(xIndex, yIndex);
            // if a cell is yellow and clicked, trigger lambda to move the piece, unhighlight everything
            } else if (isYellow) {
                movePieceFunction.process(xIndex, yIndex);
                noBorderFunction.process(xIndex, yIndex);
            // if other, just unhighlight all cells
            } else {
                noBorderFunction.process(xIndex, yIndex);
            }
        });
    }

    public void toggleNoBorder(){
        this.getStyleClass().clear();
        this.getStyleClass().add("blackborder");
        isRed = isYellow = false;
    }

    public String toString(){
        return "[ " + xIndex + " , " + yIndex + " ] at x = " + xPos + " , y = " + yPos;
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

}
