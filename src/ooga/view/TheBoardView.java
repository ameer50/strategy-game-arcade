package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class TheBoardView implements ArrangementView {

    private Rectangle[][] arrangement;
    private Rectangle[] cellList;
    private static final double BOARD_WIDTH = 600;
    private static final double BOARD_HEIGHT = 600;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());

    private double cellLength;

    public TheBoardView(int boardLength){
        arrangement = new Rectangle[boardLength][boardLength];
        cellList = new Rectangle[boardLength*boardLength];

        cellLength = BOARD_WIDTH / boardLength;
        initialize();
    }


    @Override
    public void initialize() {

    }

    @Override
    public ImageView[] gamePieces() {
        return new ImageView[0];
    }
}
