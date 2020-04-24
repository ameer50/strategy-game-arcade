package ooga.json;

import com.google.common.collect.BiMap;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ooga.board.Board;
import ooga.board.Piece;
import ooga.custom.MoveNode;
import ooga.custom.MoveNodeAnd;
import ooga.custom.MoveNodeLeaf;
import ooga.custom.MoveNodeOr;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONProcessor {

  public static final String SETTINGS = "settings";
  public static final String NAME = "name";
  public static final String MOVES = "moves";
  public static final String PIECES = "pieces";
  public static final String BASIC = "basic";
  public static final String COMPOUND = "compound";
  public static final String LOCATIONS = "locations";
  public static final String SCORES = "scores";
  public static final String CUSTOM = "CUSTOM";
  public static final String ERROR_MSG = "Error parsing JSON.";
  public static final String AND = " AND ";
  public static final String OR = " OR ";
  protected String name;
  protected Map<String, String> settings;
  protected Map<Point2D, String> pieceLocations;
  protected Map<String, MoveNode> pieceMoveNodes;
  private Map<String, MoveNode> basicMoves;
  private Map<String, MoveNode> compoundMoves;
  private Map<String, MoveNode> allMoves;
  protected Map<String, String> pieceMovePatterns;
  protected Map<String, Integer> pieceScores;
  protected JSONObject jo;

  public JSONProcessor() {
    settings = new HashMap<>();
    pieceLocations = new HashMap<>();
    pieceMoveNodes = new HashMap<>();
    pieceMovePatterns = new HashMap<>();
    pieceScores = new HashMap<>();
  }

  public void parse(String dir) {
    clearAll();
    try {
      Object obj = new JSONParser().parse(new FileReader(dir));
      jo = (JSONObject) obj;
      settings = (Map) jo.get(SETTINGS);
      name = settings.get(NAME);
      parsePieceMoves();
      parsePieceScores();
      parsePieceLocations();
    } catch (ParseException | IOException e) {
      System.out.println(ERROR_MSG);
    }
  }

  public String getName() { return name; }
  public int getWidth() { return Math.toIntExact(Long.parseLong(settings.get("width"))); }
  public int getHeight() {
    return Math.toIntExact(Long.parseLong(settings.get("height")));
  }
  public Map<String, String> getSettings() { return Map.copyOf(settings); }
  public Map<Point2D, String> getPieceLocations() { return Map.copyOf(pieceLocations); }
  public Map<String, String> getPieceMovePatterns() { return Map.copyOf(pieceMovePatterns); }
  public Map<String, Integer> getPieceScores() { return Map.copyOf(pieceScores); }

  private void parsePieceScores() {
    Map<String, Long> scores = (Map) jo.get(SCORES);
    for (String piece: scores.keySet()) {
      Long toInt = scores.get(piece);
      pieceScores.put(piece, Math.toIntExact(toInt));
    }
  }

  private void parsePieceLocations() {
    Map<String, JSONArray> locations = (Map) jo.get(LOCATIONS);
    for (String pieceName: locations.keySet()) {
      JSONArray coordinates = locations.get(pieceName);
      for (int i=0; i<coordinates.size(); i++) {
        String coordinate = (String) coordinates.get(i);
        String[] coordinateArr = coordinate.split(", ");
        int x = Integer.parseInt(coordinateArr[0]);
        int y = Integer.parseInt(coordinateArr[1]);
        Point2D point = new Point2D.Double(x, y);
        pieceLocations.put(point, pieceName);
      }
    }
  }

  protected void parsePieceMoves() {
    Map<String, Map<String, String>> moves = (Map) jo.get(MOVES);
    pieceMovePatterns = moves.get(PIECES);
    if (name.toUpperCase().equals(CUSTOM)) {
      basicMoves = new HashMap<>();
      compoundMoves = new HashMap<>();
      generateBasicMoves(moves.get(BASIC));
      generateCompoundMoves(moves.get(COMPOUND));
      addToPieceMoves(moves.get(PIECES));
      for (String piece: pieceMoveNodes.keySet()) {
        pieceMovePatterns.put(piece, pieceMoveNodes.get(piece).toString());
        // System.out.println(String.format("put %s : %s", piece, pieceMoveNodes.get(piece).toString()));
      }
    }
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
      pieceMoveNodes.put(piece, new MoveNodeOr(pieceMoveList));
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

  protected void clearAll() {
    settings.clear();
    pieceLocations.clear();
    pieceMoveNodes.clear();
    pieceMovePatterns.clear();
    pieceScores.clear();
  }

  public void writeLocations(Board board, String filename) {
    jo.remove("locations");
    BiMap<Point2D, Piece> pieceBiMap = board.getPieceBiMap();
    Map locations = new LinkedHashMap();
    for (Point2D point : pieceBiMap.keySet()) {
        String piece = pieceBiMap.get(point).getFullName();
        locations.put(piece, String.format("%d, %d", (int) point.getX(), (int) point.getY()));
    }
    jo.put("locations", locations);

    try {
      PrintWriter writer = new PrintWriter(filename);
      writer.write(jo.toJSONString());
      writer.flush();
      writer.close();
    } catch (FileNotFoundException e) {
      System.out.println(String.format("Could not find file: %s", filename));
    }
  }
}
