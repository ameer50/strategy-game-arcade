package ooga.custom;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {
  private String directory;
  private String name;
  private Map<String, String> iconMap;
  private Map<String, String> typeMap;
  private Map<String, MoveNode> pieceMoves;
  private Map<String, MoveNode> basicMoves;
  private Map<String, MoveNode> compoundMoves;

  private Map<String, String> settings;
  private Map<Point2D, String> pieceLocations;

  private static final String ERROR_MSG = "Error parsing JSON file.";
  private JSONObject jo;

  public JSONReader() {
    settings = new HashMap<>();
    pieceLocations = new HashMap<>();
    pieceMoves = new HashMap<>();
  }

  public Map<String, String> getSettings() { return Map.copyOf(settings); }
  public Map<Point2D, String> getPieceLocations() { return Map.copyOf(pieceLocations); }
  public Map<String, MoveNode> getPieceMoves() { return Map.copyOf(pieceMoves); }

  public void parse(String dir) {
    clearAll();
    try {
      File file = new File(dir);
      Object obj = new JSONParser().parse(new FileReader("custom.json"));
      jo = (JSONObject) obj;

      name = (String) jo.get("name");
      iconMap = (Map) jo.get("icons");
      typeMap = (Map) jo.get("types");
      parseMoves();

    } catch (ParseException | IOException e) {
      System.out.println(ERROR_MSG);

    }
  }

  private void parseMoves() {
    Map<String, Map<String, String>> moveMap = (Map) jo.get("moves");
    basicMoves = generateBasicMoves(moveMap.get("basic"));
    compoundMoves = generateCompoundMoves(moveMap.get("compound"));
  }

  private Map<String, MoveNode> generateBasicMoves(Map<String, String> basic) {
    Map<String, MoveNode> basicMoveMap = new HashMap<>();
    for (String name: basic.keySet()) {
      String[] coordinates = basic.get(name).split(", ");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      Point2D point = new Double(x, y);
      basicMoveMap.put(name, new MoveNodePrimitive(point));
    }
    return basicMoveMap;
  }

  private Map<String, MoveNode> generateCompoundMoves(Map<String, String> compound) {
    Map<String, MoveNode> compMoveMap = new HashMap<>();
    List<String> firstPass = new ArrayList<>();
    List<String> secondPass = new ArrayList<>();

    for (String name: compound.keySet()) {
      String moveStr = compound.get(name);
      if (moveStr.contains("AND")) {
        String[] moves = moveStr.split(" AND ");
        for (String move : moves) {
          if (basicMoves.containsKey(move.split(" ")[0])) {
            firstPass.add(moveStr);
          } else {
            secondPass.add(moveStr);
          }
        }
      } else if (moveStr.contains("OR")) {
        String[] moves = moveStr.split(" OR ");
        for (String move : moves) {
          if (basicMoves.containsKey(move.split(" ")[0])) {
            firstPass.add(moveStr);
          } else {
            secondPass.add(moveStr);
          }
        }
      }
    }
    processFirstPass(firstPass, compMoveMap);
    processSecondPass(secondPass, compMoveMap);
    return compMoveMap;
  }

  private void processFirstPass(List<String> pass, Map<String, MoveNode> map) {

  }

  private void processSecondPass(List<String> pass, Map<String, MoveNode> map) {
    
  }

  private void clearAll() {
    settings.clear();
    pieceLocations.clear();
    pieceMoves.clear();
  }
}
