public interface GameView {
    /**
     * When winner has been determined, display the result of the game on the screen
     **/
    void displayWinner();

    /**
     * Display board on screen
     **/
    void displayBoard();

    /**
     * Put pieces onto the board
     **/
    void displayPieces();
}