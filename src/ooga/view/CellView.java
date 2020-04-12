package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import ooga.CellClickedInterface;

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
    private CellClickedInterface clickPieceFunction;
    private CellClickedInterface noBorderFunction;
    private CellClickedInterface movePieceFunction;
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
        // remove original piece if it exists
        if (this.piece != null) {
            this.getChildren().remove(this.piece.getImage());
        }
        // set new piece
        this.piece = piece;
        // if we want to set it to null, return since we don't want to put an image there
        if (piece == null) return;
        ImageView pieceImage = piece.getImage();
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
                noBorderFunction.clickCell(xIndex, yIndex);
                return;
            }
            // if a piece is there, and it is not highlighted, trigger lambdas to highlight it red and its valid moves yellow
            // also unhighlight everything
            if (!isRed && !isYellow){
                noBorderFunction.clickCell(xIndex, yIndex);
                //toggleRed();
                clickPieceFunction.clickCell(xIndex, yIndex);
            // if a cell is yellow and clicked, trigger lambda to move the piece, unhighlight everything
            } else if (isYellow) {
                movePieceFunction.clickCell(xIndex, yIndex);
                noBorderFunction.clickCell(xIndex, yIndex);
            // if other, just unhighlight all cells
            } else {
                noBorderFunction.clickCell(xIndex, yIndex);
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

    public void setPieceClicked(CellClickedInterface clicked){
        this.clickPieceFunction = clicked;
    }

    public void setMoveClicked(CellClickedInterface clicked) {
        this.movePieceFunction = clicked;
    }

    public void setNoBorderFunction(CellClickedInterface clicked){
        this.noBorderFunction = clicked;
    }

}
