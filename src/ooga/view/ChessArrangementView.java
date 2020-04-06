package ooga.view;

import javafx.scene.image.ImageView;

import java.util.*;

public class ChessArrangementView extends PieceArrangementView {

    private PieceView[][] arrangement;
    private ImageView[] pieceImages;
    private static ResourceBundle res = ResourceBundle.getBundle("resources", Locale.getDefault());
    private static final int BOARD_HEIGHT = 8;
    private static final int BOARD_WIDTH = 8;
    private static List<String> blackPieces;
    private static List<String> whitePieces;
    private String playerChoice;

    public ChessArrangementView(int boardHeight, int boardWidth, int pieceHeight, int pieceWidth, String playerChoice){
        super( boardHeight, boardWidth, pieceHeight, pieceWidth, playerChoice);
        arrangement = new PieceView[BOARD_WIDTH][BOARD_HEIGHT];
        pieceImages = new ImageView[32];
        this.playerChoice = playerChoice;
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
            pieceOrder = whitePieces.toArray(new String[32]);
        }else{
            Collections.reverse(whitePieces);
            blackPieces.addAll(whitePieces);
            pieceOrder = blackPieces.toArray(new String[32]);
        }
        int pc = 0; // piece count to assing the proper order of pieces
        System.out.println(Arrays.toString(pieceOrder));
        for(int i =0; i < 2; i++){
            for(int j =0; j < arrangement.length; j++){
                arrangement[i][j] = new PieceView(115 + 70*j, 100 + 70*i, 45, 75, res.getString(pieceOrder[pc]));
                pieceImages[pc] = arrangement[i][j].getIVShape();
                pc++;
            }
        }

        for(int i =0; i < 2; i++){
            for(int j =0; j < arrangement.length; j++){
                arrangement[i][j] = new PieceView(115 + 70*j, 100 + 410 + 70*i, 45, 75, res.getString(pieceOrder[pc]));
                pieceImages[pc] = arrangement[i][j].getIVShape();
                pc++;
            }
        }
        System.out.println(pc);
        System.out.println(Arrays.toString(pieceImages));

    }

    @Override
    public ImageView[] gamePieces() {
        return pieceImages;
    }

    public ImageView bruh(){
        return pieceImages[0];
    }

}
