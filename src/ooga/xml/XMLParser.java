package ooga.xml;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class XMLParser {

    private Map<String, String> settings;
    private Map<Point2D, String> initialPieceLocations;
    private Map<String, Pair<String, Integer>> movePatterns;
    private final static String ERROR_MESSAGE = "Error parsing XML file.";

    public XMLParser() {
        settings = new HashMap<>();
        initialPieceLocations = new HashMap<>();
        movePatterns = new HashMap<>();
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public Map<Point2D, String> getInitialPieceLocations() {
        return initialPieceLocations;
    }

    public Map<String, Pair<String, Integer>> getMovePatterns() {
        return movePatterns;
    }

    public void parse(String filename) {
        settings.clear();
        initialPieceLocations.clear();
        movePatterns.clear();
        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            Node node = rootElement.getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeString = node.getNodeName();
                    Method parseMethod = this.getClass().getDeclaredMethod(String.format("parse%s",
                        capitalize(nodeString)), Node.class);
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
        System.out.println(movePatterns);
    }

    private String capitalize(String str) {
        String capital = str.substring(0, 1).toUpperCase();
        String lowercase = str.substring(1);
        return String.format("%s%s", capital, lowercase);
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
                String info = node.getTextContent().strip();
                String[] arr = info.split(",");
                Point2D.Double location = new Point2D.Double(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                initialPieceLocations.put(location, arr[2]);
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
                movePatterns.put(arr[0], new Pair<>(arr[1], Integer.parseInt(arr[2])));
            }
            node = node.getNextSibling();
        }
    }
}
