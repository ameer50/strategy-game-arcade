package ooga.controller;

import javafx.stage.Stage;
import ooga.board.Board;
import ooga.board.ChessBoard;
import ooga.view.BoardView;
import ooga.view.GameScreen;
import ooga.view.MenuScreen;
import ooga.xml.XMLParser;

import java.awt.geom.Point2D;
import java.util.List;

public class Controller {

    private GameScreen myGameScreen;
    private MenuScreen menuScreen;
    private Board myBoard;
    private BoardView myBoardView;
    private boolean toggleMoves;
    private List<Point2D> temp;

    public Controller (Stage stage) {


        menuScreen = new MenuScreen(stage);

        menuScreen.buttonListener(e -> {
            makeScreen(stage, menuScreen.getGameType());

        });

    }

    public void makeScreen (Stage stage, String gameType) {

        String file = "resources/defaultGames/" + gameType + ".xml";
        XMLParser p = new XMLParser();
        //p.parse("resources/test_xml/Chess.xml");
        p.parse(file);
        myBoard = new ChessBoard(p.getSettings(), p.getInitialPieceLocations(), p.getMovePatternsAndValues());
        //myBoard.print();
        myGameScreen = new GameScreen(stage, p.getSettings(), p.getInitialPieceLocations());
        myBoardView = myGameScreen.getBoard();
        toggleMoves = true;
        listen();

    }

    private void listen(){
        myBoardView.setOnPieceClicked((int x, int y) -> {
            myBoardView.setSelectedLocation(x, y);
            myBoardView.highlightValidMoves(myBoard.getValidMoves(x, y));
        });

        myBoardView.setOnMoveClicked((int x, int y) -> {
            myBoard.doMove((int) myBoardView.getSelectedLocation().getX(), (int) myBoardView.getSelectedLocation().getY(), x, y);
            myBoardView.movePiece(x, y);
        });
    }

}
