package game.views
public interface GameView{
	/**
	When winner has been determined, display the result of the game on the screen
	**/
	public void displayWinner();

	/**
	Display board on screen
	**/
	public void displayBoard();

	/**
	Put pieces onto the board
	**/
	public void displayPieces();
}