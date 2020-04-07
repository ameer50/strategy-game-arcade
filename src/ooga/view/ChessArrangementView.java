package ooga.view;

import javafx.scene.image.ImageView;

import java.util.*;

public class ChessArrangementView implements ArrangementView {

    private PieceView[][] arrangement;
    private ImageView[] pieceImages;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static List<String> blackPieces;
    private static List<String> whitePieces;
    private String playerChoice;
    private int pieceHeight;
    private int pieceWidth;
    private int dimension;


    public ChessArrangementView(int dimension, int pieceWidth, int pieceHeight, String playerChoice){
        this.dimension = dimension;
        arrangement = new PieceView[dimension][dimension];
        pieceImages = new ImageView[4*dimension];
        this.playerChoice = playerChoice;
        this.pieceWidth = pieceWidth;
        this.pieceHeight = pieceHeight;
        blackPieces = new ArrayList(Arrays.asList(new String[]{"BlackRook", "BlackKnight", "BlackBishop", "BlackQueen", "BlackKing",
                "BlackBishop", "BlackKnight", "BlackRook", "BlackPawn", "BlackPawn", "BlackPawn", "BlackPawn",
                "BlackPawn", "BlackPawn", "BlackPawn", "BlackPawn"}));
        whitePieces = new ArrayList(Arrays.asList(new String[]{"WhiteRook", "WhiteKnight", "WhiteBishop", "WhiteQueen", "WhiteKing",
                "WhiteBishop", "WhiteKnight", "WhiteRook", "WhitePawn", "WhitePawn", "WhitePawn", "WhitePawn",
                "WhitePawn", "WhitePawn", "WhitePawn", "WhitePawn"}));
        initialize();
    }

    @Override
    public void initialize() {
        String[] pieceOrder;
        if(playerChoice.equals("black")){
            Collections.reverse(blackPieces);
            whitePieces.addAll(blackPieces);
            pieceOrder = whitePieces.toArray(new String[4*dimension]);
        }else{
            Collections.reverse(whitePieces);
            blackPieces.addAll(whitePieces);
            pieceOrder = blackPieces.toArray(new String[4*dimension]);
        }
        int pc = 0; // piece count to assing the proper order of pieces
        for(int i =0; i < 2; i++){
            for(int j =0; j < arrangement.length; j++){
                arrangement[i][j] = new PieceView(115 + 70*j, 100 + 70*i, pieceWidth, pieceHeight, res.getString(pieceOrder[pc]));
                pieceImages[pc] = arrangement[i][j].getIVShape();
                pc++;
            }
        }

        for(int i =0; i < 2; i++){
            for(int j =0; j < arrangement.length; j++){
                arrangement[i][j] = new PieceView(115 + 70*j, 100 + 410 + 70*i, pieceWidth, pieceHeight, res.getString(pieceOrder[pc]));
                pieceImages[pc] = arrangement[i][j].getIVShape();
                pc++;
            }
        }

    }

    @Override
    public ImageView[] gamePieces() {
        return pieceImages;
    }


}
