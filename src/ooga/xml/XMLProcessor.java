package ooga.xml;

import javafx.util.Pair;
import ooga.board.Board;
import ooga.board.Piece;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class XMLProcessor {

    private Map<String, String> settings;
    private Map<Point2D, String> initialPieceLocations;
    private Map<String, Pair<String, Integer>> movePatterns;
    private static final String ERROR_MESSAGE = "Error parsing XML file.";
    private Document doc;

    public XMLProcessor() {
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
            doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            Node node = rootElement.getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeString = node.getNodeName();
                    Method parseMethod = this.getClass().getDeclaredMethod(String.format("parse%s",
                        capitalize(nodeString)), Node.class);
                    parseMethod.invoke(this, node);
                }
                node = node.getNextSibling();
            }
        } catch (Exception e) {
            System.out.println(ERROR_MESSAGE);
        }
    }

    public void write(Board board, String fileName) {
        Node node = doc.getDocumentElement().getElementsByTagName("locations").item(0);
        doc.getDocumentElement().removeChild(node);

        Element root = doc.getDocumentElement();
        Element newList = doc.createElement("locations");
        for (Point2D location : board.getPieceBiMap().keySet()) {
            Piece thePiece = board.getPieceBiMap().get(location);
            Element newItem = doc.createElement("item");
            newItem.setTextContent(
                    String.format("%d,%d,%s", (int) location.getX(), (int) location.getY(), thePiece.getFullName()));
            newList.appendChild(newItem);
        }
        root.appendChild(newList);

        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");

            FileWriter writer = new FileWriter(new File(fileName));
            StreamResult fileOut = new StreamResult(writer);
            tf.transform(new DOMSource(doc), fileOut);
            writer.flush();
            writer.close();

        } catch (TransformerException | IOException ignored) {
        }
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
