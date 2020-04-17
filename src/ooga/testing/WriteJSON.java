package ooga.testing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WriteJSON {
  public static void main(String[] args) throws FileNotFoundException {
    JSONObject jo = new JSONObject();

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
  }
}
