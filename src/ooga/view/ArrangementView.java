package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.geom.Point2D;
import java.util.Map;

public interface ArrangementView {

    public abstract void initialize();

    public abstract ImageView[] gamePieces();
}
