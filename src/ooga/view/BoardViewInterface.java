package ooga.view;

import javafx.scene.layout.StackPane;
import ooga.ProcessCoordinateInterface;

import java.awt.geom.Point2D;
import java.util.List;

public interface BoardViewInterface {

    void initialize();

    void checkeredColor();

    StackPane[] getCells();

    CellView getCellAt(int x, int y);

    void highlightValidMoves(List<Point2D> validMoves);

    void movePiece(int fromX, int fromY, int toX, int toY);

    void setOnPieceClicked(ProcessCoordinateInterface clicked);

    void setOnMoveClicked(ProcessCoordinateInterface clicked);

    void setSelectedLocation(int x, int y);
}
