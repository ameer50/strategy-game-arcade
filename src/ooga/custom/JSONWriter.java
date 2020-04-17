package ooga.custom;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public class JSONWriter {

  public static void main(String[] args) throws FileNotFoundException {
    JSONObject jo = new JSONObject();

    jo.put("name", "custom");

    Map types = new LinkedHashMap(3);
    types.put("pieceOne", "moveOne");
    types.put("pieceTwo", "moveTwo");
    types.put("pieceThree", "moveThree");
    jo.put("types", types);

    Map icons = new LinkedHashMap(3);
    types.put("pieceOne", "triangle_icon.jpg");
    types.put("pieceTwo", "square_icon.jpg");
    types.put("pieceThree", "circle_icon.jpg");
    jo.put("icons", icons);

    Map moves = new LinkedHashMap(5);
    moves.put("pieceOne", "diagonal 2:3");
    moves.put("pieceTwo", "vertical 1:4");
    moves.put("pieceThree", "horizontal 2:7");
    Map compound = new LinkedHashMap(4);
    compound.put("vertical", "up 1 | down 1");
    compound.put("horizontal", "right 1 | left 1");
    compound.put("diagonal", "vertical 1 & horizontal 1");
    compound.put("hook", "vertical 2 & horizontal 1");
    Map basic = new LinkedHashMap(4);
    basic.put("up", "0, 1");
    basic.put("down", "0, -1");
    basic.put("right", "1, 0");
    basic.put("left", "-1, 0");
    moves.put("compound", compound);
    moves.put("basic", basic);
    jo.put("moves", moves);


    PrintWriter writer = new PrintWriter("custom.json");
    writer.write(jo.toJSONString());
    writer.flush();
    writer.close();
  }
}
