package ooga.exceptions;

import javafx.scene.paint.Color;

public enum ErrorSeverity {
    BASIC ("Basic", Color.BLUE),
    MEDIUM ("Medium", Color.YELLOW),
    CRITICAL ("Critical", Color.RED);

    private final String mySevereLevel;
    private final Color myColor;
    ErrorSeverity(String level, Color color) {
        mySevereLevel = level;
        myColor = color;
    }

    public Color getColor() {return myColor;}
    public String getLevel() {return mySevereLevel;}
}
