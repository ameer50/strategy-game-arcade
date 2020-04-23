package ooga.view;

import javafx.scene.layout.StackPane;
import ooga.ProcessCoordinateInterface;
import ooga.history.Move;

import java.awt.geom.Point2D;
import java.util.List;

public interface BoardViewInterface {

    void initialize();

    void createBoardColors();

    StackPane[] getCells();

    CellView getCellAt(int x, int y);

    void highlightValidMoves(List<Point2D> validMoves);

    void doMove(Move m);

    void setOnPieceClicked(ProcessCoordinateInterface clicked);

    void setOnMoveClicked(ProcessCoordinateInterface clicked);

    void setSelectedLocation(Point2D coordinate);
}
