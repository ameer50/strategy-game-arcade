package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import ooga.controller.ProcessCoordinateInterface;

import java.awt.geom.Point2D;

public class CellView extends StackPane {

    public static final String YELLOW_BORDER = "yellowborder";
    public static final String RED_BORDER = "redborder";
    public static final String BLACK_BORDER = "blackborder";
    private boolean hasYellowBorder;
    private boolean hasRedBorder;
    private double width;
    private double height;
    private String style;
    private Point2D coordinate;
    private ProcessCoordinateInterface clickPieceFunction;
    private ProcessCoordinateInterface noBorderFunction;
    private ProcessCoordinateInterface movePieceFunction;
    private PieceView pieceView;

    public CellView(Point2D coordinate, double width, double height, String cellColorStyle){
        this.coordinate = coordinate;
        this.width = width;
        this.height = height;
        this.style = cellColorStyle;

        hasRedBorder = false;
        hasYellowBorder = false;
        pieceView = null;

        initialize();
        setOnClickFunction();
    }

    private void initialize(){
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.getStyleClass().add(style);
        this.getChildren().addAll(rectangle);
        toggleNoBorder();
        setOnClickFunction();
    }

    public void setPieceView(PieceView pieceView) {
        // remove original piece if it exists
        if (this.pieceView != null) {
            this.getChildren().remove(this.pieceView.getImage());
        }

        // set new piece
        this.pieceView = pieceView;

        // if we want to set it to null, return since we don't want to put an image there
        if (pieceView == null) return;

        ImageView pieceImage = pieceView.getImage();
        pieceImage.setFitHeight(0.9 * height);
        pieceImage.setPreserveRatio(true);
        pieceImage.setLayoutX(width / 2 - pieceImage.getBoundsInLocal().getWidth() / 2);
        this.getChildren().add(pieceImage);
    }

    public PieceView getPieceView() {
        return pieceView;
    }

    public void toggleYellow(){
        if (!hasYellowBorder) {
            this.getStyleClass().clear();
            this.getStyleClass().add(YELLOW_BORDER);
        } else {
            toggleNoBorder();
        }
        hasYellowBorder = !hasYellowBorder;
    }

    public void toggleRed(){
        if (!hasRedBorder) {
            this.getStyleClass().clear();
            this.getStyleClass().add(RED_BORDER);
        } else {
            toggleNoBorder();
        }
        hasRedBorder = !hasRedBorder;
    }


    public void setOnClickFunction() {
        this.setOnMouseClicked(e -> {
            // unhighlight everything if a box is clicked that has nothing there
            if (pieceView == null && !hasYellowBorder){
                noBorderFunction.process(coordinate);
                return;
            }
            // if a piece is there, and it is not highlighted, trigger lambdas to highlight it red and its valid moves yellow
            // also unhighlight everything
            if (!hasRedBorder && !hasYellowBorder){
                noBorderFunction.process(coordinate);
                clickPieceFunction.process(coordinate);
            // if a cell is yellow and clicked, trigger lambda to move the piece, unhighlight everything
            } else if (hasYellowBorder) {
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
        this.getStyleClass().add(BLACK_BORDER);
        hasRedBorder = hasYellowBorder = false;
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

    public boolean isHasYellowBorder() {
        return hasYellowBorder;
    }

    public boolean isHasRedBorder() {
        return hasRedBorder;
    }
}
