package ooga.board;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Piece implements Serializable {

    private String type;
    private String pattern;
    private int value;
    private String color;
    private int moves;
    private List<Point2D> displacements;

    public Piece(String type, String pattern, int value, String color) {
        this.type = type;
        this.pattern = pattern;
        this.value = value;
        this.color = color;
        this.moves = 0;

        displacements = new ArrayList<>();
        if (pattern != null && pattern.length() > 2 && pattern.substring(0, 2).contains("(")) {
            for (String pointStr : pattern.split("[)], ")) {
                pointStr = pointStr.substring(1);
                // System.out.println(pointStr);
                Pattern regexp = Pattern.compile("(-?\\d), (-?\\d)");
                Matcher matcher = regexp.matcher(pointStr);
                matcher.matches();

                int x = Integer.parseInt(matcher.group(1));
                //System.out.println(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                //System.out.println(matcher.group(2));
                displacements.add(new Point2D.Double(x, y));
            }
        }
    }

    public boolean isSameColor(Piece that) {
        return (this.getColor().equals(that.getColor()));
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.color, this.type);
    }

    public boolean hasMoved() {
        return (this.moves != 0);
    }

    public void incrementMoveCount(boolean isUndo) {
        if (isUndo) moves--;
        else moves++;
    }

    public String getType() {
        return type;
    }

    public String getMovePattern() {
        return this.pattern;
    }

    public int getValue() {
        return this.value;
    }

    public String getColor() {
        return this.color;
    }

    public String getFullName() {
        return String.format("%s_%s", this.color, this.type);
    }

    public List<Point2D> getDisplacements() {
        return List.copyOf(displacements);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setMovePattern(String pattern) {
        this.pattern = pattern;
    }
}
