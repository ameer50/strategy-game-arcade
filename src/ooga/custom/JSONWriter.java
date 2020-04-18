package ooga.custom;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public class JSONWriter {

  public static void main(String[] args) throws FileNotFoundException {
    JSONObject jo = new JSONObject();
    jo.put("name", "Custom");

    Map dimensions = new LinkedHashMap(2);
    dimensions.put("height", 6);
    dimensions.put("width", 6);
    jo.put("dimensions", dimensions);

    Map scores = new LinkedHashMap(3);
    scores.put("Triangle", 3);
    scores.put("Square", 2);
    scores.put("Circle", 2);
    jo.put("scores", scores);

    Map moves = new LinkedHashMap(5);
    Map pieces = new LinkedHashMap(3);
    pieces.put("Triangle", "diagonal 2:3");
    pieces.put("Square", "vertical 1:4");
    pieces.put("Circle", "horizontal 2:7");
    Map compound = new LinkedHashMap(4);
    compound.put("vertical", "up 1 OR down 1");
    compound.put("horizontal", "right 1 OR left 1");
    compound.put("diagonal", "vertical 1 AND horizontal 1");
    compound.put("hook", "vertical 2 AND horizontal 1");
    Map basic = new LinkedHashMap(4);
    basic.put("up", "0, 1");
    basic.put("down", "0, -1");
    basic.put("right", "1, 0");
    basic.put("left", "-1, 0");
    moves.put("compound", compound);
    moves.put("basic", basic);
    moves.put("pieces", pieces);
    jo.put("moves", moves);

    Map locations = new LinkedHashMap();
    locations.put("White_Triangle", "5, 1");
    locations.put("White_Square", "5, 3");
    locations.put("White_Circle", "5, 5");
    locations.put("Black_Triangle", "0, 0");
    locations.put("Black_Square", "0, 2");
    locations.put("Black_Circle", "0, 4");
    jo.put("locations", locations);

    PrintWriter writer = new PrintWriter("custom.json");
    writer.write(jo.toJSONString());
    writer.flush();
    writer.close();
  }
}
