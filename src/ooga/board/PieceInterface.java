package ooga.board;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

public interface PieceInterface {

    public String getColor();

    public int getValue();

    public String getType();

    public void setType();

    public void setColor();

    public List<Point2D> getDisplacements();

    public String getFullName();

    public String getMovePattern();

    public boolean isSameColor();

    public void incrementMoveCount(boolean isUndo);





}
