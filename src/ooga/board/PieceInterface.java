package ooga.board;

import java.io.Serializable;

public interface PieceInterface {

    public String getColor();

    public int getValue();

    public String getMovePattern();

    public boolean hasMoved();

    public String getType();




}
