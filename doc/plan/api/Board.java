import java.util.List;

public interface Board {
    /**
     * Check board to see if the game has been completed and a winner has been found
     *
     * @return if there is a winner
     **/
    boolean checkWon();

    /**
     * Find valid moves from selected cell
     *
     * @param x x coordinate of the cell
     * @param y y coordinate of the cell
     * @return potential moves of the piece at cell x,y
     **/
    List<String> getValidMoves(int x, int y);

    /**
     Get piece at the specified coordinates
     @return piece object at x, y; null if empty cell
     **/
    //Piece getPieceAt(int x, int y);

    /**
     * Execute the desired move
     *
     * @param x    x position of origin of move
     * @param y    y position of origin of move
     * @param move move to be executed through reflection
     * @return score from completing this move
     * @throws NoSuchMethodException if unknown move type is requested
     **/
    double doMove(int x, int y, String move) throws NoSuchMethodException;

    /**
     * Set up board from config file
     **/
    void initStartingPieces();

    /**
     * Check if coordinates are valid on board
     *
     * @param x potential x coord
     * @param y potential y coord
     * @return true if valid coordinate
     **/
    boolean isValidCell(int x, int y);
}