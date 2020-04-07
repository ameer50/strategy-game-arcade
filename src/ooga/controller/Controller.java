package ooga.controller;

import javafx.stage.Stage;
import ooga.view.GameScreen;
import ooga.view.SplashScreen;
import ooga.xml.XMLParser;

import java.awt.geom.Point2D;
import java.util.Map;

public class Controller {

    private GameScreen myGameScreen;
    private SplashScreen mySplashScreen;
    //private Board myBoard;

    public Controller (Stage stage) {
        makeScreen(stage);
    }

    public void makeScreen (Stage stage) {
        XMLParser p = new XMLParser();
        p.parse("resources/test_xml/test.xml");
//        myGameScreen = new GameScreen(stage, p.getSettings(), p.getInitialPieceLocations());
//        mySplashScreen = new SplashScreen(stage);
//        myScreen.onGameSelection((String file) -> {
//            XMLParser p = new XMLParser();
//            p.parse(file);
//            myGameScreen = new GameScreen(stage);
//            initializeBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatterns());
//        }
//        myScreen.onPieceClicked((int x, int y) -> {
//            myScreen.highlightPossibleMoves(myBoard.getValidMoves(x, y));
//        });
    }

    public void initializeBoard(Map<String, String> settings, Map<Point2D, String> pieceLocations, Map<String, String> movePatterns) {
        // myBoard.initialize(settings, pieceLocations, movePatterns);
    }
}
