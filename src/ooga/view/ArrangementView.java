package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.geom.Point2D;
import java.util.Map;

public interface ArrangementView {

    void initialize();

    ImageView[] gamePieces();

    PieceView pieceAt(int x, int y);
}
