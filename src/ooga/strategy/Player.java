package ooga.strategy;

import java.awt.*;

public interface Player {

    boolean isCPU();
    void doMove(int startX, int startY, int endX, int endY);
    double getScore();
    String getName();
    Color getColor();
}
