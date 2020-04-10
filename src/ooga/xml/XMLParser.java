package ooga.xml;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class XMLParser {

    private Map<String, String> settings;
    private Map<Point2D, String> initialPieceLocations;
    private Map<String, Pair<String, Double>> movePatternsAndValues;
    private final static String PARSE_STRING = "parse";
    private final static String ERROR_MESSAGE = "Error parsing XML file.";

    public XMLParser() {
        settings = new HashMap<>();
        initialPieceLocations = new HashMap<>();
        movePatternsAndValues = new HashMap<>();
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public Map<Point2D, String> getInitialPieceLocations() {
        return initialPieceLocations;
    }

    public Map<String, Pair<String, Double>> getMovePatterns() {
        return movePatternsAndValues;
    }

    public void parse(String filename) {
        settings.clear();
        initialPieceLocations.clear();
        movePatternsAndValues.clear();
        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            Node node = rootElement.getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeAsString = node.getNodeName();
                    Method parseMethod = this.getClass().getDeclaredMethod(PARSE_STRING + nodeAsString.substring(0, 1).toUpperCase() + nodeAsString.substring(1), Node.class);
                    parseMethod.invoke(this, node);
//                  Field f = this.getClass().getDeclaredField(node.getTextContent());
//                  Node childNode = node.getFirstChild();
//                  while (childNode != null) {
//                      ((Map<String, String>) f.get(this)).put(childNode.getNodeName(), childNode.getTextContent());
//                      childNode = childNode.getNextSibling();
//                  }
                }
                node = node.getNextSibling();
            }
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE);
        }
        System.out.println(settings);
        System.out.println(initialPieceLocations);
        System.out.println(movePatternsAndValues);
    }

    private void parseSettings(Node node) {
        node = node.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                settings.put(node.getNodeName(), node.getTextContent().strip());
            }
            node = node.getNextSibling();
        }
    }

    private void parseLocations(Node node) {
        node = node.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String locationAndPiece = node.getTextContent().strip();
                String[] arr = locationAndPiece.split(",");
                initialPieceLocations.put(new Point2D.Double(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])), arr[2]);
            }
            node = node.getNextSibling();
        }
    }

    private void parsePieces(Node node) {
        node = node.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String pieceAndPattern = node.getTextContent().strip();
                String[] arr = pieceAndPattern.split(":");
                movePatternsAndValues.put(arr[0], new Pair<>(arr[1], Double.parseDouble(arr[2])));
            }
            node = node.getNextSibling();
        }
    }
}
