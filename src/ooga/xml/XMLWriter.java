package ooga.xml;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ooga.board.Board;
import ooga.board.Piece;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class XMLWriter {

    private Document doc;
    private String fileName;

    /**
     * Creates a new XMLWriter
     *
     * @param fileName The name of the file to copy
     */
    public XMLWriter(String fileName) {
        this.fileName = fileName;
        loadEmptyDoc();
    }

    /**
     * Load the original doc but make the initial states empty
     */
    private void loadEmptyDoc() {

        File file = new File(fileName);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return;
        }
        doc = null;
        try {
            doc = Objects.requireNonNull(dBuilder).parse(file);
        } catch (SAXException | IOException e) {
            return;
        }
        doc.getDocumentElement().normalize();

        Node node = doc.getDocumentElement().getElementsByTagName("item").item(0);
        //  System.out.println(
//    doc.getDocumentElement().getElementsByTagName("initialstates").item(0).getChildNodes()
//        .getLength());
        //  System.out.println(node);
        doc.getDocumentElement().removeChild(node);
    }

    /**
     * Write in the values from a grid
     *
     * @param board The grid to write the values from
     */
    public void writePresets(Board board, String saveName) {
        if (doc == null) {
            System.out.println("Failed to save");
            return;
        }
        Element root = doc.getDocumentElement();
        Element newList = doc.createElement("locations");
        newList.setAttribute("item", "List");
        root.appendChild(newList);
        for (Point2D location : board.getPieceLocationBiMap().keySet()) {
            Piece thePiece = board.getPieceLocationBiMap().get(location);
            Element newItem = doc.createElement("item");
            newItem.setAttribute("item", "String");
            newItem.setTextContent(
                    String.format(location.getX() + "," + location.getY() + ",%s", thePiece.getFullName()));
            newList.appendChild(newItem);
        }

        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer out = new StringWriter();

            FileWriter writer = new FileWriter(new File(saveName));
            StreamResult fileOut = new StreamResult(writer);
            tf.transform(new DOMSource(doc), fileOut);
            writer.flush();
            writer.close();

        } catch (TransformerException | IOException ignored) {
        }
    }
}
