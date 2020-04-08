package ooga.view;

import javafx.scene.image.ImageView;
import java.awt.geom.Point2D;
import java.util.*;

public class ChessArrangementView implements ArrangementView {

    private PieceView[][] arrangement;
    private ImageView[] pieceImages;
    private ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static List<String> blackPieces;
    private static List<String> whitePieces;
    private String playerChoice; // either 'Black' or 'White'
    private int pieceHeight;
    private int pieceWidth;
    private Map<Point2D, String> pieceLocation;


    public ChessArrangementView(int dimension, int pieceWidth, int pieceHeight, String playerChoice){
        arrangement = new PieceView[dimension][dimension];
        pieceImages = new ImageView[4*dimension];
        this.playerChoice = playerChoice;
        this.pieceWidth = pieceWidth;
        this.pieceHeight = pieceHeight;
        this.pieceLocation = new HashMap<>();
        initialize();
    }

    @Override
    public void initialize() {
        initializeDefaultLocations();
        fillArrangement();
    }

    @Override
    public ImageView[] gamePieces() {
        return pieceImages;
    }

    private void initializeDefaultLocations(){
        ResourceBundle defaultLocations = ResourceBundle.getBundle("default" + playerChoice + "Loc", Locale.getDefault());
        for (String key: Collections.list(defaultLocations.getKeys())) {
            String[] coord = key.split(",");
            pieceLocation.put(new Point2D.Double(Double.parseDouble(coord[0]), Double.parseDouble(coord[1])), defaultLocations.getString(key));
        }
    }

    private void fillArrangement() {
        int pc = 0;
        for (Point2D point : pieceLocation.keySet()) {
            int x = (int) point.getX();
            int y = (int) point.getY();
            arrangement[x][y] = new PieceView(115 + 70 * y, 110 + 70 * x, pieceWidth, pieceHeight, res.getString(pieceLocation.get(point)));
            pieceImages[pc] = arrangement[x][y].getIVShape();
            pc++;
        }
    }
}
