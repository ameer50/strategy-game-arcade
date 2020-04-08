package ooga.view;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class ChessArrangementView implements ArrangementView {

    private PieceView[][] arrangement;
    private List<ImageView> pieceImages;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static List<String> blackPieces;
    private static List<String> whitePieces;
    private String playerChoice; // either 'Black' or 'White'
    private int pieceHeight;
    private int pieceWidth;
    private Map<Point2D, String> pieceLocations;


    public ChessArrangementView(int dimension, int pieceWidth, int pieceHeight, String playerChoice, Map<Point2D, String> locs){
        arrangement = new PieceView[dimension][dimension];
        pieceImages = new ArrayList<>();
        this.playerChoice = playerChoice;
        this.pieceWidth = pieceWidth;
        this.pieceHeight = pieceHeight;
        this.pieceLocations = locs;
        initialize();
    }

    @Override
    public void initialize() {
        for (Point2D point : pieceLocations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            arrangement[x][y] = new PieceView(115 + 70 * y, 110 + 70 * x, pieceWidth, pieceHeight, res.getString(pieceLocations.get(point)));
            pieceImages.add(arrangement[x][y].getIVShape());
        }
    }

//    public void initializeFromXML(Map<Point2D, String> locs) {
//        pieceLocations = locs;
//        fillArrangement();
//    }

    @Override
    public ImageView[] gamePieces() {
        return pieceImages.toArray(new ImageView[0]);
    }

//    private void initializeDefaultLocations(){
//        ResourceBundle defaultLocations = ResourceBundle.getBundle("default" + playerChoice + "Loc", Locale.getDefault());
//        for (String key: Collections.list(defaultLocations.getKeys())) {
//            String[] coord = key.split(",");
//            pieceLocation.put(new Point2D.Double(Double.parseDouble(coord[0]), Double.parseDouble(coord[1])), defaultLocations.getString(key));
//        }
//    }

    private void fillArrangement() {
        for (Point2D point : pieceLocations.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            arrangement[x][y] = new PieceView(115 + 70 * y, 110 + 70 * x, pieceWidth, pieceHeight, res.getString(pieceLocations.get(point)));
            pieceImages.add(arrangement[x][y].getIVShape());
        }
    }
}
