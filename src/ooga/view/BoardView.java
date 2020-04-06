package ooga.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Locale;
import java.util.ResourceBundle;

public class BoardView extends ImageView {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private ImageView boardImage;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int BOARD_XOFFSET = 75;
    private static final int BOARD_YOFFSET = 75;


    public BoardView(String boardType){
        boardImage = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(res.getString(boardType))));
        setBoardConfiguration();
    }

    public ImageView getBoardView(){
        return this.boardImage;
    }

    private void setBoardConfiguration(){
        boardImage.setFitWidth(BOARD_WIDTH);
        boardImage.setFitHeight(BOARD_HEIGHT);

        boardImage.setX(BOARD_XOFFSET);
        boardImage.setY(BOARD_YOFFSET);
    }


}
