package ooga.json;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import ooga.custom.MoveNode;
import ooga.custom.MoveNodeAnd;
import ooga.custom.MoveNodeLeaf;
import ooga.custom.MoveNodeOr;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONProcessor {

  public static final String AND = " AND ";
  public static final String OR = " OR ";
  private String name;
  private Map<String, MoveNode> pieceMoves;
  private Map<String, Long> pieceScores;
  private Map<Point2D, String> pieceLocations;
  private Map<String, MoveNode> basicMoves;
  private Map<String, MoveNode> compoundMoves;
  private Map<String, MoveNode> allMoves;
  private Map<String, Long> dimensions;

  private static final String ERROR_MSG = "Error parsing JSON file.";
  private JSONObject jo;

  public JSONProcessor() {
    dimensions = new HashMap<>();
    pieceLocations = new HashMap<>();
    pieceMoves = new HashMap<>();
    pieceScores = new HashMap<>();
  }

  public String getName() { return name; }
  public Map<String, Long> getDimensions() { return Map.copyOf(dimensions); }
  public Map<Point2D, String> getPieceLocations() { return Map.copyOf(pieceLocations); }
  public Map<String, MoveNode> getPieceMoves() { return Map.copyOf(pieceMoves); }
  public Map<String, Long> getPieceScores() { return pieceScores; }

  public void parse(String dir) {
    clearAll();
    try {
      Object obj = new JSONParser().parse(new FileReader(dir));
      jo = (JSONObject) obj;

      name = (String) jo.get("name");
      dimensions = (Map) jo.get("dimensions");
      pieceScores = (Map) jo.get("scores");
      parsePieceLocations();
      parsePieceMoves();

    } catch (ParseException | IOException e) {
      System.out.println(ERROR_MSG);
    }
  }

  private void parsePieceLocations() {
    pieceLocations = new HashMap<>();
    Map<String, String> locations = (Map) jo.get("locations");
    for (String pieceName: locations.keySet()) {
      String[] coordinateArr = locations.get(pieceName).split(", ");
      int x = Integer.parseInt(coordinateArr[0]);
      int y = Integer.parseInt(coordinateArr[1]);
      Point2D point = new Point2D.Double(x, y);
      pieceLocations.put(point, pieceName);
    }
  }

  private void parsePieceMoves() {
    Map<String, Map<String, String>> moveMap = (Map) jo.get("moves");
    basicMoves = new HashMap<>();
    compoundMoves = new HashMap<>();
    generateBasicMoves(moveMap.get("basic"));
    generateCompoundMoves(moveMap.get("compound"));
    addToPieceMoves(moveMap.get("pieces"));
  }

  private void generateBasicMoves(Map<String, String> basic) {
    for (String basicName: basic.keySet()) {
      String[] coordinates = basic.get(basicName).split(", ");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      Point2D point = new Double(x, y);
      basicMoves.put(basicName, new MoveNodeLeaf(point));
    }
  }

  private void generateCompoundMoves(Map<String, String> compound) {
    List<String> firstPass = new ArrayList<>();
    List<String> secondPass = new ArrayList<>();

    for (String compoundName: compound.keySet()) {
      String constituents = compound.get(compoundName);
      String[] constituentArr = splitAndOr(constituents);
      for (String constituent : constituentArr) {
        /* This imposes the requirement that, for a compound move, the most complex constituent
          must come FIRST in the JSON file. */
        if (basicMoves.containsKey(constituent.split(" ")[0])) {
          firstPass.add(compoundName);
        } else {
          secondPass.add(compoundName);
        }
      }
    }
    processPass(firstPass, compound);
    processPass(secondPass, compound);
  }

  private void processPass(List<String> pass, Map<String, String> compound) {
    Map passMap = new HashMap();
    for (String compoundName : pass) {
      List<MoveNode> subNodes = new ArrayList<>();
      String constituents = compound.get(compoundName);
      String[] constituentArr = splitAndOr(constituents);

      for (String constituent : constituentArr) {
        String constituentName = constituent.split(" ")[0];
        int multiplier = Integer.parseInt(constituent.split(" ")[1]);

        MoveNode subNode = (basicMoves.containsKey(constituentName)) ?
            basicMoves.get(constituentName) : compoundMoves.get(constituentName);
        subNode = subNode.copy();
        subNode.multiply(multiplier);
        subNodes.add(subNode);
      }
      MoveNode compoundNode = (compoundName.contains(AND)) ? new MoveNodeAnd(subNodes) : new MoveNodeOr(subNodes);
      compoundMoves.put(compoundName, compoundNode);
    }
  }

  private void addToPieceMoves(Map<String, String> pieces) {
    allMoves = new HashMap();
    allMoves.putAll(basicMoves);
    allMoves.putAll(compoundMoves);
    for (String piece: pieces.keySet()) {
      List pieceMoveList = new ArrayList<>();
      String move = pieces.get(piece);

      String[] moveArr = move.split(" ");
      String moveName = moveArr[0];
      String[] multiplierArr = moveArr[1].split(":");
      MoveNode unMultipliedMove = allMoves.get(moveName);

      for (int i = Integer.parseInt(multiplierArr[0]); i < Integer.parseInt(multiplierArr[1]); i++) {
        MoveNode pieceMove = unMultipliedMove.copy();
        pieceMove.multiply(i);
        pieceMoveList.add(pieceMove);
      }
      pieceMoves.put(piece, new MoveNodeOr(pieceMoveList));
    }
  }
  private String[] splitAndOr(String moveStr) {
    if (moveStr.contains(AND)) {
      return moveStr.split(AND);
    } else if (moveStr.contains(OR)) {
      return moveStr.split(OR);
    }
    return null;
  }

  private void clearAll() {
    dimensions.clear();
    pieceLocations.clear();
    pieceMoves.clear();
    pieceScores.clear();
  }
}
