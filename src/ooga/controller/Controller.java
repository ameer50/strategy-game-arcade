package ooga.controller;

import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.board.Piece;
import ooga.view.GameScreen;
import ooga.view.SplashScreen;
import ooga.xml.XMLParser;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class Controller {

    private GameScreen myGameScreen;
    private SplashScreen mySplashScreen;
    private Board myBoard;
    private boolean toggleMoves;
    private List<Point2D> temp;

    public Controller (Stage stage) {
        makeScreen(stage);
    }

    public void makeScreen (Stage stage) {
        XMLParser p = new XMLParser();

        p.parse("resources/test_xml/test.xml");
        myBoard = new ChessBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatternsAndValues());
        //myBoard.print();
        myGameScreen = new GameScreen(stage, p.getSettings(), p.getInitialPieceLocations());
        toggleMoves = true;


//        mySplashScreen = new SplashScreen(stage);
//        myScreen.onGameSelection((String file) -> {
//            XMLParser p = new XMLParser();
//            p.parse(file);
//            myGameScreen = new GameScreen(stage);
//            initializeBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatterns());
//        }
        myGameScreen.onPieceClicked((int x, int y) -> {
            myGameScreen.highlightValidMoves(myBoard.getValidMoves(x, y));
        });

        myGameScreen.onMoveClicked((int x, int y) -> {
            //myGameScreen.movePiece(myBoard.doMove(x, y));
            //myGameScreen.movePiece(6, 0, 5, 0);
        });
    }

    public void initializeBoard(Map<String, String> settings, Map<Point2D, String> pieceLocations, Map<String, String> movePatterns) {

        // myBoard.initialize(settings, pieceLocations, movePatterns);
    }



}
