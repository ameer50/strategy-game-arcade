package ooga.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Locale;
import java.util.ResourceBundle;

public class BoardView extends ImageView {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private ImageView boardImage;

    public BoardView(String boardType){
        boardImage = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(res.getString(boardType))));
        setBoardConfiguration();
    }

    public ImageView getBoardView(){
        return this.boardImage;
    }

    private void setBoardConfiguration(){
        boardImage.setFitWidth(600);
        boardImage.setFitHeight(600);

        boardImage.setX(75);
        boardImage.setY(75);
    }


}
