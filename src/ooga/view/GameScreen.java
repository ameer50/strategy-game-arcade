package ooga.view;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
        PieceView rook = new PieceView(115, 100, 45, 75, res.getString("BlackRook"));
        PieceView rook2 = new PieceView(185, 100, 45, 75, res.getString("WhiteRook"));
        PieceView rook3 = new PieceView(115, 170, 45, 75, res.getString("WhiteQueen"));
        BoardView theBoard = new BoardView("ChessBoard");
        root.getChildren().add(theBoard.getBoardView());
        ImageView[] array = new ImageView[]{rook.getIVShape(), rook2.getIVShape(), rook3.getIVShape()};
        PieceArrangementView ar = new ChessArrangementView(800, 1200, 45, 75, "black");
        //System.out.println(Arrays.toString(ar.gamePieces()));
        root.getChildren().addAll(ar.gamePieces());

        //root.getChildren().addAll(array);
        setAsScene(new Scene(root));
        stage.setScene(scene);
        stage.setTitle(res.getString("GameStageTitle"));
        scene.getStylesheets().add(res.getString("GameStyleSheet"));
    }

    private void setAsScene(Scene scene) {
        this.scene = scene;
    }
}
