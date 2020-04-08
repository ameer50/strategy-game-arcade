package ooga.view;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class BoardView {


    private CellView[][] arrangement;
    private HBox[] cellList;
    private int boardLength;
    private static final int BOARD_XOFFSET = 35;
    private static final int BOARD_YOFFSET = 35;
    private static final int PIECE_SPACE = 6;
    private static final double BOARD_WIDTH = 600;
    private static final double BOARD_HEIGHT = 600;
    private List<String> firstColorSequence;
    private List<String> secondColorSequence;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());

    private double cellLength;

    public BoardView(int boardDim){

        arrangement = new CellView[boardDim][boardDim];
        cellList = new CellView[boardDim*boardDim];
        this.boardLength = boardDim;
        cellLength = (BOARD_WIDTH) / boardDim;
        initialize();
    }


    public void initialize() {
        checkeredColor();
        int cellIndex = 0;
        for(int i = 0; i < boardLength; i++){
            for(int j =0; j < boardLength; j++){
                if( i % 2 == 0){
                    arrangement[i][j] = new CellView((BOARD_XOFFSET + cellLength * i + PIECE_SPACE*i), (BOARD_YOFFSET + cellLength * j + PIECE_SPACE*j), cellLength, cellLength, secondColorSequence.get(j));
                }else{
                    arrangement[i][j] = new CellView((BOARD_XOFFSET + cellLength*i + PIECE_SPACE*i), (BOARD_YOFFSET + cellLength*j + PIECE_SPACE*j), cellLength, cellLength, firstColorSequence.get(j));
                }
                cellList[cellIndex] = arrangement[i][j];
                cellIndex++;

            }
        }
    }


    public HBox[] getCells() {

        return cellList;
    }

    public CellView getCell(int row, int col){
        return arrangement[row][col];
    }



    private void checkeredColor(){
        firstColorSequence = new ArrayList<>();
        for(int i =0; i<boardLength; i++){
            if (i % 2 == 0){
                firstColorSequence.add("cellcolor1");
            }else{
                firstColorSequence.add("cellcolor2");
            }
        }
        //System.out.println(firstColorSequence);


        secondColorSequence = new ArrayList<>(firstColorSequence);
        Collections.reverse(secondColorSequence);
        //System.out.println(secondColorSequence);
    }

}
