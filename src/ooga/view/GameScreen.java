package ooga.view;

import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameScreen {

    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final double STAGE_HEIGHT = 800;
    private static final double STAGE_WIDTH = 1200;

    private Stage stage;
    private Scene scene;

    public GameScreen(Stage stage){
        this.stage = stage;
        startView();
        stage.show();

    }

    private void startView(){
        BorderPane root = new BorderPane();
        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));

        Pane canvas = new Pane();
        Rectangle rectangle = new Rectangle(100,100,Color.SANDYBROWN);
        canvas.setPrefSize(100, 100);
        rectangle.relocate(470,470);
        HBox rt = new HBox();
        rt.getChildren().addAll(rectangle);
        rt.setLayoutX(470);
        rt.setLayoutY(470);
        rt.getStyleClass().add("yellowborder");
        canvas.getChildren().addAll(rt);



        //BoardView theBoard = new BoardView("ChessBoard");
        //root.getChildren().add(theBoard.getBoardView());
        ArrangementView ar = new ChessArrangementView(8, 45, 75, "black");
        root.getChildren().addAll(ar.gamePieces());
        root.getChildren().addAll(canvas);




    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }
}
