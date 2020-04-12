package ooga.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ooga.CellClickedInterface;

import java.awt.geom.Point2D;
import java.util.List;

public interface BoardViewInterface {

    void initialize();

    void checkeredColor();

    HBox[] getCells();

    CellView getCell(int x, int y);

    ImageView[] getPieces();

    void highlightValidMoves(List<Point2D> validMoves);

    void movePiece(int fromX, int fromY, int toX, int toY);

    void setOnPieceClicked(CellClickedInterface clicked);

    void setOnMoveClicked(CellClickedInterface clicked);

    void setSelectedLocation(int x, int y);
}
